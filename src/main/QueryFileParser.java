package edu.usfca.cs272;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * Builder class for parsing query and getting results
 * 
 * @author Amanda Wang CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 *
 */
public class QueryFileParser implements QueryFileParserInterface {
	/**
	 * Data structure for storing query lines and its SearchResults
	 */
	private final TreeMap<String, List<InvertedIndex.SearchResult>> resultsMap;
	
	/**
	 * To access the Inverted Index 
	 */
	private final InvertedIndex invertedIndex;
	
	/**
	 * Constructor to initialized invertedIndex and create a new map for results
	 * 
	 * @param infoMap the inverted index
	 */
	public QueryFileParser(InvertedIndex infoMap) {
		invertedIndex = infoMap;
		resultsMap = new TreeMap<String, List<InvertedIndex.SearchResult>>();
	}
	
	/**
	 * Performs search for inputed query line
	 * 
	 * @param line each query line
	 * @param exactSearch determines if it's exact or partial search
	 */
	@Override
	public void buildSearch(String line, boolean exactSearch) {
		Set<String> cleaned = TextFileStemmer.uniqueStems(line);
		if (cleaned.isEmpty()) {
			return;
		}
		String queryLine = String.join(" " , cleaned);
		if (!resultsMap.containsKey(queryLine)) {
			resultsMap.put(queryLine, invertedIndex.search(cleaned, exactSearch));
		}
	}
	
	/**
	 * Write the search results with pretty JSON
	 * 
	 * @param writePath path that is "-results" flag's value
	 * @throws IOException if IO error occurs
	 */
	@Override
	public void writeResults(Path writePath) throws IOException {
		SimpleJsonWriter.writeSearchResults(resultsMap, writePath);
	}
}