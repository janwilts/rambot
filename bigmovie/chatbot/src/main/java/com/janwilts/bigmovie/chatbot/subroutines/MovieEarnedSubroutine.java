package com.janwilts.bigmovie.chatbot.subroutines;

import com.janwilts.bigmovie.chatbot.discord.DiscordBot;
import com.janwilts.bigmovie.chatbot.models.Gross;
import com.janwilts.bigmovie.chatbot.models.Movie;
import com.janwilts.bigmovie.chatbot.util.APIRequester;
import com.janwilts.bigmovie.chatbot.util.PrintUtils;
import com.rivescript.RiveScript;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovieEarnedSubroutine extends Routine {

    public MovieEarnedSubroutine(DiscordBot bot) {
        super(bot);
    }

    @Override
    public String call(RiveScript rs, String[] args) {
        String country = "";
        String period = "";

        focusedMovies.clear();

        if(args[0].equals("ever")) {
            period = "ever";

            if(args[1].equals("worldwide")) {
                country = "worldwide";
            }
            else {
                if(args[2].equals("the")) {
                    country = args[3];
                }
                else {
                    country = args[2];
                }
            }
        }
        else if(args[0].equals("in")) {

            if(args[3].equals("worldwide")) {
                country = "worldwide";
            }
            else if (args[4].equals("the")) {
                country = args[5];
            }
            else {
                country = args[4];
            }

            if(args[2].equals("week")) {
                period = "7";
            }
            else if(args[2].equals("month")) {
                period = "30";
            }
            else if(args[2].equals("year")) {
                period = "365";
            }
        }

        try {
            APIRequester apiRequester = new APIRequester(Gross.class);
            List<Gross> grossResult = apiRequester.getArrayFromAPI("/q/a8?period=" + period + "&country=" + country);

            apiRequester = new APIRequester(Movie.class);
            List<Movie> movieResult = new ArrayList<>();

            for(Gross g : grossResult) {
                Movie movie = (Movie)(apiRequester.getFromAPI("/movie?id=" + g.getMovie_id()));

                if(movieResult.stream().noneMatch(m -> m.getMovie_id().equals(movie.getMovie_id()))) {
                    movieResult.add(movie);
                }
            }

            for(int i = 0; i < movieResult.size(); i++) {
                focusedMovies.put(i+1, movieResult.get(i));
            }

            setMainFocus(focusedMovies);

            return PrintUtils.movieListPrint(focusedMovies);
        }
        catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return "An error occurred.";
        }
    }
}
