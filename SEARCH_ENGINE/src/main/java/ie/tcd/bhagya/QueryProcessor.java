package ie.tcd.bhagya;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Handles query-related operations including parsing and preparation
 * of search queries.
 */
public class QueryProcessor {
    public String prepareQuery(String queryText) {
        String[] terms = queryText.split("\\s+");
        StringBuilder preparedQuery = new StringBuilder();

        for (String term : terms) {
            // Escape special characters that could interfere with Lucene query syntax
            term = term.replaceAll("[*?]", "\\\\$0");
            preparedQuery.append(term).append(" ");
        }
        return preparedQuery.toString().trim();
    }

    public List<String> parseQueries(String filePath) throws IOException {
        List<String> queries = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        StringBuilder queryBuilder = new StringBuilder();

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith(".I")) {
                if (queryBuilder.length() > 0) {
                    queries.add(queryBuilder.toString().trim());
                }
                queryBuilder.setLength(0);
            } else if (!line.startsWith(".W")) {
                queryBuilder.append(line).append(" ");
            }
        }

        // Add last query if exists
        if (queryBuilder.length() > 0) {
            queries.add(queryBuilder.toString().trim());
        }

        return queries;
    }
}
