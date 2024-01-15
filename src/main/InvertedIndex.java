package edu.usfca.cs272;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Stores the underlying data structure when processing the input files
 * 
 * @author Amanda Wang CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
public class InvertedIndex {
	/**
	 * Data structure for Inverted Index that stores stemmed words, path and positions 
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> infoMap;
	
	/**
	 * Data structure for storing path and number of stems
	 */
	private final TreeMap<String, Integer> countMap;
	
	/**
	 * Constructor creates a new TreeMap
	 */
	public InvertedIndex() {
		this.infoMap = new TreeMap<>();
		this.countMap = new TreeMap<>();
	}
	
	/**
	 * adds all the stems, paths and locations to infoMap
	 * 
	 * @param listStems List of stemmed words
	 * @param inputPath the value of "-text" flag
	 */
	public void addAll(List<String> listStems, String inputPath) {
		for (int i = 0; i < listStems.size(); i++) {
			 add(listStems.get(i), inputPath.toString(), i + 1);
		}
	}
	
	/**
	 * Copy and add data from threaded inverted index to the infoMap
	 * 
	 * @param otherInvertedIndex the other inverted index (threaded index)
	 */
	public void addAll(InvertedIndex otherInvertedIndex) {
		for (String word : otherInvertedIndex.infoMap.keySet()) {
			if (!this.infoMap.containsKey(word)) {
				this.infoMap.put(word, otherInvertedIndex.infoMap.get(word));
			} 
			else {
				for (String location : otherInvertedIndex.infoMap.get(word).keySet()) {
					if (!this.infoMap.get(word).containsKey(location)) {
						this.infoMap.get(word).put(location, otherInvertedIndex.infoMap.get(word).get(location));
					}
					else {
						this.infoMap.get(word).get(location).addAll(otherInvertedIndex.infoMap.get(word).get(location));
					}
				}
			}
		}
		
		for (String location : otherInvertedIndex.countMap.keySet()) {
			Integer position = otherInvertedIndex.countMap.get(location);
			if (!countMap.containsKey(location) || position > countMap.get(location)) {
				this.countMap.put(location, position);
			}
		}
	}
	
	/**
	 * add the each stem, path and location to infoMap
	 * 
	 * @param word stemmed word to add to the infoMap
	 * @param location path of the stemmed word
 	 * @param position position of the stemmed word
 	 */
	public void add(String word, String location, Integer position) {
		this.infoMap.putIfAbsent(word, new TreeMap<>());
		this.infoMap.get(word).putIfAbsent(location, new TreeSet<Integer>());
		this.infoMap.get(word).get(location).add(position);
		
		if (!countMap.containsKey(location) || position > countMap.get(location)) {
			this.countMap.put(location, position);
		}
	}
	
	/**
	 * Decides whether to call exact or partial search
	 * 
	 * @param query set of query words for each query line
	 * @param exactSearch calls exactSerach if true
	 * @return a list of SearchResults objects
	 */
	public List<SearchResult> search(Set<String> query, boolean exactSearch) {
		if (exactSearch) {
			return exactSearch(query);
		}
		return partialSearch(query);
	}
	
	/**
	 * Search if the query stems matches the infoMap stems exactly
	 * 
	 * @param queries set of query words for each query line
	 * @return a list of SearchResults objects
	 */
	public List<SearchResult> exactSearch(Set<String> queries) {
		// return list of SearchResults to return
		List<SearchResult> results = new ArrayList<SearchResult>();
		
		// for keeping track of matched paths and checks for path duplicates
		HashMap<String, SearchResult> lookupMap = new HashMap<String, SearchResult>();
		
		// Search result object to store the results
		SearchResult searchRes = null;
		
		for (String query : queries) {
			if (infoMap.containsKey(query)) {
				searchHelper(query, lookupMap, searchRes, results);
			}
		}
		Collections.sort(results);
		return results;
	}
	
	/**
	 * Search if the stems in infoMap starts with query stems.
	 * 
	 * @param queries set of query words for each query line
	 * @return a list of SearchResults objects
	 */
	public List<SearchResult> partialSearch(Set<String> queries) {
		// return list of SearchResults to return
		List<SearchResult> results = new ArrayList<SearchResult>();

		// for keeping track of matched paths and checks for path duplicates
		HashMap<String, SearchResult> lookupMap = new HashMap<String, SearchResult>();
		
		// Search result object to store the results
		SearchResult searchRes = null;
		
		for (String query : queries) {
			for (String stem : infoMap.tailMap(query).keySet()) {
				if (!stem.startsWith(query)) {
					break;
				}
				else {
					searchHelper(stem, lookupMap, searchRes, results);
				}
			}
		}
		Collections.sort(results);
		return results;
	}
	
	/**
	 * Helper method for exact and partial search that loops through paths and compares/updates lookupMap
	 * 
	 * @param stem query stem or invertedIndex stem to loop through paths of the stem
	 * @param lookupMap stores matched paths and SearchResults objects and keep track of duplicates
	 * @param searchRes single SearchResult object
	 * @param results list of all the SearchResult
	 */
	private void searchHelper(String stem, HashMap<String, SearchResult> lookupMap, SearchResult searchRes, List<SearchResult> results) {
		for (String path : infoMap.get(stem).keySet()) {
			if (lookupMap.containsKey(path)) {
				searchRes = lookupMap.get(path);
			} else {
				searchRes = new SearchResult(path);
				results.add(searchRes);
				lookupMap.put(path, searchRes);
			}
			searchRes.update(stem);
		}
	}
	/**
	 * class that stores a single search result and implements compareTo method for sorting
	 *
	 */
	public class SearchResult implements Comparable<SearchResult> {
		/** The score calculated after getting total words and total count*/
		private double score;
		
