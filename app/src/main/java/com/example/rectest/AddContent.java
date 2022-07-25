package com.example.rectest;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;



public class AddContent extends AppCompatActivity {

    protected static final int ContentNameView_Padding = 10;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_content);

        TextView ERROR_TEXT_VIEW = findViewById(R.id.QueryErrorTextView);

        ImageButton BackToSearchButton = findViewById(R.id.BackToSearchButton);
        ToggleButton AddToLibraryButton = findViewById(R.id.AddToLibaryButton);
        RatingBar UserRating = findViewById(R.id.RatingBar);

        TextView ContentName = findViewById(R.id.ContentName);
        TextView ContentDescription = findViewById(R.id.DescriptionText);
        TextView ReleaseDate = findViewById(R.id.ReleaseDateText);
        TextView Type = findViewById(R.id.TypeText);
        TextView Status = findViewById(R.id.StatusText);

        TextView SortTErrorText = findViewById(R.id.SoftErrorText);
        SortTErrorText.setVisibility(View.GONE);


        ReleaseDate.setText(Utilities.SpecificSearchResults.ReleaseDate);
        Type.setText(Utilities.ContentTypeNames[Utilities.SpecificSearchResults.ContentType]);
        Status.setText(Utilities.SpecificSearchResults.Status);

        View ContentPoster = findViewById(R.id.BackroundImage);

        ContentName.setText(Utilities.SpecificSearchResults.Name);
        ContentName.measure(0,0);
        BackToSearchButton.measure(0,0);

        int ScreenWidth  = Resources.getSystem().getDisplayMetrics().widthPixels;

        while (ContentName.getMeasuredWidth() + BackToSearchButton.getMeasuredWidth() + ContentNameView_Padding >= ScreenWidth) { //This is terrible, why does getTextSize() return something other than the text size
            ContentName.setTextSize(ContentName.getTextSize() - (float)(18*(Math.floor(ContentName.getTextSize()/18)-1)));
            ContentName.measure(0,0);
        }

        if (Utilities.SpecificSearchResults.CoverArt == null) {
            ContentPoster.setBackgroundResource(R.drawable.color_grey_dark);
        }
        else {
            Drawable CoverArtDrawable = new BitmapDrawable(getResources(), Utilities.SpecificSearchResults.CoverArt);
            ContentPoster.setBackground(CoverArtDrawable);
        }


        ContentDescription.setText(Utilities.SpecificSearchResults.Description);

        BackToSearchButton.setOnClickListener(new View.OnClickListener() { //Back To Search Button Logic
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddContent.this, MainActivity.class);
                startActivity(intent);
            }
        });

        AddToLibraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserRating.getRating() == 0) {
                    SortTErrorText.setVisibility(View.VISIBLE);
                    AddToLibraryButton.setChecked(false);
                }
                else {
                    //Utilities.MegaTestVariable = true;
                }
            }
        });
    }
}
