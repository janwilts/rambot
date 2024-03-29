package com.janwilts.bigmovie.parser.inserters;

import com.janwilts.bigmovie.parser.parsers.Parser;
import com.janwilts.bigmovie.parser.util.DatabaseConnection;

import java.io.File;

/**
 * @author Jan
 */
public class CountryInserter extends Inserter {
    public CountryInserter(File file, DatabaseConnection connection) {
        super(file, connection);
    }

    @Override
    public void insert() {
        message();
        try {
            executeSQL("country.sql");
            executeInsert("insertion.country", csv.getCanonicalPath(), Parser.DELIMITER);
            executeSQL("countryjoiner.sql");
            executeSQL("gross.sql");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] getRequiredTables() {
        return new String[] {
                "country",
                "movie_country",
                "movie"
        };
    }
}
