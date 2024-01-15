package edu.usfca.cs272;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * 
 * @author Amanda Wang CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 *
 */
public class SearchEngineServer {
	/**
	 * port number that user provided
	 */
	private int port;
	
	/**
	 * threaded version of Inverted Index
	 */
	private final ThreadedInvertedIndex threadedIndex;
	
	/**
	 * Initializes port, workers, threadedIndex
	 * 
	 * @param port port number that user provided
	 * @param threadedIndex threaded version of inverted index
	 */
	public SearchEngineServer(int port, ThreadedInvertedIndex threadedIndex) {
		this.port = port;
		this. threadedIndex = threadedIndex;
	}
	
	public void runServer() throws IOException {
		Server server = new Server(port);
		ServletHandler handler = new ServletHandler();
//		handler.addServletWithMapping(MainServlet.class, "/main");
		
		handler.addServletWithMapping(new ServletHolder(new MainServlet(threadedIndex)), "/main");
		
		server.setHandler(handler);
		try {
			server.start();
			server.join();
		}
		catch (Exception e){
			System.out.println("Failed to run the server.");
		}
	}
}
