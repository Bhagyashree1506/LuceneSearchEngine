package ie.tcd.bhagya;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.apache.lucene.document.*;
import opennlp.tools.tokenize.*;

/**
 * Handles parsing of document files and text preprocessing.
 * Responsible for converting raw text into Lucene documents.
 */
public class DocumentParser {
    private final TokenizerME tokenizer;

    public DocumentParser() throws IOException {
        // Initialize tokenizer
        try (var modelIn = Files.newInputStream(Paths.get("../models/en-token.bin"))) {
            TokenizerModel model = new TokenizerModel(modelIn);
            this.tokenizer = new TokenizerME(model);
        }
    }

    public List<Document> parseFiles(String filePath) throws IOException {
        List<Document> documentList = new ArrayList<>();
        List<String> fileLines = Files.readAllLines(Paths.get(filePath));
        Document currentDocument = null;
        StringBuilder stringBuilder = new StringBuilder();
        String currentField = "";

        for (String line : fileLines) {
            line = line.trim();

            if (line.startsWith(".I")) {
                if (currentDocument != null) {
                    addContentToDocument(currentDocument, stringBuilder, currentField);
                    documentList.add(currentDocument);
                }

                currentDocument = new Document();
                stringBuilder.setLength(0);
                String docId = line.substring(3).trim();
                currentDocument.add(new StringField("docID", docId, Field.Store.YES));
                currentField = "";
            } else if (line.startsWith(".T")) {
                currentField = "title";
                stringBuilder.setLength(0);
            } else if (line.startsWith(".A")) {
                currentField = "author";
                stringBuilder.setLength(0);
            } else if (line.startsWith(".W")) {
                currentField = "content";
                stringBuilder.setLength(0);
            } else {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(" ");
                }
                stringBuilder.append(line);
            }
        }

        // Handle last document
        if (currentDocument != null) {
            addContentToDocument(currentDocument, stringBuilder, currentField);
            documentList.add(currentDocument);
        }

        return documentList;
    }

    private void addContentToDocument(Document doc, StringBuilder contentBuilder, String field) {
        if (!field.isEmpty() && contentBuilder.length() > 0) {
            String processedContent = preprocessText(contentBuilder.toString());
            doc.add(new TextField(field, processedContent, Field.Store.YES));
        }
    }

    private String preprocessText(String text) {
        String[] tokenArray = tokenizer.tokenize(text);
        List<String> filteredTokensList = new ArrayList<>();

        for (String token : tokenArray) {
            token = token.toLowerCase();
            if (!Constants.STOPWORDS.contains(token)) {
                filteredTokensList.add(token);
            }
        }

        return String.join(" ", filteredTokensList);
    }
}
