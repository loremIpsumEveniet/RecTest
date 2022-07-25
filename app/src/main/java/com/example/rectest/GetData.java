package com.example.rectest;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONObject;

public class GetData {

    private static final String API_KEY = "e9c1a11bb3e77145dac9ee507b296931";

    private static final String GetMovieURL = "https://api.themoviedb.org/3/search/movie?api_key=";
    private static final String GetTvURL = "https://api.themoviedb.org/3/search/tv?api_key=";

    private static final String GeneralQueryURL = "https://api.themoviedb.org/3/";

    private static JSONObject RetrievedMovieJson;
    private static JSONObject RetrievedTVJson;

    public static String ExecutionErrors = "";

    public static Bitmap ReturnedImage;


    public static Bitmap getBitmapFromURL(String URL, int ScaleToWidth, int ScaleToHeight) { //Executes in a thread a different class, get image something, it's bellow
        ExecutionErrors = "";

        Bitmap TempBitmap = ReturnedImage;

        GetImageFromURL classObject = new GetImageFromURL(URL,ScaleToWidth,ScaleToHeight);
        Thread getImageThread = new Thread(classObject);
        getImageThread.start();

        try {
            getImageThread.join();
        }
        catch (Exception error) {
            System.out.println(error.toString());
            ExecutionErrors+="\nError: could not join threads";
        }
        if (TempBitmap == ReturnedImage) {
            return null;
        }
        else {
            return ReturnedImage;
        }
    }
    public static SpecificSearchResultsStruct getSpecificDetails(GeneralSearchResultsStruct AddObject) {
        ExecutionErrors = "";

        String QueryContentType = "movie/";
        if (AddObject.ContentTypeID > 0) {
            QueryContentType = "tv/";
        }

        GetJsonFromURL classObject = new GetJsonFromURL(GeneralQueryURL + QueryContentType + AddObject.TMDB_ID + "?api_key=" + API_KEY);
        Thread getDataThread = new Thread(classObject);
        getDataThread.start();

        try {
            getDataThread.join();
        }
        catch (Exception error) {
            System.out.println(error.toString());
            ExecutionErrors+="\nError: could not join threads";
        }

        JSONObject SpecificContentDetails = GetJsonFromURL.ReturnedJson;

        String Description = "";

        String ReleaseDate = "";

        String Status = "";

        Bitmap BigPoster = null;
        String BigPosterRequestString = "https://image.tmdb.org/t/p/original/"; //TODO: if "original" fails often then implement https://developers.themoviedb.org/3/configuration/get-api-configuration


        if (SpecificContentDetails != null) { //TODO so far all the fields are the same but in the future I might need to add a check if we're searching a tv json or a movie json

            //Get Description
            try {
                Description = SpecificContentDetails.getString("overview");
            }
            catch (Exception nothing) {
                Description = "N/A"; //Could not find a description, do nothing
            }
            //Get Description ^

            //Get Poster
            try {
                int ScreenWidth  = Resources.getSystem().getDisplayMetrics().widthPixels;
                int ScreenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
                BigPoster = getBitmapFromURL(BigPosterRequestString + (SpecificContentDetails.getString("poster_path")),ScreenWidth,ScreenHeight);
            }
            catch (Exception nothing) {
                BigPoster = null; //Could not find a poster, do nothing
            }
            //Get Poster ^

            //Get Release Date
            try {
                ReleaseDate = SpecificContentDetails.getString("release_date");
            }
            catch (Exception nothing) {
                try {
                    ReleaseDate = SpecificContentDetails.getString("first_air_date");
                }
                catch (Exception nothing2) {
                    ReleaseDate = "N/A";
                }
            }
            //Get Release Date ^


            //Get Status
            try {
                Status = SpecificContentDetails.getString("status");
            }
            catch (Exception nothing) {
                Status = "N/A";
            }
            //Get Status ^
        }
        return new SpecificSearchResultsStruct(AddObject.Name,AddObject.TMDB_ID,AddObject.ContentTypeID,BigPoster,Description,ReleaseDate,Status);
    }

    public static RetrievedContent readJsonFromString(String RawInputString) { //Get json from direct string search (like a google search)
        ExecutionErrors = "";

        String AppendedRawInputString = RawInputString.replaceAll(" ", "+");

        GetJsonFromURL classObject = new GetJsonFromURL(GetMovieURL + API_KEY + ("&query=") + AppendedRawInputString);
        Thread getDataThread = new Thread(classObject);
        getDataThread.start();

        try {
            getDataThread.join();
            RetrievedMovieJson =  GetJsonFromURL.ReturnedJson;
        }
        catch (Exception error) {
            System.out.println(error.toString());
            ExecutionErrors+="\nError: could not join threads";
        }

        classObject = new GetJsonFromURL(GetTvURL + API_KEY + ("&query=") + AppendedRawInputString);
        getDataThread = new Thread(classObject);
        getDataThread.start();

        try {
            getDataThread.join();
            RetrievedTVJson =  GetJsonFromURL.ReturnedJson;
        }
        catch (Exception error) {
            System.out.println(error.toString());
            ExecutionErrors+="\nError: could not join threads";
        }
        return new RetrievedContent(RetrievedMovieJson, RetrievedTVJson);
    }


}
class GetJsonFromURL implements  Runnable {

    public static JSONObject ReturnedJson;

    private final String URL;

    public void run() {
        if (URL != null) {
            try {
                ReturnedJson = StartRead(URL);
            }
            catch (Exception error) {
                System.out.println(error.toString());
                GetData.ExecutionErrors = "\n Error: could start StartRead from thread";
            }
        }
    }
    public GetJsonFromURL(String url) {
        URL = url;
    }

    private JSONObject StartRead(String url) throws IOException { //Reads any json from any url (if available)

        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            StringBuilder resultBuilder = new StringBuilder();
            int counter;
            while ((counter = rd.read()) != -1) {
                resultBuilder.append((char) counter);
            }
            String jsonText = resultBuilder.toString();
            return new JSONObject(jsonText);
        }
        catch (FileNotFoundException error) {
            return null;
            //TODO File Not Found
        }
        catch (Exception error) {
            System.out.println(error.toString());
            GetData.ExecutionErrors = "\n Error: could not resolve the url";
            return null;
        }
        finally {
            is.close();
        }
    }
}

class GetImageFromURL implements Runnable {

    private final String SourceURL;
    private final int ScaleToWidth;
    private final int ScaleToHeight;

    public void run() {
        try {
            GetBitmap();
        }
        catch (Exception error) {
           System.out.println(error.toString());
           GetData.ExecutionErrors += "\n Error: could not start GetBitmap() from thread";
        }

    }
    public GetImageFromURL(String URL, int ScaleX, int ScaleY) {
        SourceURL = URL;
        ScaleToWidth = ScaleX;
        ScaleToHeight = ScaleY;
    }
    private void GetBitmap() {
        try {
            System.out.println(SourceURL);
            URL url = new URL(SourceURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            myBitmap = Bitmap.createScaledBitmap(myBitmap, ScaleToWidth, ScaleToHeight, false);
            GetData.ReturnedImage = myBitmap;
        }
        catch (IOException error) {
            System.out.println(error.toString());
            GetData.ExecutionErrors += "\n Error: could not get image from URL";
        }
    }
}
