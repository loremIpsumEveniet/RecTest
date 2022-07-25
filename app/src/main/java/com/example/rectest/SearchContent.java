package com.example.rectest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import static java.lang.Math.min;

public class SearchContent extends AppCompatActivity {

    protected static final int MAX_NUMBER_OF_SUGGESTIONS = 8;
    protected static final int MIN_NUMBER_OF_REQUIRED_CHARACTERS_FOR_SEARCH = 5;
    protected static final int MaxImageWidth = 185;
    protected static final int MaxImageHeight = 278;

    protected Context MainContext;

    protected TextView ERROR_TEXT_VIEW;

    protected TextView NoResultsTextView;
    protected LinearLayout AutoCompleteResultsBox;
    protected TextInputEditText SearchMovieTextBox;

    protected SearchView TestSearchView;

    protected Thread getDataThread;

    protected static boolean UpdateAutoCompleteTextFlag = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_content);

        ERROR_TEXT_VIEW = findViewById(R.id.QueryErrorTextView);

        SearchMovieTextBox = findViewById(R.id.SearchByNameBox);
        AutoCompleteResultsBox = findViewById(R.id.AutoCompleteLayout);
        NoResultsTextView = findViewById(R.id.NoResultsText);
        NoResultsTextView.setVisibility(View.GONE);

        //RunCleanUp(); //Clean up from the last time //TODO until I figure out how to keep the search results

        MainContext = this; //FUCK this line, this is the worst line i've ever written

        ImageButton BackToLibraryButton = findViewById(R.id.BackToLibraryButton);

        BackToLibraryButton.setOnClickListener(new View.OnClickListener() { //Back To Library Button Logic
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchContent.this, MainActivity.class);
                startActivity(intent);
            }
        });



        ImageButton InstantSearchButton = findViewById(R.id.SearchButton);

        InstantSearchButton.setOnClickListener(new View.OnClickListener() { //Instant Search Button Logic //TODO look into replacing my shitty text box with an "Search View"
            @Override
            public void onClick(View v) {

                RunInstantSearch();
            }
        });

        Handler ErrorHandler = new Handler(); //Run Error Display Handing "Thread"
        ErrorHandler.post(new Runnable(){
            @Override
            public void run() {
                PrintDebug();
                if (RunPersistentThread.ThreadErrorText!=null) {
                    ERROR_TEXT_VIEW.append(RunPersistentThread.ThreadErrorText);
                    RunPersistentThread.ThreadErrorText = "";
                }
                ErrorHandler.postDelayed(this,500); // Set time here to refresh textView
            }
        });

        Handler TestHandler = new Handler(); //Run Autocomplete Text Box Update "Thread" //TODO possible optimisation, replace Autocomplete only liner-layout with the once pre-calculated in the persistent thread
        TestHandler.post(new Runnable(){ //TODO implement a "see more" button to show more results

            public int PreviewImagePadding = 10;

            public int LittleArrowPadding = 100;

            @Override
            public void run() {
                if (UpdateAutoCompleteTextFlag) {

                    UpdateAutoCompleteTextFlag = false;

                    AutoCompleteResultsBox.removeAllViews();

                    if (Utilities.SearchResults.size() > 0) {

                        NoResultsTextView.setVisibility(View.GONE);

                        for (int i = 0; i < Utilities.SearchResults.size(); i++) {

                            LinearLayout TextAndImageView = new LinearLayout(MainContext); //TODO, maybe in future I could try the Card View looks a lot better

                            GeneralSearchResultsStruct ClickedObject = Utilities.SearchResults.get(i);  //Set A Function To Run If Clicked
                            TextAndImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AddContentToLibrary(ClickedObject);
                                }
                            });
                            TextAndImageView.setBackgroundResource(R.drawable.btn_clear);

                            AutoCompleteResultsBox.addView(TextAndImageView);

                            TextView TempNameTextView = new TextView(MainContext);

                            String TempNameText = "<font color=#737373>" + Utilities.SearchResults.get(i).Name + "</font>";
                            String TempTypeText = "<font color=#FF5722>" + Utilities.ContentTypeNames[Utilities.SearchResults.get(i).ContentTypeID]  + "</font>";

                            TempNameTextView.setText(Html.fromHtml(TempNameText+" "+TempTypeText));

                            TempNameTextView.setMaxWidth(AutoCompleteResultsBox.getWidth()-MaxImageWidth-LittleArrowPadding);
                            TempNameTextView.setMaxHeight(MaxImageWidth+PreviewImagePadding);

                            TempNameTextView.measure(0, 0);

                            TempNameTextView.setSingleLine(false);
                            TempNameTextView.setPadding(0,0,0,0);

                            ImageView TempImageView = new ImageView(MainContext);
                            ImageView TempNextImageView = new ImageView(MainContext);

                            TempNextImageView.setImageResource(R.drawable.next_arrow_drawable);
                            TempNextImageView.setPadding(AutoCompleteResultsBox.getWidth()-(TempNameTextView.getMeasuredWidth()+MaxImageWidth)-LittleArrowPadding,(MaxImageHeight - 4*PreviewImagePadding)/2,0,0);


                            TempImageView.setMaxWidth(MaxImageWidth);
                            TempImageView.setMaxHeight(MaxImageHeight);
                            TempImageView.setAdjustViewBounds(true);

                            if (Utilities.SearchResults.get(i).CoverArt == null) {
                                TempImageView.setImageResource(R.drawable.no_image_found_small_drawable);
                            }
                            else {
                                TempImageView.setImageBitmap(Utilities.SearchResults.get(i).CoverArt);

                            }
                            TempImageView.setPadding(0, 0, PreviewImagePadding, PreviewImagePadding);

                            TextAndImageView.addView(TempImageView, 0);
                            TextAndImageView.addView(TempNameTextView, 1);
                            TextAndImageView.addView(TempNextImageView,2);
                        }
                    }
                    else {
                        NoResultsTextView.setVisibility(View.VISIBLE);
                    }
                }
                TestHandler.postDelayed(this,500); // Set time here to refresh textView
            }
        });

        RunPersistentThread classObject = new RunPersistentThread();
        getDataThread = new Thread(classObject);
        getDataThread.start();


        AutoCompleteResultsBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ERROR_TEXT_VIEW.append(AutoCompleteResultsBox.getChildAt(v.getC));
            }
        });

        SearchMovieTextBox.setOnKeyListener(new View.OnKeyListener() { // If the event is a key-down event on the "enter" button
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    RunInstantSearch();
                    return true;
                }
                return false;
            }
        });

        SearchMovieTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                SearchedString = s.toString();
                RunPersistentThread.ThreadWaitFlag = true;

                if (SearchedString.length() == 0) {
                    RunPersistentThread.ThreadWaitFlag = false;
                    AutoCompleteResultsBox.removeAllViews();
                    Utilities.SearchResults.clear();
                }
            }
        });
    }
    protected static String SearchedString;

    private void PrintDebug() {
        ERROR_TEXT_VIEW.setText("");
        ERROR_TEXT_VIEW.append(String.valueOf(AutoCompleteResultsBox.getChildCount()) + "\n" + RunPersistentThread.InstantSearchFlag + "\n" + RunPersistentThread.ThreadWaitFlag + "\n" + UpdateAutoCompleteTextFlag);
    }

    private void RunInstantSearch() {
        Utilities.hideKeyboard(MainContext,SearchMovieTextBox);
        if (SearchedString != null) {
            if (SearchedString.length() >= SearchContent.MIN_NUMBER_OF_REQUIRED_CHARACTERS_FOR_SEARCH) {
                RunPersistentThread.InstantSearchFlag = true;
                RunPersistentThread.StartQuery();
            }
        }
    }
    public void RunCleanUp() {


        Utilities.SearchResults.clear(); //Some garbage collection so that the search works the next time you enter
        AutoCompleteResultsBox.removeAllViews();
    }

    public void AddContentToLibrary(GeneralSearchResultsStruct FoundObject) {
        //Todo when returning back here make the search results appear again
        RunCleanUp();

        Utilities.SpecificSearchResults = GetData.getSpecificDetails(FoundObject);

        Intent intent = new Intent(SearchContent.this, AddContent.class);
        startActivity(intent);
    }
}

