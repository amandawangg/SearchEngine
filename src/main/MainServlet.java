package edu.usfca.cs272;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Amanda Wang
 *
 */
public class MainServlet extends HttpServlet {
	/** ID used for serialization, which we are not using. */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	ThreadedInvertedIndex threadedIndex;

	/**
	 *
	 */
	List<String> results;

	/**
	 *
	 */
	String query;

	/** HTML Template **/
	private final String htmlTemplate;

	// TODO might need to create html folder in resources and need to add html back to this line
	/** Base path with HTML templates. */
	private static final Path BASE = Path.of("src", "main", "resources");

	/**
	 * @param threadedIndex
	 * @throws IOException
	 */
	public MainServlet(ThreadedInvertedIndex threadedIndex) throws IOException {
		super();
		this.threadedIndex = threadedIndex;
		this.results = new ArrayList<>();
		this.query = new String();
		htmlTemplate = Files.readString(BASE.resolve("index.html"), StandardCharsets.UTF_8);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, String> values = new HashMap<>();
		// TODO change to name of search engine
		values.put("title", "Amanda's Search Engine");
		values.put("header", "Search Engine");

		values.put("method", "POST");
		values.put("action", request.getServletPath());

		values.put("query", "Your input: " + query);

		if (results.isEmpty()) {
			values.put("results", "");
		}
		else {
			values.put("results", String.join("\n\n", results));
		}
		
		StringSubstitutor replacer = new StringSubstitutor(values);
		String html = replacer.replace(htmlTemplate);

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();
		out.println(html);
		out.flush();

		results.clear();
		query = "";
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");

		String input = request.getParameter("search");
		query = input;
		
		if (query.isBlank()) {
			query = "";
			results.add("No results found.");
		} 
		else {
			query = StringEscapeUtils.escapeHtml4(query);
			Set<String> cleaned = TextFileStemmer.uniqueStems(query);
			
			List<InvertedIndex.SearchResult> searchResults = threadedIndex.partialSearch(cleaned);
			for (InvertedIndex.SearchResult res : searchResults) {
				synchronized (results) {
					String formatted = String.format("<p><a href=\"%s\">%s</a></p>", res.getLocation(), res.getLocation());
					results.add(formatted);
				}
			}
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

}