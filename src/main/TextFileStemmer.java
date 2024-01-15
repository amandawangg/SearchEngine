package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;
import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * Utility class for parsing and stemming text and text files into collections
 * of stemmed words.
 *
 * @see TextParser
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
public class TextFileStemmer {
	/**
	 * Parses each line into cleaned and stemmed words
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return a list of cleaned and stemmed words in parsed order
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static List<String> listStems(String line, Stemmer stemmer) {
		List<String> stemmed = new ArrayList<String>();
		stemLine(line, stemmer, stemmed);
		return stemmed;
	}
	
	/**
	 * Parses each line into cleaned and stemmed words
	 * 
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @param stemmed the Collections of stemmed words
	 */
	public static void stemLine(String line, Stemmer stemmer, Collection<String> stemmed) {
		String[] parsed = TextParser.parse(line);
		for (String words : parsed) {
			stemmed.add(stemmer.stem(words).toString());
		}
	}

	/**
	 * Parses each line into cleaned and stemmed words using the default stemmer.
	 *
	 * @param line the line of words to parse and stem
	 * @return a list of cleaned and stemmed words in parsed order
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see #listStems(String, Stemmer)
	 */
	public static List<String> listStems(String line) {
		return listStems(line, new SnowballStemmer(ENGLISH));
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words
	 * using the default stemmer.
	 *
	 * @param input the input file to parse and stem
	 * @return a list of stems from file in parsed order
	 * @throws IOException if unable to read or parse file
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see StandardCharsets#UTF_8
	 * @see #listStems(String, Stemmer)
	 */
	public static List<String> listStems(Path input) throws IOException {
		Stemmer stemmer = new SnowballStemmer(ENGLISH);
		List<String> cleaned = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(input, UTF_8)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				stemLine(line, stemmer, cleaned);
			}
		}
		return cleaned;
	}

	/**
	 * Parses the line into unique, sorted, cleaned, and stemmed words.
	 *
	 * @param line the line of words to parse and stem
	 * @param stemmer the stemmer to use
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static Set<String> uniqueStems(String line, Stemmer stemmer) {
		Set<String> stemmed = new TreeSet<>();
		stemLine(line, stemmer, stemmed);
		return stemmed;
	}

	/**
	 * Parses the line into unique, sorted, cleaned, and stemmed words using the
	 * default stemmer.
	 *
	 * @param line the line of words to parse and stem
	 * @return a sorted set of unique cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static Set<String> uniqueStems(String line) {
		return uniqueStems(line, new SnowballStemmer(ENGLISH));
	}

	/**
	 * Reads a file line by line, parses each line into unique, sorted, cleaned,
	 * and stemmed words using the default stemmer.
	 *
	 * @param input the input file to parse and stem
	 * @return a sorted set of unique cleaned and stemmed words from file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see StandardCharsets#UTF_8
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static Set<String> uniqueStems(Path input) throws IOException {
		Stemmer stemmer = new SnowballStemmer(ENGLISH);
		Set<String> cleaned = new TreeSet<>();
		try (BufferedReader reader = Files.newBufferedReader(input, UTF_8)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				stemLine(line, stemmer, cleaned);
			}
		}
		return cleaned;
	}

	/**
	 * Reads a file line by line, parses each line into unique, sorted, cleaned,
	 * and stemmed words using the default stemmer, and adds the set of unique
	 * sorted stems to a list per line in the file.
	 *
	 * @param input the input file to parse and stem
	 * @return a list where each item is the sets of unique sorted stems parsed
	 *   from a single line of the input file
	 * @throws IOException if unable to read or parse file
	 *
	 * @see SnowballStemmer
	 * @see ALGORITHM#ENGLISH
	 * @see StandardCharsets#UTF_8
	 * @see #uniqueStems(String, Stemmer)
	 */
	public static List<Set<String>> listUniqueStems(Path input) throws IOException {
		Stemmer stemmer = new SnowballStemmer(ENGLISH);
		List<Set<String>> cleaned = new ArrayList <Set<String>>();
		try (BufferedReader reader = Files.newBufferedReader(input, UTF_8)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				cleaned.add(uniqueStems(line, stemmer));
			}
		}
		return cleaned;
	}
}