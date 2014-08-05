/**
 * 
 */
package hurricane.server;

import java.util.Set;

import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;

/**
 * @author Romancer
 *
 */
public class HurricaneCreator extends WebSocketServerFactory {
	
	private Set<HurricaneServerSocket> sockets;

	public HurricaneCreator(Set<HurricaneServerSocket> sockets) {
		this.sockets = sockets;
//		System.out.println("Running HurricaneCreator...");
	}

	/* (非 Javadoc)
	 * @see org.eclipse.jetty.websocket.servlet.WebSocketCreator#createWebSocket(org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest, org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse)
	 */
	@Override
	public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse res) {
//		for(Entry<String, List<String>> headerEntry : req.getHeaders().entrySet()) {	// for debug
//			System.out.print(headerEntry.getKey());
//			for (String value : headerEntry.getValue()) {
//				System.out.print("\t");
//				System.out.println(value);
//			}
//		}
		String nickname = req.getHeader("user");
		return new HurricaneServerSocket(nickname, this.sockets);
	}

}
