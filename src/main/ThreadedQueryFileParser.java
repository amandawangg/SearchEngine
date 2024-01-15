package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * Threaded version of QueryFileParser for Multithreading
 * 
 * @author Amanda Wang CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 *
 */
public class ThreadedQueryFileParser implements QueryFileParserInterface {
	/**
	 * threaded version of Inverted Index
	 */
	private final ThreadedInvertedIndex index;
	
	/**
	 * Data structure for storing query lines and its SearchResults
	 */
	private final TreeMap<String, List<ThreadedInvertedIndex.SearchResult>> resultsMap;
	
	/**
	 * Read write lock to protect data
	 */
	private final SimpleReadWriteLock queryLock;
	
	/**
	 * Worker threads for Multithreading
	 */
	private final WorkQueue workers;
	
	/**
	 * Initializes InvertedIndex, ResultsMap, ReadWrite lock and WorkQueue
	 * 
	 * @param index threaded version of inverted index
	 * @param workers worker threads
	 */
	public ThreadedQueryFileParser(ThreadedInvertedIndex index, WorkQueue workers) {
		this.index = index;
		this.resultsMap = new TreeMap<>();
		queryLock = new SimpleReadWriteLock();
		this.workers = workers;
	}

	@Override
	public void buildSearch(Path inputPath, boolean exactSearch) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(inputPath, UTF_8)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				buildSearch(line, exactSearch);
			}
		}
		QueryFileParserInterface.super.buildSearch(inputPath, exactSearch);
		workers.finish();
	}

	@Override
	public void buildSearch(String line, boolean exactSearch) {
		workers.execute(new Task(line, exactSearch));
	}
	
	/**
	 * The non-static task class that will update the search results and handles tasks.
	 *
	 */
	private class Task implements Runnable {
		/**
		 * Query line
		 */
		private final String line;
		
		/** 
		 * Determines if it's exact or partial search
		 */
		private final boolean exactSearch;
		
		/**
		 * Initializes invertedIndex, query line, set of cleaned query, exactSearch
		 * 
		 * @param line each query line
		 * @param exactSearch determines if it's exact or partial search
		 */
		public Task(String line, boolean exactSearch) {
			this.line = line;
			this.exactSearch = exactSearch;
		}
		
		@Override
		public void run() {
			Set<String> cleaned = TextFileStemmer.uniqueStems(line);
			if (cleaned.isEmpty()) {
				return;
			}
			String queryLine = String.join(" ", cleaned);
			
			queryLock.readLock().lock();
			try {
				if (resultsMap.containsKey(queryLine)) {
					return;
				}
			}
			finally {
				queryLock.readLock().unlock();
			}
			
			var local = index.search(cleaned, exactSearch);
			
			queryLock.writeLock().lock();
			try {
				resultsMap.put(queryLine, local);
			}
			finally {
				queryLock.writeLock().unlock();
			}
		}
	}
	
	@Override
	public synchronized void writeResults(Path writePath) throws IOException {
		queryLock.readLock().lock();
		try {
			SimpleJsonWriter.writeSearchResults(this.resultsMap, writePath);
		}
		finally {
			queryLock.readLock().unlock();
		}
	}
}
