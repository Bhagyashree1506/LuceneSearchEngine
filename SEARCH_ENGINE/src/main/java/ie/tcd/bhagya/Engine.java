package ie.tcd.bhagya;

import java.io.IOException;
import org.apache.lucene.queryparser.classic.ParseException;

/**
 * Main entry point for the search engine application.
 * Orchestrates the indexing and searching processes.
 */
public class Engine {
    public static void main(String[] args) throws IOException, ParseException {
        try {
            // Initialize components
            IndexBuilder indexBuilder = new IndexBuilder();
            SearchExecutor searchExecutor = new SearchExecutor();

            // Execute main workflow
            indexBuilder.buildIndex(); // Step 1: Index the Cranfield Collection
            searchExecutor.proceedWithVSM(); // Step 2: Query the index using BM25
        } catch (IOException e) {
            System.err.println("Error during initialization or execution: " + e.getMessage());
            throw e;
        }
    }
}
