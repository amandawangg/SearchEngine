package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Threaded version of InvertedIndexBuilder for Multithreading
 * 
 * @author Amanda Wang CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 *
 */
public class ThreadedInvertedIndexBuilder {
	/**
	 * If it is the input path is a directory, traverse, stem each path/file and write to the output file using writeNested()
	 * 
	 * @param inputPath path that is "-text" flag's value
	 * @param threadedIndex data structure that stores stemmed words, path and location
	 * @param workers WorkQueue
	 * @throws IOException if IO error occurs
	 */
	public static void build(Path inputPath, ThreadedInvertedIndex threadedIndex, WorkQueue workers) throws IOException {
		if (Files.isDirectory(inputPath)) {
			traverseDirectory(inputPath, threadedIndex, workers);
		}
		else {
			workers.execute(new Task(inputPath, threadedIndex));
		}
		workers.finish();
	}
	
	/**
	 * Traversing the directory 
	 * 
	 * @param directory the input path directory
	 * @param threadedIndex threaded version of Inverted Index
	 * @param workers WorkQueue
	 * @throws IOException if an IO Exception occurs
	 */
	private static void traverseDirectory(Path directory, ThreadedInvertedIndex threadedIndex, WorkQueue workers) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					traverseDirectory(path, threadedIndex, workers);
				}
				else if (InvertedIndexBuilder.isTextFile(path)) {
					workers.execute(new Task(path, threadedIndex));
				}
			}
		}
	}
	
	/**
	 * The non-static task class that will update the threaded inverted index and handles tasks.
	 *
	 */
	private static class Task implements Runnable {
		/**
		 * Threaded version of inverted index
		 */
		private final ThreadedInvertedIndex threadedIndex;
		
		/**
		 * Input path that is "-text" flag's value
		 */
		private final Path path;
		
		/**
		 * Initializes the path and threaded inverted index
		 * 
		 * @param path inputPath that is "-text" flag's value
		 * @param threadedIndex threaded version of inverted index
		 */
		public Task(Path path, ThreadedInvertedIndex threadedIndex){
			this.path = path;
			this.threadedIndex = threadedIndex;
		}

		@Override
		public void run() {
			try {
				InvertedIndex local = new InvertedIndex();
				InvertedIndexBuilder.build(path, local);
				threadedIndex.addAll(local);
			}
			catch (IOException e) {
				System.out.println("Unable to build.");
			}
		}
	}
}