		/** The number of times query is found in the inverted index */
		private int count;
		
		/** The matched file path from searching. */
		private final String location;
		
		/**
		 * initializes location, count and score
		 * 
		 * @param path the source
		 */
		public SearchResult(String path) {
			location = path;
			count = 0;
			score = 0.0;
		}
		
		@Override
		public String toString() {
			String formatScore = String.format("%.8f", score);
			return "\"count\": " + count + ",\n\t\t\t" + "\"score\": " + formatScore + ",\n\t\t\t" + "\"where\": " + "\"" + location + "\"\n\t\t";
		}

		@Override
		public int compareTo(SearchResult otherResult) {
			if (Double.compare(this.score, otherResult.score) < 0) {
				return 1;
			}
			if (Double.compare(this.score, otherResult.score) > 0) {
				return -1;
			}
			else if (Double.compare(this.score, otherResult.score) == 0) {
				if (Integer.compare(this.count, otherResult.count) < 0) {
					return 1;
				}
				else if (Integer.compare(this.count, otherResult.count) == 0) {
					return this.location.compareToIgnoreCase(otherResult.location);
				}
				else {
					return -1;
				}
			}
			return -1;
		}
		
		/**
		 * updates score and count for the query word
		 * 
		 * @param word query word
		 */
		private void update(String word) {
			this.count += infoMap.get(word).get(location).size();
			this.score = (double) this.count / countMap.get(location);
		}
		
		/**
		 * returns score
		 * 
		 * @return score
		 */
		public double getScore() {
			return score;
		}
		
		/**
		 * returns count 
		 * 
		 * @return count
		 */
		public int getCount() {
			return count;
		}
		
		/**
		 * returns location
		 * 
		 * @return location
		 */
		public String getLocation() {
			return location;
		}
	}
	
	/**
	 * returns the size of infoMap
	 * 
	 * @return the number of keys in infoMap
	 */
	public int size() {
		return infoMap.size();
	}
	
	/**
	 * returns the number of paths the word appeared in
	 * 
	 * @param word stemmed word
	 * @return the number of paths the word appeared in
	 */
	public int size(String word) {
		return contains(word) ? infoMap.get(word).size() : 0;
	}
	
	/**
	 * returns the number of times the word appears in the location
	 * 
	 * @param word stemmed word
	 * @param location file path
	 * @return the number of times the word appears in the location
	 */
	public int size(String word, String location) {
		return contains(word, location) ? infoMap.get(word).get(location).size() : 0;
	}
	
	/**
	 * checks if the word exists in the infoMap
	 * 
	 * @param word stemmed word
	 * @return true if the word exists in the map; otherwise false
	 */
	public boolean contains(String word) {
		return infoMap.containsKey(word);
	}
	
	/**
	 * checks if the word is in the file path
	 * 
	 * @param word stemmed word
	 * @param location file path
	 * @return true if the word is in the file path; otherwise false
	 */
	public boolean contains(String word, String location) {
		return contains(word) && infoMap.get(word).containsKey(location);
	}
	
	/**
	 * checks if the word is in the position of the file
	 * 
	 * @param word stemmed word
	 * @param location file path
	 * @param position position of the word in the file
	 * @return true if the the word is in the position of the file; otherwise false
	 */
	public boolean contains(String word, String location, Integer position) {
		return contains(word, location) && infoMap.get(word).get(location).contains(position);
	}
	
	/**
	 * Write the stems, location and file path with pretty JSON to the file
	 * 
	 * @param writePath path that is "-index" flag's value
	 * @throws IOException if IO error occurs
	 */
	public void writeJson(Path writePath) throws IOException {
		SimpleJsonWriter.writeTripleNested(infoMap, writePath);
	}
	
	/**
	 * Write the file paths and word count with pretty JSON to the file
	 * @param writePath path that is "-counts" flag's value
	 * @throws IOException if IO error occurs
	 */
	public void writeCount(Path writePath) throws IOException {
		SimpleJsonWriter.writeObject(countMap, writePath);
	}
	
	/**
	 * returns the paths and total word count
	 * @return returns the countMap
	 */
	public Map<String, Integer> getCount() {
		return Collections.unmodifiableMap(this.countMap);
	}
	
	/**
	 * returns the set of stemmed words in infoMap
	 * 
	 * @return the set of words
	 */
	public Set<String> get() {
		return Collections.unmodifiableSet(infoMap.keySet());
	}
	
	/**
	 * returns the set of paths for the stemmed word in infoMap
	 * 
	 * @param word stemmed word
	 * @return the nested map (all the file paths and positions of the word)
	 */
	public Set<String> get(String word) {
		if (contains(word)) {
			return Collections.unmodifiableSet(infoMap.get(word).keySet());
		}
		return Collections.emptySet();
	}
	
	/**
	 * returns the set of location for the stemmed word and the path in infoMap
	 * 
	 * @param word stemmed word
	 * @param location file path
	 * @return return the positions of the word
	 */
	public Set<Integer> get(String word, String location) {
		if (contains(word, location)) {
			return Collections.unmodifiableSet(infoMap.get(word).get(location));
		}
		return Collections.emptySet();
	}
	
	@Override
	public String toString() {
		return this.infoMap.toString();
	}
}