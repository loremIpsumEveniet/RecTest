package com.example.rectest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONObject;

import java.util.ArrayList;

public class Utilities {

    public static String ContentTypeNames[] = {"(Movie)","(TV)"};

    public static SpecificSearchResultsStruct SpecificSearchResults;
    public static ArrayList<GeneralSearchResultsStruct> SearchResults = new ArrayList<GeneralSearchResultsStruct>();

    public static void hideKeyboard(Context context, View view) { //Hides keyboards, thank you kind stranger from stack overflow
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void Wait(int numberOfMilliseconds) {
        try {
            Thread.sleep(numberOfMilliseconds);
        }
        catch (Exception  error) {
            System.out.println(error.toString());
        }
    }
}
class SpecificSearchResultsStruct {

    public String Name;
    public int TMDB_ID;
    public int ContentType;
    public Bitmap CoverArt;

    //New Parameters
    public String Description;
    public String ReleaseDate;
    public String Status;
    //TODO I could insert more information here

    public SpecificSearchResultsStruct(String name, int ID, int contentTypeID, Bitmap poster, String description,String releaseDate,String status) {
        Name = name;
        TMDB_ID = ID;
        ContentType = contentTypeID;
        CoverArt = poster;
        Description = description;
        ReleaseDate = releaseDate;
        Status = status;
    }
}

class GeneralSearchResultsStruct {

    public String Name;
    public int TMDB_ID;
    public int ContentTypeID;
    public Bitmap CoverArt;

    public GeneralSearchResultsStruct(String name, int ID, int contentTypeID, Bitmap poster) {
        Name = name;
        TMDB_ID = ID;
        ContentTypeID = contentTypeID;
        CoverArt = poster;
    }
}
class RetrievedContent {
    public JSONObject Movies;
    public JSONObject TV;

    public RetrievedContent(JSONObject MovieJson, JSONObject TVJson) {
        Movies = MovieJson;
        TV = TVJson;
    }
}