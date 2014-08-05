/**
 * 
 */
package hurricane.server;

import hurricane.message.HurricaneMessage;
import hurricane.server.embedded.HurricaneHandler;

import java.io.IOException;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * @author Romancer
 *
 */
@WebSocket(maxTextMessageSize = 128)
public class HurricaneServerSocket {
	
	protected String nickname;
	private String sendTo;
	private HurricaneServerSocket sendToSocket = null;
	protected Session session = null;
	private Set<HurricaneServerSocket> sockets;
	
//	private double RANDOM_REPLY_WINDOW = 3.0;
	public static final String[] NORMAL_SOCK = {
			HurricaneMessage.SOCK_HURRICANE,
			HurricaneMessage.SOCK_GO,
			HurricaneMessage.SOCK_NO,
			HurricaneMessage.SOCK_NUMERIC};
	
	
	public HurricaneServerSocket() {
		this("annonymous",null);
	}
	
	/**Basic constructor. Called from {@link HurricaneServlet} or {@link HurricaneServlet#init()}.<br>
	 * Alternately, this may be called from {@link HurricaneHandler} in the embedded server.<br>
	 * Name the socket with the given nickname, and direct to the Set of {@link HurricaneServerSocket}.
	 * @param nickname
	 * @param sockets
	 */
	public HurricaneServerSocket(String nickname, Set<HurricaneServerSocket> sockets) {
		this.nickname = nickname;
		this.sockets = sockets;
		if (this.nickname.equals("hurry")) {
			this.sockets.add(this);
			System.out.printf("Dummy user [%s] initiated.\n", this.nickname);
		} else {
			System.out.printf("[%s] requested WebSocket connection.\n", this.nickname);
		}
		//TODO implement DB registration which records ever-existed usernames.
	}


