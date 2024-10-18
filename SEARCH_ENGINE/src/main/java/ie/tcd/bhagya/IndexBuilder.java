package ie.tcd.bhagya;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;

public class IndexBuilder {
    private final DocumentParser documentParser;

    public IndexBuilder() throws IOException {
        try {
            this.documentParser = new DocumentParser();
        } catch (IOException e) {
            System.err.println("Failed to initialize DocumentParser: " + e.getMessage());
            throw e; // Re-throw the exception to be handled by the calling code
        }
    }

    public void buildIndex() throws IOException {
        // Define analyzers for different fields
        Map<String, Analyzer> analyzerMap = new HashMap<>();
        analyzerMap.put("docID", new KeywordAnalyzer());
        analyzerMap.put("title", new EnglishAnalyzer());
        analyzerMap.put("author", new WhitespaceAnalyzer());
        analyzerMap.put("content", new EnglishAnalyzer());

        // Create combined analyzer
        PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(
            new StandardAnalyzer(), 
            analyzerMap
        );

        // Create and populate index
        try (Directory indexDirectory = FSDirectory.open(Paths.get(Constants.INDEX_FILES));
             IndexWriter indexWriter = new IndexWriter(
                 indexDirectory, 
                 new IndexWriterConfig(analyzer).setOpenMode(IndexWriterConfig.OpenMode.CREATE)
             )) {

            List<Document> documents = documentParser.parseFiles(Constants.CRANFIELD_DOCS);
            for (Document doc : documents) {
                indexWriter.addDocument(doc);
            }
        }
    }
}
