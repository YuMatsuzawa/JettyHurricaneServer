/**
 * 
 */
package hurricane.server;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
//
//import javax.servlet.annotation.WebServlet;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**Servlet class of the Hurricane service.<br>
 * Run on the Jetty application server.
 * @author YuMatsuzawa
 *
 */
//@WebServlet(name = "Hurricane Servlet", urlPatterns = { "/hurricane" })
public class HurricaneServlet extends WebSocketServlet {
	
	private Set<HurricaneServerSocket> sockets = new CopyOnWriteArraySet<HurricaneServerSocket>();
	
	protected HurricaneServerSocket hurry = new HurricaneServerSocket("Hurry", sockets);
	
	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.getPolicy().setIdleTimeout(10000);
//		factory.register(HurricaneServerSocket.class);
		factory.setCreator(new HurricaneCreator(sockets));
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
