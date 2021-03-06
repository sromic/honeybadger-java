package io.honeybadger.util;

import java.util.*;

/**
 * Commonly shared Collection utilities.
 *
 * @author <a href="https://github.com/dekobon">Elijah Zupancic</a>
 * @since 1.0.11
 */
public class HBCollectionUtils {
    private HBCollectionUtils() { }

    /**
     * Checks a collection to see if it is null or empty.
     * @param collection Collection to check
     * @return true if null or empty
     */
    public static boolean isPresent(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * Parses a comma separated string into a collection of values.
     * This is not a real CSV parser. This is a toy used for processing
     * configuration values that should never have embedded commas or
     * quotation marks.
     *
     * @param csv CSV string input
     * @return a collection of values base on CSV
     */
    public static Collection<String> parseNaiveCsvString(String csv) {
        if (!HBStringUtils.isPresent(csv)) return Collections.emptyList();

        List<String> list = new LinkedList<>();
        String[] values = csv.split("(\\s*),(\\s*)");
        Collections.addAll(list, values);

        return list;
    }
}
