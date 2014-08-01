/**
 * 
 */
package hurricane.server.embedded;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 * @author Romancer
 *
 */
@WebSocket
public class SimpleWebSocketHandler {
	
	@OnWebSocketConnect
	public void onConnect(Session session) {
		System.out.println("Connected: " + session.getRemoteAddress().toString());
		try {
			session.getRemote().sendString("Yo");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		System.out.printf("Disconnected. (%d - %s)\n",statusCode, reason);
	}

}
