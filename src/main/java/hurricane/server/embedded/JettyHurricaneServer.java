/**
 * 
 */
package hurricane.server.embedded;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;

/**
 * @author Romancer
 *
 */
public class JettyHurricaneServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		Server hurricaneServer = new Server(8080);
		
		
		HurricaneHandler hurricaneHandler = new HurricaneHandler();
//		SimpleHandler simpleHandler = new SimpleHandler();
//		WebSocketHandler wsHandler = new WebSocketHandler() {
//			
//			@Override
//			public void configure(WebSocketServletFactory factory) {
//				factory.register(SimpleWebSocketHandler.class);
//			}
//		};
		
		ContextHandler hurricaneContext = new ContextHandler("/hurricane");
		hurricaneContext.setDisplayName("JettyHurricaneServer");
		hurricaneContext.setResourceBase(".");
		hurricaneContext.setAllowNullPathInfo(true);									//required to accept both "/context" and "/context/" uri
		hurricaneContext.setClassLoader(Thread.currentThread().getContextClassLoader());
		hurricaneContext.setHandler(hurricaneHandler);
//		hurricaneContext.setHandler(simpleHandler);
//		hurricaneContext.setHandler(wsHandler);

//		ContextHandler context = new ContextHandler();
//		context.setContextPath("/hurricane");
//		context.setAllowNullPathInfo(true);
//		context.setResourceBase(".");
//		context.setClassLoader(Thread.currentThread().getContextClassLoader());
//		context.setHandler(wsHandler);
		
//		hurricaneServer.setHandler(context);
		hurricaneServer.setHandler(hurricaneContext);
//		hurricaneServer.setHandler(wsHandler);
		
		hurricaneServer.start();
		hurricaneServer.join();

	}

}
