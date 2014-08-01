/**
 * 
 */
package hurricane.server;

import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
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
	protected Session session;
	private Set<HurricaneServerSocket> sockets;
	
	public HurricaneServerSocket() {
		this("annonymous",null);
	}
	
	/**Basic constructor. Called from {@link HurricaneServlet} or {@link HurricaneServlet#init()}.<br>
	 * Name the socket with the given nickname, and direct to the Set of {@link HurricaneServerSocket}.
	 * @param nickname
	 * @param sockets
	 */
	public HurricaneServerSocket(String nickname, Set<HurricaneServerSocket> sockets) {
		this.nickname = nickname;
		this.sockets = sockets;
	}


	/**Handler of OnConnect event.
	 * @param session
	 */
	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
		this.sockets.add(this);
	}
	
	/**Handler of OnMessage event.
	 * @param message
	 */
	@OnWebSocketMessage
	public void onMessage(String message) {
//		HurricaneHandler.sendMessageByPush(message, user);
	}
	
	/**Push a Hurricane Message to the remote of this socket.<br>
	 * Basic hurricane transaction strategy would be: <br>
	 * - if the dest user is currently connected (socket is held in the {@link #sockets} Set), push the message through the socket.<br>
	 * - if the dest user is currently offline, push the message through Google/Apple push notification service.<br>
	 * Obviously this method handles former. Latter would be handled by {@link HurricaneServlet#sendMessageByPush(String, String)}.<br>
	 * @param message
	 * @return isDone
	 */
	public boolean sendMessage(String message) {
		boolean isDone = false;
		Future<Void> fut = this.session.getRemote().sendStringByFuture(message);
		try {
			fut.get(500, TimeUnit.MILLISECONDS);
			if (fut.isDone()) {
				isDone = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return isDone;
		}
		return isDone;
	}

}
