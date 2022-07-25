package com.example.rectest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    LinearLayout LibraryContentList;
    Context MainContext;

    private TextView ERROR_TEXT_VIEW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainContext = this; //FUCK this line, this is the worst line i've ever written

        ERROR_TEXT_VIEW = findViewById(R.id.LibraryErrorTextView);
        Button SendRequestButton = findViewById(R.id.SendRequest);
        Button AddContentToLibraryButton = findViewById(R.id.AddContent);
        LibraryContentList = findViewById(R.id.LibraryContentList);



        AddContentToLibraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, SearchContent.class);
               startActivity(intent);
            }
        });

        SendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseManager dbHelper = new DatabaseManager(MainContext);
                // Gets the data repository in write mode
                SQLiteDatabase WritableDB = dbHelper.getWritableDatabase();

                //Cursor NothingCursor = WritableDB.rawQuery("INSERT INTO " +  DatabaseCreator.DatabaseContainer.TABLE_NAME + " VALUES ('HELLO', 'WORLD')",null);

                String Request = "INSERT INTO MyStuff VALUES (null,'HOUSE',0,0,)";
                WritableDB.execSQL(Request);

                SQLiteDatabase ReadableDB = dbHelper.getReadableDatabase();
                Cursor cursor = ReadableDB.rawQuery("SELECT * FROM " + DatabaseCreator.DatabaseContainer.TABLE_NAME,null);

                //Cursor cursor = db.query(DatabaseCreator.FeedEntry.TABLE_NAME, null, null, null, null, null, null);

                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    ERROR_TEXT_VIEW.append(cursor.getString(1) + "\n");
                }
            }
        });
    }

}