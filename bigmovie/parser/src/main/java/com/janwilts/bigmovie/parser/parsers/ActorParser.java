package com.janwilts.bigmovie.parser.parsers;

import com.janwilts.bigmovie.parser.util.RomanNumeral;

import java.io.File;
import java.io.PrintWriter;

/**
 * @author Yannick
 */
public class ActorParser extends Parser {
    public ActorParser(File file) {
        super(file);
    }
    
    @Override
    public void parse() {
        try (PrintWriter writer = new PrintWriter(this.csv, "UTF-8")) {
            String currentActorName = "";
            int currentActorOccurance = 0;
            
            String gender = this.csv.getName().equals("actors.csv") ? "M" : "F";
            
            int linesBeforeList = 4;
            boolean foundList = false;
            
            for (String line; (line = this.readLine()) != null; ) {
                if (!foundList && line.contains("THE " + file.getName().substring(0, file.getName().indexOf('.')).toUpperCase() + " LIST")) foundList = true;
                else if (foundList && linesBeforeList != 0) linesBeforeList--;
                else if (foundList && line.startsWith("-----------------------")) return;
                else if (linesBeforeList == 0 && line.length() > 0) {
                    if (line.charAt(0) != TAB_CHAR) {
                        try{
                            currentActorName = line.substring(0, line.indexOf(TAB)).trim();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        
                        String[] result = RomanNumeral.getFromActorName(currentActorName);
                        
                        currentActorName = result[0];
                        currentActorOccurance = Integer.parseInt(result[1]);
                        
                        line = line.substring(line.indexOf(TAB_CHAR));
                    }
                    
                    if (line.contains(QUOTE)) {
                        continue;
                    }
                    
                    String currentFilm;
                    String currentRole = "";
                    String currentYear = "";
                    int yearOccurance = 0;
                    
                    if (line.contains("[")) {
                        currentFilm = line.substring(line.indexOf(TAB), line.indexOf("[")).trim();
                        currentRole = line.substring(line.indexOf("[") + 1, line.indexOf("]", line.indexOf("["))).trim();

                        if(currentRole.startsWith("Undetermined"))
                            currentRole = "";
                    }
                    else if (line.contains("<")) {
                        currentFilm = line.substring(line.indexOf(TAB), line.indexOf("<")).trim();
                    }
                    else {
                        currentFilm = line.substring(line.indexOf(TAB));
                    }
                    
                    String searchLine = line;
                    
                    while (searchLine.contains("(")) {
                        int leftCommaInd = searchLine.indexOf("(");
                        int rightCommaInd = searchLine.indexOf(")", searchLine.indexOf("(") + 1);
                        
                        if (rightCommaInd == -1) break;

                        if (searchLine.contains("(") &&
                                Character.isDigit(searchLine.charAt(leftCommaInd + 1)) &&
                                (rightCommaInd - leftCommaInd == 5 && Character.isDigit(searchLine.charAt(leftCommaInd + 1)) && Character.isDigit(searchLine.charAt(rightCommaInd - 1))) ||
                                (rightCommaInd - leftCommaInd > 5 && Character.isDigit(searchLine.charAt(leftCommaInd + 1)) && searchLine.charAt(rightCommaInd - 1) == 'I' || searchLine.charAt(rightCommaInd - 1) == 'V' || searchLine.charAt(rightCommaInd - 1) == 'X') ||
                                (rightCommaInd - leftCommaInd <= 5 && searchLine.charAt(rightCommaInd - 1) == '?' && searchLine.charAt(leftCommaInd + 1) == '?')) {
    
                            currentFilm = line.substring(line.indexOf(TAB), line.indexOf("(", line.indexOf(searchLine))).trim();
    
                            currentYear = searchLine.substring(searchLine.indexOf("(") + 1, searchLine.indexOf(")", searchLine.indexOf("(") + 1));
                            if (currentYear.contains("/")) {
                                yearOccurance = RomanNumeral.convert(currentYear.substring(currentYear.indexOf("/") + 1, currentYear.length()));
                                currentYear = currentYear.substring(0, currentYear.indexOf("/"));
                            }
                            if (currentYear.equals("????")) currentYear = "";
                            break;
                        }
                        else {
                            searchLine = searchLine.substring(searchLine.indexOf("(") + 1);
                        }
                    }
                    
                    writer.println(String.join(",", addQuotes(currentActorName.replace(QUOTE, DOUBLE_QUOTE)), currentActorOccurance + "", gender, addQuotes(currentFilm.replace(QUOTE, DOUBLE_QUOTE)), currentYear, yearOccurance + "", addQuotes(currentRole.replace(QUOTE, DOUBLE_QUOTE))));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
