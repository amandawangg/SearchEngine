package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using tabs.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author Amanda Wang CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
public class SimpleJsonWriter {
	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at
	 *   the initial indentation level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeArray(Collection<Integer> elements, Writer writer, int indent) throws IOException {
		var iterator = elements.iterator();
		
		writer.write("[\n");
		if (!elements.isEmpty()) {
			writeIndent(iterator.next().toString(), writer, indent + 1);
			
			while (iterator.hasNext()) {
				writer.write(",\n");
				writeIndent(iterator.next().toString(), writer, indent + 1);
			}
			writer.write("\n");
		}
		writeIndent(writer, indent);
		writer.write("]");
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at
	 *   the initial indentation level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeObject(Map<String, Integer> elements, Writer writer, int indent) throws IOException {
		var iterator = elements.keySet().iterator();
		
		writer.write("{\n");
		if (!elements.isEmpty()) {
			String firstElem = iterator.next();
			writeIndent("\"" + firstElem, writer, indent + 1);
			writer.write("\": " + elements.get(firstElem));
			
			while (iterator.hasNext()) {
				String nextElem = iterator.next();
				writer.write(",\n");
				writeIndent("\"" + nextElem, writer, indent + 1);
				writer.write("\": " + elements.get(nextElem));
			}
			writer.write("\n");
		}
		writeIndent(writer, indent);
		writer.write("}");
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays. The generic
	 * notation used allows this method to be used for any type of map with any
	 * type of nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param indent the initial indent level; the first bracket is not indented,
	 *   inner elements are indented by one, and the last bracket is indented at
	 *   the initial indentation level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeNestedArray(Map<String, ? extends Collection<Integer>> elements, Writer writer, int indent) throws IOException {
		var iterator = elements.keySet().iterator();
		
		writer.write("{\n");
		if (!elements.isEmpty()) {
			String firstElem = iterator.next();
			writeIndent("\"" + firstElem, writer, indent + 1);
			writer.write("\": ");
			writeArray(elements.get(firstElem), writer, indent + 1);

			while (iterator.hasNext()) {
				String nextElem = iterator.next();
				writer.write(",\n");
				writeIndent("\"" + nextElem, writer, indent + 1);
				writer.write("\": ");
				writeArray(elements.get(nextElem), writer, indent + 1);
			}
			writer.write("\n");
		}
		writeIndent(writer, indent);
		writer.write("}");
	}
	
	/**
	 * @param infoMap the map that contains all stems, path and location of all file paths inside the directory
	 * @return a {@link String} containing the elements in pretty JSON format
	 */
	public static String writeTripleNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> infoMap) {
		try {
			StringWriter writer = new StringWriter();
			writeTripleNested(infoMap, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * @param infoMap the map that contains all stems, path and location of all file paths inside the directory
	 * @param path the file path to use
	 * @throws IOException if IO error occurs
	 */
	public static void writeTripleNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> infoMap, Path path) throws IOException{
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeTripleNested(infoMap, writer, 0);
		}
	}
	
	/**
	 * Writes the elements of infoMap as a pretty JSON object with triple nested arrays to file.
	 * 
	 * @param infoMap the map that contains all stems, path and location of all file paths inside the directory
	 * @param writer Buffered writer
	 * @param indent the number of indent
	 * @throws IOException if IO error occurs
	 */
	public static void writeTripleNested(TreeMap<String, TreeMap<String, TreeSet<Integer>>> infoMap, Writer writer, int indent) throws IOException {
		var iterator = infoMap.keySet().iterator();
		
		writer.write("{\n");
		if (!infoMap.isEmpty()) {
			String firstElem = iterator.next();
			writeIndent("\"" + firstElem, writer, indent + 1);
			writer.write("\": ");
			writeNestedArray(infoMap.get(firstElem), writer, indent + 1);

			while (iterator.hasNext()) {
				String nextElem = iterator.next();
				writer.write(",\n");
				writeIndent("\"" + nextElem, writer, indent + 1);
				writer.write("\": ");
				writeNestedArray(infoMap.get(nextElem), writer, indent + 1);
			}
			writer.write("\n");
		}
		writeIndent(writer, indent);
		writer.write("}");
	}
	
	/**
	 * Writes the elements of search results as a pretty JSON object to file.
	 * 
	 * @param results search results map that stores all query lines and its matched results
	 * @return a {@link String} containing the elements in pretty JSON format
	 */
	public static String writeSearchResults(TreeMap<String, List<InvertedIndex.SearchResult>> results) {
		try {
			StringWriter writer = new StringWriter();
			writeSearchResults(results, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * Writes the elements of search results as a pretty JSON object to file.
	 * 
	 * @param results search results map that stores all query lines and its matched results
	 * @param path the file path to write to
	 * @throws IOException if IO error occurs
	 */
	public static void writeSearchResults(TreeMap<String, List<InvertedIndex.SearchResult>> results, Path path) throws IOException{
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeSearchResults(results, writer, 0);
		}
	}
	
	/**
	 * Writes the elements of search results as a pretty JSON object to file.
	 * 
	 * @param results search results map that stores all query lines and its matched results
	 * @param writer Buffered Writer
	 * @param indent the number of indent
	 * @throws IOException if IO error occurs
	 */
	public static void writeSingleSearch(List<InvertedIndex.SearchResult> results, Writer writer, int indent) throws IOException {
		writer.write("[\n");
		if (!results.isEmpty()) {
			writeIndent("{", writer, indent + 1);
			writer.write("\n");
			var iterator = results.iterator();
			writeIndent(iterator.next().toString(), writer, indent + 2);
			while (iterator.hasNext()) {
				writer.write("}");
				writer.write(",\n");
				writeIndent("{\n", writer, indent + 1);
				writeIndent(iterator.next().toString(), writer, indent + 2);
			}
			writer.write("}");
			writer.write("\n");
		}
		writeIndent("]", writer, indent);
	}
	
	/**
	 * Writes the elements of search results as a pretty JSON object to file.
	 * 
	 * @param results search results map that stores all query lines and its matched results
	 * @param writer Buffered Writer
	 * @param indent the number of indent
	 * @throws IOException if IO error occurs
	 */
	public static void writeSearchResults(TreeMap<String, List<InvertedIndex.SearchResult>> results, Writer writer, int indent) throws IOException {
		writer.write("{\n");
		if (!results.isEmpty()) {
			var iterator = results.keySet().iterator();
			String firstElem = iterator.next();
			writeQuote(firstElem, writer, indent + 1);
			writer.write(": ");
			writeSingleSearch(results.get(firstElem), writer, indent + 1);
			while (iterator.hasNext()) {
				String nextElem = iterator.next();
				writer.write(",\n");
				writeQuote(nextElem, writer, indent + 1);
				writer.write(": ");
				writeSingleSearch(results.get(nextElem), writer, indent + 1);
			}
			writer.write("\n");
		}
		writeIndent("}", writer, indent);
	}
	
	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static void writeArray(
			Collection<Integer> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #writeObject(Map, Writer, int)
	 */
	public static void writeObject(
			Map<String, Integer> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #writeNestedArray(Map, Writer, int)
	 */
	public static void writeNestedArray(
			Map<String, ? extends Collection<Integer>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8)) {
			writeNestedArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #writeArray(Collection, Writer, int)
	 */
	public static String writeArray(Collection<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #writeObject(Map, Writer, int)
	 */
	public static String writeObject(Map<String, Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #writeNestedArray(Map, Writer, int)
	 */
	public static String writeNestedArray(Map<String, ? extends Collection<Integer>> elements) {
		try {
			StringWriter writer = new StringWriter();
			writeNestedArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Indents the writer by the specified number of times. Does nothing if the
	 * indentation level is 0 or less.
	 *
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(Writer writer, int indent) throws IOException {
		while (indent-- > 0) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the String element.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeIndent(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write(element);
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "}
	 * quotation marks.
	 *
	 * @param element the element to write
	 * @param writer the writer to use
	 * @param indent the number of times to indent
	 * @throws IOException if an IO error occurs
	 */
	public static void writeQuote(String element, Writer writer, int indent) throws IOException {
		writeIndent(writer, indent);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}
}
