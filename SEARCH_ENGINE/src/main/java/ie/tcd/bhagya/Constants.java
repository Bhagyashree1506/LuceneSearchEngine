package ie.tcd.bhagya;

import java.util.*;

/**
 * Central location for all constants used in the application.
 * Makes it easier to modify configuration values.
 */
public class Constants {
    public static final String INDEX_FILES = "../GeneratedIndex";
    public static final String CRANFIELD_DOCS = "../Cranfield_Files/cran.all.1400";
    public static final String QUERY_FILE_PATH = "../Cranfield_Files/cran.qry";
    public static final String RESULTS_FILES = "../GeneratedResults.txt";
    public static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
        "a", "an", "the", "and", "or", "but", "is", "to", "in", "for", "on", "with", "as", "by"
    ));
}
