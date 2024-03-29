package com.janwilts.bigmovie.parser.parsers;

import com.janwilts.bigmovie.parser.Parsable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

/**
 * @author Yannick & Jan
 */
public abstract class Parser {
    // Some static characters for use within the parsers
    public static final String DELIMITER = ",";
    public static final String NEW_LINE = "\n";
    public static final String QUOTE = "\"";
    public static final char QUOTE_CHAR = '\"';
    public static final String DOUBLE_QUOTE = "\"\"";
    public static final String TAB = "\t";
    public static final char TAB_CHAR = '\t';
    
    private static Parser currentParser;
    
    protected File file;
    BufferedReader reader;
    File csv;
    private static int lines = 0;

    // Gets the correct parser class from the enum and executes it.
    public static String[] parseFile(File file) {
        //@formatter:off
        int index = Parsable.getList()
                            .stream()
                            .map(Parsable::toString)
                            .collect(Collectors.toList())
                            .indexOf(file.getName().substring(0, file.getName().indexOf('.')));
        //@formatter:on
        
        Parsable parser = Parsable.getList().get(index);
        
        try {
            currentParser = parser.getParser(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        currentParser.parse();
        return new String[]{file.getName().substring(0, file.getName().indexOf('.')), String.valueOf(Parsable.getList().indexOf(parser) + 1), String.valueOf(Parsable.getList().size()), String.valueOf(lines)};
    }

    // Constructor of a parser which also sets the csv file
    public Parser(File file) {
        this.file = file;
        this.csv = new File("output/" + file.getName().substring(0, file.getName().indexOf('.')) + ".csv");
        csv.getParentFile().mkdirs();
        
        try {
            this.reader = getReader();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Gets the correct reader depending on the file type, either .list or .list.gz
    private BufferedReader getReader() throws IOException {
        String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1);
        
        switch (extension) {
            case "gz":
                return new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), StandardCharsets.ISO_8859_1));
            case "zip":
                return new BufferedReader(new InputStreamReader(new ZipInputStream(new FileInputStream(file)), StandardCharsets.ISO_8859_1));
            default:
                return new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1));
        }
    }
    
    protected String readLine() throws IOException {
        lines++;
        return this.reader.readLine();
    }
    
    public abstract void parse();
    
    /**
     * Surround the supplied String in quotation marks
     *
     * @param input the String input to be surrounded with quotation marks
     * @return the String input surrounded with quotation marks
     */
    protected String addQuotes(String input) {
        return QUOTE + input + QUOTE;
    }
}
