/**
 * 
 */
package hurricane.server.embedded;

import hurricane.server.HurricaneCreator;
import hurricane.server.HurricaneServerSocket;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * @author Romancer
 *
 */
public class HurricaneHandler extends WebSocketHandler {
	
	private Set<HurricaneServerSocket> sockets;
	
	protected HurricaneServerSocket hurry;

	/**
	 * 
	 */
	public HurricaneHandler() {
		this.sockets = new CopyOnWriteArraySet<HurricaneServerSocket>();
		this.hurry = new HurricaneServerSocket("hurry", sockets);
	}

	/* (非 Javadoc)
	 * @see org.eclipse.jetty.websocket.server.WebSocketHandler#configure(org.eclipse.jetty.websocket.servlet.WebSocketServletFactory)
	 */
	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.getPolicy().setIdleTimeout(600000);
//		factory.register(HurricaneServerSocket.class);
		factory.setCreator(new HurricaneCreator(this.sockets));
	}

	/**Send Hurricane Message to the given user through Google/Apple push notification service.
	 * @param message
	 * @param user
	 * @return isDOne
	 */
	public static boolean sendMessageByPush(String message, String user) {
		boolean isDone = false;
		//TODO
		return isDone;
	}
}
