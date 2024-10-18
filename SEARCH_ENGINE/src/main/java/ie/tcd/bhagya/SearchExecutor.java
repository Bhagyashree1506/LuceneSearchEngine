package ie.tcd.bhagya;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.*;
import org.apache.lucene.queryparser.classic.*;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;

/**
 * Handles all search-related operations including query parsing
 * and execution of searches.
 */
public class SearchExecutor {
    public void proceedWithBM25() throws IOException, ParseException {
        try (Directory directory = FSDirectory.open(Paths.get(Constants.INDEX_FILES));
             DirectoryReader ireader = DirectoryReader.open(directory)) {

            IndexSearcher indexSearcher = new IndexSearcher(ireader);
            indexSearcher.setSimilarity(new BM25Similarity());
            queryOnIndex(indexSearcher, "BM25");
        }
    }

    public void proceedWithVSM() throws IOException, ParseException {
        try (Directory directory = FSDirectory.open(Paths.get(Constants.INDEX_FILES));
             DirectoryReader ireader = DirectoryReader.open(directory)) {

            IndexSearcher indexSearcher = new IndexSearcher(ireader);
            indexSearcher.setSimilarity(new ClassicSimilarity());
            queryOnIndex(indexSearcher, "VSM");
        }
    }

    private void queryOnIndex(IndexSearcher searcher, String similarityType) throws IOException, ParseException {
        QueryProcessor queryProcessor = new QueryProcessor();
        List<String> queryList = queryProcessor.parseQueries(Constants.QUERY_FILE_PATH);
        
        // Configure analyzers
        Map<String, Analyzer> analyzerPerField = new HashMap<>();
        analyzerPerField.put("docID", new KeywordAnalyzer());
        analyzerPerField.put("title", new EnglishAnalyzer());
        analyzerPerField.put("content", new EnglishAnalyzer());
        PerFieldAnalyzerWrapper queryAnalyzer = new PerFieldAnalyzerWrapper(
            new StandardAnalyzer(), 
            analyzerPerField
        );

        QueryParser parser = new QueryParser("content", queryAnalyzer);
        List<String> results = new ArrayList<>();

        // Process each query
        for (int i = 0; i < queryList.size(); i++) {
            String queryText = queryList.get(i);
            if (queryText.isEmpty()) {
                System.err.println("Skipping empty query with ID: " + (i + 1));
                continue;
            }

            queryText = queryProcessor.prepareQuery(queryText);
            Query query = parser.parse(queryText);
            TopDocs topDocs = searcher.search(query, 50);

            for (int rank = 0; rank < topDocs.scoreDocs.length; rank++) {
                ScoreDoc scoreDoc = topDocs.scoreDocs[rank];
                Document doc = searcher.doc(scoreDoc.doc);
                String docId = doc.get("docID");
                if (docId != null) {
                    String resultLine = String.format("%d Q0 %s %d %.4f %s", 
                        (i + 1), docId, (rank + 1), scoreDoc.score, similarityType);
                    results.add(resultLine);
                }
            }
        }

        Files.write(Paths.get(Constants.RESULTS_FILES), results);
    }
}
