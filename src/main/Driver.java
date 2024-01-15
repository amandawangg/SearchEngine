package edu.usfca.cs272;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author Amanda Wang
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
public class Driver {
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		ArgumentParser parser = new ArgumentParser(args);
		ThreadedInvertedIndex threadedIndex = null;
		InvertedIndex index = null;
		WorkQueue queue = null;
		QueryFileParserInterface query = null; 
		int threadCount;
		int numURL = 1;
		int port;
		
		try {
			threadCount = Integer.parseInt(parser.getString("-threads"));
		} catch (NumberFormatException e) {
			threadCount = 5;
		}
		
		if (parser.hasFlag("-max")) {
			try {
				numURL = Integer.parseInt(parser.getString("-max"));
			} catch (NumberFormatException e) {
				numURL = 1;
			}
		}
		
		if (parser.hasFlag("-threads")) {
			threadedIndex = new ThreadedInvertedIndex();
			index = threadedIndex;
			
			if (threadCount > 0) {
				queue = new WorkQueue(threadCount);
				query = new ThreadedQueryFileParser(threadedIndex, queue);
			}
		}
		else {
			index = new InvertedIndex();
			query = new QueryFileParser(index);
		}
		
		if (parser.hasFlag("-text")) {
			Path inputPath = parser.getPath("-text");
			try {
				if (queue != null) {
					ThreadedInvertedIndexBuilder.build(inputPath, threadedIndex, queue);
				}
				else {
					InvertedIndexBuilder.build(inputPath, index);
				}
			} catch (IOException e) {
				System.out.println("Unable to open the input file: " + inputPath);
			} catch (NullPointerException e) {
				System.out.println("No file exists in the input path.");
			}
		}
		
		if (parser.hasFlag("-html")) {
			threadedIndex = new ThreadedInvertedIndex();
			index = threadedIndex;
			queue = new WorkQueue(threadCount);
			query = new ThreadedQueryFileParser(threadedIndex, queue);
			
			try {
				URL url = new URL(parser.getString("-html"));
				WebCrawler crawler = new WebCrawler(url, numURL, threadedIndex, queue);
				crawler.crawl();
			}
			catch (MalformedURLException e) {
				System.out.println("Unable to process URL");
			}
		}
		
		if (parser.hasFlag("-server")) {
			port = Integer.parseInt(parser.getString("-server"));
//			threadedIndex = new ThreadedInvertedIndex();
//			index = threadedIndex;
			
			SearchEngineServer server = new SearchEngineServer(port, threadedIndex);
			try {
				server.runServer();
			} catch (IOException e) {
				System.out.println("Unable to run server");
			}
		}
		
		if (parser.hasFlag("-query")) { 
			Path inputPath = parser.getPath("-query");
			try {
				query.buildSearch(inputPath, parser.hasFlag("-exact"));
			}
			catch (IOException e) {
				System.out.println("Unable to process the search query: " + inputPath.toString());
			}
			catch (NullPointerException e) {
				System.out.println("No file exists in the query path.");
			}
		}
		
		if (parser.hasFlag("-results")) {
			Path writePath = parser.getPath("-results", Path.of("results.json"));
			try {
				query.writeResults(writePath);
			}
			catch (Exception e) {
				System.out.println("Unable to write to path");
			}
		}
		
		if (parser.hasFlag("-index")) {
			Path writePath = parser.getPath("-index", Path.of("index.json"));
			try {
				index.writeJson(writePath);
			}
			catch (Exception e) {
				System.out.println("Unable to write to path: " + writePath.toString());
			}
		}
		
		if (parser.hasFlag("-counts")) {
			Path writePath = parser.getPath("-counts");
			try {
				index.writeCount(writePath);
			}
			catch (Exception e) {
				System.out.println("Unable to write to path: " + writePath.toString());
			}
		}
		
		if (queue != null) {
			queue.shutdown();
		}
	}
}