package edu.usfca.cs272;

import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Crawls each webpage using Multithreading
 * 
 * @author Amanda Wang CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 *
 */
public class WebCrawler {
	/**
	 * threaded version of Inverted Index
	 */
	private final ThreadedInvertedIndex threadedIndex;
	
	/**
	 * URL to process
	 */
	private URL url;
	
	/**
	 * Worker threads for Multithreading
	 */
	private WorkQueue workers;
	
	/**
	 * Set of links in the webpage
	 */
	private final Set<String> linkSet;
	
	/**
	 * The total number of URLs to crawl when building the index
	 */
	private int max;
	
	/**
	 * Initializes url, max, threadedIndex and workers
	 * 
	 * @param url URL to process
	 * @param numURL the total number of URLs to crawl when building the index
	 * @param threadedIndex threaded version of inverted index
	 * @param workers worker threads for multithreading
	 */
	public WebCrawler(URL url, int numURL, ThreadedInvertedIndex threadedIndex, WorkQueue workers) {
		this.url = url;
		this.threadedIndex = threadedIndex;
		this.workers = workers;
		this.linkSet = new HashSet<String>();
		this.max = numURL;
	}
	
	/**
	 * Checks if url is already in the link set or not. If it is a unique url, create workers to build
	 */
	public void crawl() {
		linkSet.add(url.toString());
		workers.execute(new Task(url, max, threadedIndex, linkSet));
		workers.finish();
	}
	
	/**
	 * The non-static task class that will build the index.
	 *
	 */
	private class Task implements Runnable {
		/**
		 * URL to process
		 */
		private URL url;
		
		/**
		 * threaded version of Inverted Index
		 */
		private ThreadedInvertedIndex index;
		
		/**
		 * Set of links in the webpage
		 */
		private Set<String> linkSet;
		
		/**
		 * The total number of URLs to crawl when building the index
		 */
		private int max;
		
		/**
		 * Initializes the path and threaded inverted index
		 * @param url URL to process
		 * @param max the total number of URLs to crawl when building the index
		 * @param threadedIndex threaded version of inverted index
		 * @param linkSet Set of links in the webpage
		 */
		public Task(URL url, int max, ThreadedInvertedIndex threadedIndex, Set<String> linkSet){
			this.url = url;
			this.max = max;
			this.index = threadedIndex;
			this.linkSet = linkSet;
		}

		@Override
		public void run() {
			String html = HtmlFetcher.fetch(url, 3);
			ArrayList<URL> links = LinkParser.getValidLinks(url, HtmlCleaner.stripBlockElements(html));
			for (URL link : links) {
				if (!linkSet.contains(link.toString())) {
					if (linkSet.size() < max) {
						linkSet.add(link.toString());
						workers.execute(new Task(link, max, threadedIndex, linkSet));
					}
					else {
						break;
					}
				}
			}
			
			Stemmer stemmer = new SnowballStemmer(ENGLISH);
			String cleaned = HtmlCleaner.stripHtml(html);
			String[] parsed = TextParser.parse(cleaned);
			InvertedIndex local = new InvertedIndex();
			
			int i = 0; 
			for (String words : parsed) {
				local.add(stemmer.stem(words).toString(), url.toString(), i + 1);
				i++;
			}
			index.addAll(local);
		}
	}
}
