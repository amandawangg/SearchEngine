package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Interface for Query file parser
 * 
 * @author Amanda Wang CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 *
 */
public interface QueryFileParserInterface {
	/**
	 * Performs search for inputed query file
	 * 
	 * @param inputPath path that includes what is being put into query
	 * @param exactSearch determines if it's exact or partial search
	 * @throws IOException if IO error occurs
	 */
	public default void buildSearch(Path inputPath, boolean exactSearch) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(inputPath, UTF_8)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				buildSearch(line, exactSearch);
			}
		}
	}
	
	/**
	 * Performs search for inputed query line
	 * 
	 * @param line each query line
	 * @param exactSearch determines if it's exact or partial search
	 */
	public void buildSearch(String line, boolean exactSearch);

	/**
	 * Write the search results with pretty JSON
	 * 
	 * @param writePath path that is "-results" flag's value
	 * @throws IOException if IO error occurs
	 */
	public void writeResults(Path writePath) throws IOException;
}
