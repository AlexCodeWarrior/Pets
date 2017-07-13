/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;
import com.example.android.pets.data.PetContract.PetsEntry;
/**
 * Displays list of pets that were entered and stored in the app.
 */

public class CatalogActivity extends AppCompatActivity {
    public static final int GENDER_MALE = 0;
  public static final int GENDER_FEMALE = 1;


    //private PetDbHelper mDbHelper;
    public  static final String LOG_TAG =  CatalogActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
      // mDbHelper = new PetDbHelper(this);
        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {


        String[] projection = {PetsEntry._ID, PetsEntry.COLUMN_PET_NAME,
                PetsEntry.COLUMN_PET_BREED,
                PetsEntry.COLUMN_PET_GENDER,
                PetsEntry.COLUMN_PET_WEIGHT};


        Cursor c = getContentResolver().query( PetsEntry.CONTENT_URI ,projection,null,null,null);


        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            displayView.setText("Number of rows in pets database table: " + c.getCount() + "\n\n");
            displayView.append(PetsEntry._ID + "-"+PetsEntry.COLUMN_PET_NAME+"-"+PetsEntry.COLUMN_PET_BREED+"-"+PetsEntry.COLUMN_PET_GENDER+"-"+PetsEntry.COLUMN_PET_WEIGHT + "\n");

            int idColumnIndex = c.getColumnIndex(PetsEntry._ID);
            int nameColumnIndex = c.getColumnIndex(PetsEntry.COLUMN_PET_NAME);
            int breedColumnIndex = c.getColumnIndex(PetsEntry.COLUMN_PET_BREED);
            int genderColumnIndex = c.getColumnIndex(PetsEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = c.getColumnIndex(PetsEntry.COLUMN_PET_WEIGHT);
            while(c.moveToNext()){
                int currID = c.getInt(idColumnIndex);
                String crrName = c.getString(nameColumnIndex);
                String  currBreed = c.getString(breedColumnIndex);
                int currGender = c.getInt(genderColumnIndex);
                int currWeight = c.getInt(weightColumnIndex);
                
                displayView.append("\n" + currID + "-"+ crrName+ "-"+ currBreed+ "-"+ currGender+ "-"+ currWeight);
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            c.close();
        }
    }

       @Override
      protected void onStart(){
           super.onStart();
           displayDatabaseInfo();
      }

     public void insertPet(){
         // Create a new map of values, where column names are the keys
         ContentValues values = new ContentValues();

         values.put(PetsEntry.COLUMN_PET_NAME ,"Toto");
         values.put(PetsEntry.COLUMN_PET_BREED,"chiquaua");
         values.put(PetsEntry.COLUMN_PET_GENDER ,PetsEntry.GENDER_MALE);
         values.put(PetsEntry.COLUMN_PET_WEIGHT,14);


          getContentResolver().insert(PetsEntry.CONTENT_URI,values);
     }
}