	/**Handler of OnConnect event.
	 * @param session
	 */
	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
		this.sockets.add(this);
		System.out.printf("WebSocket connection established.\n");
	}
	
	/**Handler of OnClose event.<br>
	 * 
	 * @param session
	 * @param statusCode
	 * @param reason
	 */
	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		switch(statusCode) {
		case StatusCode.NORMAL :
			//to be implemented
			this.session = null;
			this.sockets.remove(this);
			System.out.printf("Connection to [%s] closed. [%d - %s]\n", this.nickname, statusCode, reason);
			return;
		case StatusCode.SERVER_ERROR :
			//to be implemented
			this.session = null;
			this.sockets.remove(this);
			System.out.printf("Connection to [%s] closed. [%d - %s]\n", this.nickname, statusCode, reason);
			return;
		default :
			//to be implemented
			this.session = null;
			this.sockets.remove(this);
			System.out.printf("Connection to [%s] closed. [%d - %s]\n", this.nickname, statusCode, reason);
			return;
		}
	}
	
	/**Handler of OnMessage event.<br>
	 * There are "special" Hurricane Text Messages and normal Hurricane Text Messages.<br>
	 * Currently, the only special one is "u" method, which handles determination of a target user.<br>
	 * The others are actual messages being transferred. They should be simply passed to the target.
	 * @param message
	 */
	@OnWebSocketMessage
	public void onMessage(String message) {
		try {
			this.session.getRemote().sendString(HurricaneMessage.SOCK_ACK);
			this.assortTextMessageFromRemote(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**Assort Hurricane Text Message into specials ("u") and normals. Process accordingly.<br>
	 * This private method is designed to be called in the onMessage event handler method.<br>
	 * Normal messages are simply passed to the target socket.
	 * @param text
	 */
	private void assortTextMessageFromRemote(String text) {
		String[] splitText = text.split("\\s");
		if (splitText[0].equals(HurricaneMessage.SOCK_USER)) {
			this.setSendTo(splitText);
		} else {
			if (this.sendToSocket != null) {
				System.out.printf("[%s]>[%s]: %s\n", this.nickname, this.sendTo, text);
//				if (this.session.isOpen()) {
//					this.session.getRemote().sendString(text, null);		//debug echoing
//				}
				this.sendToSocket.sendMessageToRemote(text, this);
//				System.out.printf("[%s]>[%s]: %s\n", this.nickname, this.sendTo, text);
			}
		}
	}
	
	/**Set a target user of this Hurricane socket.<br>
	 * If a specified user does not exist in the user set ({@link #sockets}),
	 * this method replies "u [username] 404" to the client remote endpoint,
	 * which indicates there is no such user.<br>
	 * If exists, replies "u [username]".
	 * @param splitText
	 */
	private void setSendTo(String[] splitTextMessageU) {
		String method = HurricaneMessage.SOCK_USER;
		String sendToUserName = splitTextMessageU[1];
		String optNum = "";
		String message = "";
		boolean found = false;
		
		System.out.printf("[%s] called a user [%s].\n", this.nickname, sendToUserName);
		
		for (HurricaneServerSocket socket : this.sockets) { 
			if (socket.equals(sendToUserName)) {
				this.sendToSocket = socket;
				this.sendTo = sendToUserName;
				found = true;
			}
		}
		if (!found) {
			//TODO implement DB search for ever-existed users.
		}
		if (found) {
			message = method
					+ HurricaneMessage.TEXT_DELIM + sendToUserName;
			this.session.getRemote().sendStringByFuture(message);
			System.out.printf("User [%s] found. [%s] communicate with [%s].\n", sendToUserName, this.nickname, sendToUserName);
		} else {
			optNum = "404";
			message = method
					+ HurricaneMessage.TEXT_DELIM + sendToUserName
					+ HurricaneMessage.TEXT_DELIM + optNum;
			this.session.getRemote().sendStringByFuture(message);
			System.err.printf("User [%s] not found.\n", sendToUserName);
		}
	}

	/**Push a Hurricane Message to the remote of this socket.<br>
	 * Basic hurricane transaction strategy would be: <br>
	 * - if the dest user is currently connected (socket is held in the {@link #sockets} Set), push the message through the socket.<br>
	 * - if the dest user is currently offline, push the message through Google/Apple push notification service.<br>
	 * Obviously this method handles former. Latter would be handled by {@link HurricaneServlet#sendMessageByPush(String, String)}.<br>
	 * @param message
	 * @return isDone
	 */
	public boolean sendMessageToRemote(String message, HurricaneServerSocket sourceSocket) {
		boolean isDone = false;
		if (this.session != null) {
//			Future<Void> fut = this.session.getRemote().sendStringByFuture(message);
			this.session.getRemote().sendStringByFuture(message);
//			try {
//				//fut.get(500, TimeUnit.MILLISECONDS);
//				if (fut.isDone()) {
//					isDone = true;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				return isDone;
//			}
		} else if (this.equals("hurry")) {
			// dummy user function
			// immitate "replying behavior".
//			double rndSec = Math.random() * RANDOM_REPLY_WINDOW ;
//			long rndMillisec = (long) (rndSec * 1000);
			
			double rndMethod = Math.random() * 4.0;
			int rndNum = (int) (Math.random() * 99999999.0);
			String text = "", method = "";
			String optNum = String.valueOf(rndNum);
			for (int i = 0; i < rndMethod; i++) {
				method = NORMAL_SOCK[i];
			}
			text = method + HurricaneMessage.TEXT_DELIM + optNum;
			
//			try {
//				Thread.sleep(rndMillisec);			//millisec
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
			double roll = Math.random();
			
			if (roll > 0.5) { // randomly replies
				System.out.printf("[%s]>[%s]: %s\n", this.nickname, sourceSocket.nickname, text);
				isDone = sourceSocket.sendMessageToRemote(text, this);
			}
		}
		return isDone;
	}
	
	public boolean equals(String username) {
		if (this.nickname.equals(username)) {
			return true;
		}
		return false;
	}

}
