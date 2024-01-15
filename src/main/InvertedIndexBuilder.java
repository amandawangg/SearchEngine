package edu.usfca.cs272;

import static java.nio.charset.StandardCharsets.UTF_8;
import static opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM.ENGLISH;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * Builder class for building the files and directories
 * Stems the file, stores the stemmed words in the underlying data structure and writes it to the output path
 * 
 * @author Amanda Wang CS 272 Software Development (University of San Francisco)
 * @version Spring 2022
 */
public class InvertedIndexBuilder {
	/**
	 * If it is the input path is a file, stem each path/file and write to the output file using write()
	 * 
	 * @param inputPath path that is "-text" flag's value
	 * @param infoMap data structure that stores stemmed words, path and location
	 * @throws IOException if IO error occurs
	 */
	public static void buildFile(Path inputPath, InvertedIndex infoMap) throws IOException {
		Stemmer stemmer = new SnowballStemmer(ENGLISH);
		String location = inputPath.toString();
		int i = 0;
		try (BufferedReader reader = Files.newBufferedReader(inputPath, UTF_8)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] parsed = TextParser.parse(line);
				for (String words : parsed) {
					infoMap.add(stemmer.stem(words).toString(), location, i + 1);
					i++;
				}
			}
		}
	}
	
	/**
	 * If it is the input path is a directory, traverse, stem each path/file and write to the output file using writeNested()
	 * 
	 * @param inputPath path that is "-text" flag's value
	 * @param infoMap data structure that stores stemmed words, path and location
	 * @throws IOException if IO error occurs
	 */
	public static void build(Path inputPath, InvertedIndex infoMap) throws IOException {
		if (Files.isDirectory(inputPath)) {
			traverseDirectory(inputPath, infoMap);
		}
		else {
			buildFile(inputPath, infoMap);
		}
	}

	/**
	 * Traversing the directory
	 * 
	 * @param directory the input path directory
	 * @param infoMap data structure that stores stemmed words, path and location
	 * @throws IOException if an IO Exception occurs
	 */
	private static void traverseDirectory(Path directory, InvertedIndex infoMap) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(directory)) {
			for (Path path : listing) {
				if (Files.isDirectory(path)) {
					traverseDirectory(path, infoMap);
				}
				else if(isTextFile(path)) {
					buildFile(path, infoMap);
				}
			}
		}
	}
	
	/**
 	* checks if the path being inputed is a text file
 	* 
 	* @param path the input path
 	* @return true if it is a text file; otherwise, returns false
 	*/
	public static boolean isTextFile(Path path) {
		String pathStr = path.toString().toLowerCase();
		return (pathStr.endsWith(".text") || pathStr.endsWith(".txt"));
	}
}