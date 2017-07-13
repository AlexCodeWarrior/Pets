package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.security.Provider;
import java.util.IllegalFormatException;

import com.example.android.pets.data.PetContract.PetsEntry;
/**
 * Serve as a layer of abstract between the database and the activity
 */

public class PetProvider extends ContentProvider {

    public static final String LOG_TAG = Provider.class.getSimpleName();
    public static final int PETS = 100 ;
    public static final int PET_ID = 101;
    private PetDbHelper mDbHelper;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

     //identify which format was given
    static{
       sURIMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,PETS);

        sURIMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS + "/#",PETS);
    }


    /**
     * Initialize the provider and the database helper object.
     */
   @Override
    public boolean onCreate() {
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.

          mDbHelper = new PetDbHelper(getContext());
        return true;
    }


    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;
        int match = sURIMatcher.match(uri);
        switch(match){
            case PETS:
                 cursor = database.query(PetsEntry.TABLE_NAME,projection,null,null,null,null,sortOrder);
                break;
            case PET_ID:
                selection = PetsEntry._ID+"=/?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetsEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw  new IllegalArgumentException("CANNOT QUERY URI" + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sURIMatcher.match(uri);

        switch(match){
            case PETS:
                return PetsEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI" + uri +"with match"+ match);
        }

    }


    /**
     * Insert new data into the provider with the given ContentValues.
     */

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final int match = sURIMatcher.match(uri);
        switch (match){
            case PETS:
                return insertPet( uri , contentValues);
            default:
                throw  new IllegalArgumentException("Insertion not supported for" + uri);
        }

    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {



            // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sURIMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                return database.delete(PetsEntry.TABLE_NAME, selection, selectionArgs);
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(PetsEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }



    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sURIMatcher.match(uri);

        switch (match){
            case PETS:
                return updatePet (uri , contentValues ,selection,selectionArgs);
            case PET_ID:
                selection = PetsEntry._ID+"=?";
                selectionArgs = new String [] {String.valueOf(ContentUris.parseId(uri))};
                return updatePet (uri , contentValues ,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update not supperted"+uri);
        }
    }

   private  Uri insertPet(Uri uri , ContentValues values){
        //Sanity check

       String name = values.getAsString(PetsEntry.COLUMN_PET_NAME);

       if (name == null || name.length() == 0){
           throw new IllegalArgumentException("Pet requires a name");
       }


       Integer weight  = values.getAsInteger(PetsEntry.COLUMN_PET_WEIGHT);

       if (weight!= null && weight < 0){
           throw new IllegalArgumentException("Pet requires a valid weight ");
       }


       Uri returnUri;

       //Get a writable database
       SQLiteDatabase database = mDbHelper.getWritableDatabase();

       //insert the new pet in the given value
       long rowID = database.insert(PetsEntry.TABLE_NAME,null,values);
       Log.e( LOG_TAG,"URI bEFORE: " + uri);
       Log.e( LOG_TAG,"ID: " + rowID);

        if(rowID == -1 ){
            Log.e(LOG_TAG, "Failed to insert row  " + uri);
            throw new android.database.SQLException("Failed to insert row into " + uri);
        }

           returnUri = ContentUris.withAppendedId(uri,rowID);
           Log.e(LOG_TAG, "Returned as is " + returnUri);
       //return Uri with ID
           return  returnUri;
   }

   private int updatePet(Uri uri , ContentValues values , String selection , String[] selectionArgs){
       //Get a writable database

       if( values.containsKey(PetsEntry.COLUMN_PET_NAME)){
           String name = values.getAsString(PetsEntry.COLUMN_PET_NAME);
           if (name == null){
               throw  new IllegalArgumentException("Pet requires a name ");
           }
       }

       if ( values.containsKey(PetsEntry.COLUMN_PET_GENDER)){
           Integer gender = values.getAsInteger(PetsEntry.COLUMN_PET_GENDER);
           if ( gender == null || !PetsEntry.isValidGender(gender)){
               throw  new IllegalArgumentException("Pet requires gender ");
           }
       }

       if ( values.containsKey(PetsEntry.COLUMN_PET_WEIGHT)){
           Integer weight = values.getAsInteger(PetsEntry.COLUMN_PET_WEIGHT);
           if ( weight != null && weight < 0){
               throw  new IllegalArgumentException("Invalid et weight ");
           }
       }

       if ( values. size() == 0 ){
           return 0 ;
       }

       int count = 0 ;

       SQLiteDatabase database = mDbHelper.getWritableDatabase();

       count  = database.update(PetsEntry.TABLE_NAME,values,selection,selectionArgs);

       return count ;
   }
}
