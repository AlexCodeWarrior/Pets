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


import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
        import android.app.AlertDialog;
        import android.app.LoaderManager;
        import android.content.ContentValues;
        import android.content.CursorLoader;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.Loader;
        import android.database.Cursor;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.v4.app.NavUtils;
        import android.support.v7.app.AppCompatActivity;
        import android.text.TextUtils;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.MotionEvent;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.EditText;
        import android.widget.Spinner;
        import android.widget.Toast;
         import android.content.DialogInterface;

import com.example.android.pets.data.PetContract.PetsEntry;


import static com.example.android.pets.CatalogActivity.LOG_TAG;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    //keep track of wether the pet has been edited (true) or false
     private boolean mPetHasChanged = false ;


    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;
    private Uri petUri ;


    /** Identifier for the pet data loader */
     private static final int EXISTING_PET_LOADER = 0;



    /*listen for any user that touches on a View , implying that they are modifying the view , and change
       the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        petUri = intent.getData();

        if (petUri == null ){
            setTitle(R.string.editor_activity_title_new_pet);
        }
        else {
            setTitle("Edit Pet");
            Log.e(LOG_TAG,"EDIT PET: "+ petUri );
            //initialize loader
            getLoaderManager().initLoader(EXISTING_PET_LOADER,null,this);

        }


        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

         /* Setup OnTouchListeners all the input fields , so we can determine if the user has touched
          or modified them. Tis will let us know if there are unsaved changes or not , if the user tries to
          leave the editor without saving.
*/
          mNameEditText.setOnTouchListener(mTouchListener);
         mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);


        setupSpinner();



    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetsEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetsEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetsEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

        //If the pet hasn't changed , continue with handling back button press

        if (!mPetHasChanged) {
            super.onBackPressed();
            return ;
        }

        /*
        Otherwise if there are unsaved changes , set up the dialog to warn user.
        Create a click listener to handle the user confirming that changes should be discarded.
         */
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //User click "Discard" button, close the current activity
                        finish();
                    }
                };
                //Show dialog that their are unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
    }

    /** show dialog that warnss the user there are unsaved changes that will be lost
     * if they continue leaving the editor .
     * discardButtonClickListener is the click listener for what to do when the user confirms
     *                                   they want to discard changes
     *
     */

      private void showUnsavedChangesDialog ( DialogInterface.OnClickListener discardButtonClickListener){
          //Create an AlertDialog.Builder and set the message , and click listeners
          // for the positive and negative  buttons on the dialog

          AlertDialog.Builder builder = new AlertDialog.Builder(this);
          builder.setMessage(R.string.unsaved_changes_dialog_msg);
          builder.setPositiveButton(R.string.discard,discardButtonClickListener);
          builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener(){
              
              @Override
              public void onClick(DialogInterface dialog , int id ){
                  //user clicked the "Keep editing"  button , so dismiss the dialog
                  //continue editing pet
                  if ( dialog !=null){
                      dialog.dismiss();
                  }
              }
          });
          AlertDialog alertDialog = builder.create();
          alertDialog.show();
      }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
              SavePet();
                finish();

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
               //If the pet hasn't changed , continue without navigating up to the parent activity
                if(!mPetHasChanged) {
                   super.onBackPressed();
                    return true;
                }
                /*
                otherwise if unsaved changes , set up a dialog to warn user
               create clicklistener to handle the user confirming that the changes should be discarded
                 */
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //User click discard button , navigae to the parent activity
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void SavePet(){


        String nameString=  mNameEditText.getText().toString().trim();
        String breedString=  mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString();
        int genderInt = mGender;

       //check if their suppose to be a new pet and all fields are empty
        if ( petUri == null &&  TextUtils.isEmpty(nameString)&& TextUtils.isEmpty(breedString)&&
                TextUtils.isEmpty(breedString)&& TextUtils.isEmpty(weightString)&& mGender == PetsEntry.GENDER_UNKNOWN){
            //leave task
            return;
        }




        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(PetsEntry.COLUMN_PET_NAME ,nameString);
        values.put(PetsEntry.COLUMN_PET_BREED, breedString);
        values.put(PetsEntry.COLUMN_PET_GENDER ,genderInt);

        int  weightInt = 0;

           //if weight was not left blank
         if (!TextUtils.isEmpty(weightString) )
         {
               weightInt = Integer.parseInt(weightString);
         }

        values.put(PetsEntry.COLUMN_PET_WEIGHT,weightInt);



        if ( petUri == null ) {
             Uri newURI = getContentResolver().insert(PetsEntry.CONTENT_URI, values);

            Log.e(LOG_TAG,"URI===========:" + newURI );



            Toast toast;
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            if(newURI == null ) {
                CharSequence text = getString(R.string.edit_insert_Failed);
                Toast.makeText(this, text, duration).show();
            }
            else{
                CharSequence text = getString(R.string.edit_insert_Success);
                Toast.makeText(this, text , duration).show();
            }

        }
        else {

            int numRow = getContentResolver().update(petUri,values,null,null);


            Toast toast;
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            if(numRow == 0 ) {
                CharSequence text = getString(R.string.edit_insert_Failed);
                Toast.makeText(this, text, duration).show();
            }
            else{
                Toast.makeText(this, R.string.Toast_modified, duration).show();
            }
        }


    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Since the editor shows all pet attributes, define a projection that contains
               // all columns from the pet table
                        String[] projection = {
                                PetsEntry._ID,
                                PetsEntry.COLUMN_PET_NAME,
                                PetsEntry.COLUMN_PET_BREED,
                                PetsEntry.COLUMN_PET_GENDER,
                                PetsEntry.COLUMN_PET_WEIGHT };

        Uri newURI =Uri.parse("content://com.example.android.pets/pets/#2");
     Log.i(LOG_TAG,"URI:"+petUri);

        return new CursorLoader(this,   // Parent activity context
               petUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {



        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }




        if (cursor.moveToFirst()) {

            Log.i(LOG_TAG, "How many:" + cursor.getCount());

            // Find the columns of pet attributes that we're interested in
                        int nameColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_NAME);
                       int breedColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_BREED);
                        int genderColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_GENDER);
                        int weightColumnIndex = cursor.getColumnIndex(PetsEntry.COLUMN_PET_WEIGHT);

            // Extract out the value from the Cursor for the given column inde
                       String name = cursor.getString(nameColumnIndex);
                        String breed = cursor.getString(breedColumnIndex);
                        int gender = cursor.getInt(genderColumnIndex);
                        int weight = cursor.getInt(weightColumnIndex);

                               // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(Integer.toString(weight));
            mGenderSpinner.setSelection(gender,true);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
                mNameEditText.setText("");
                mBreedEditText.setText("");
               mWeightEditText.setText("");
               mGenderSpinner.setSelection(0); // Select "Unknown" gender

    }
}