class RunPersistentThread implements Runnable {

    protected static boolean InstantSearchFlag = false;

    protected static boolean ThreadWaitFlag = false;

    protected static String ThreadErrorText = "";

    private static final String DefaultImageSearchURL = "https://image.tmdb.org/t/p/w185"; //TODO: if w185 fails often then implement https://developers.themoviedb.org/3/configuration/get-api-configuration

    public void run() {
        try {
            ThreadWaitFlag = false;
            InstantSearchFlag = false;
            SearchContent.UpdateAutoCompleteTextFlag = false;
            PersistentThread();
        }
        catch (Exception error) {
            System.out.println(error.toString());
            ThreadErrorText+="\nError: Catastrophic Error";
        }
    }
    private static void PersistentThread() {
        while (true) {
            if (ThreadWaitFlag) {
                ThreadWaitFlag = false;
                Utilities.Wait(2000);
                if (!ThreadWaitFlag && SearchContent.SearchedString.length() >= SearchContent.MIN_NUMBER_OF_REQUIRED_CHARACTERS_FOR_SEARCH) {
                    if (!InstantSearchFlag) {
                        StartQuery();
                    }
                    else {
                        InstantSearchFlag = false;
                    }
                }
            }
        }
    }

    public static void StartQuery() {

        Utilities.SearchResults.clear();

        RetrievedContent RetrievedContent = GetData.readJsonFromString(SearchContent.SearchedString);
        ThreadErrorText+=GetData.ExecutionErrors;
        int i = 0;

        //Movies and TV shows query separately because I didn't want to rely on "name" vs "title" what if TMDB changes something
        //Search Results For TV Shows
        JSONObject JsonTVFromRequest = RetrievedContent.TV;

        if (JsonTVFromRequest != null) {
            try {
                JSONArray jsonResult = JsonTVFromRequest.getJSONArray("results");
                int numberOfResults = JsonTVFromRequest.getInt("total_results");
                for (; i < min(numberOfResults, SearchContent.MAX_NUMBER_OF_SUGGESTIONS); i++) {

                    String name = jsonResult.getJSONObject(i).getString("name");
                    int ID = jsonResult.getJSONObject(i).getInt("id");
                    String posterPath = jsonResult.getJSONObject(i).getString("poster_path");
                    Bitmap CoverArt = GetData.getBitmapFromURL(DefaultImageSearchURL + posterPath, SearchContent.MaxImageWidth, SearchContent.MaxImageHeight);

                    GeneralSearchResultsStruct TempResultsStruct = new GeneralSearchResultsStruct(name, ID, 1, CoverArt); //Type 0 is TV
                    Utilities.SearchResults.add(TempResultsStruct);
                }
            }
            catch (Exception error) {
                System.out.println(error.toString());
                ThreadErrorText += "\nError: could not fetch Json for TV shows";
            }
        }

        //SearchResults For Movies
        JSONObject JsonMoviesFromRequest = RetrievedContent.Movies;

        if (JsonMoviesFromRequest != null) {
            try {
                JSONArray jsonResult = JsonMoviesFromRequest.getJSONArray("results");
                int numberOfResults = JsonMoviesFromRequest.getInt("total_results");
                for (; i < min(numberOfResults, SearchContent.MAX_NUMBER_OF_SUGGESTIONS); i++) {

                    String name = jsonResult.getJSONObject(i).getString("title");
                    int ID = jsonResult.getJSONObject(i).getInt("id");
                    String posterPath = jsonResult.getJSONObject(i).getString("poster_path");
                    Bitmap CoverArt = GetData.getBitmapFromURL(DefaultImageSearchURL + posterPath, SearchContent.MaxImageWidth, SearchContent.MaxImageHeight);

                    GeneralSearchResultsStruct TempResultsStruct = new GeneralSearchResultsStruct(name, ID, 0, CoverArt); //Type 0 is Movie
                    Utilities.SearchResults.add(TempResultsStruct);
                }
            }
            catch (Exception error) {
                System.out.println(error.toString());
                ThreadErrorText += "\nError: could not fetch Json for movies";
            }
        }

        InstantSearchFlag = false;
        SearchContent.UpdateAutoCompleteTextFlag = true;
    }
}


