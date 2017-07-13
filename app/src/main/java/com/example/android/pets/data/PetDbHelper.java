package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.pets.data.PetContract.PetsEntry;

import static com.example.android.pets.data.PetContract.PetsEntry.COLUMN_PET_NAME;

/**
 * Created a database with sqlite
 */

public class PetDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME ="shelter.db";

    public PetDbHelper(Context context){
        super (context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    //database created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db,oldVersion,newVersion);
    }

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PetsEntry.TABLE_NAME;


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PetsEntry.TABLE_NAME + " (" +
                    PetsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PetsEntry.COLUMN_PET_NAME + " TEXT NOT NULL," +
                    PetsEntry.COLUMN_PET_BREED + " TEXT,"+
                    PetsEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "+
                    PetsEntry.COLUMN_PET_WEIGHT + "  INTEGER NOT NULL DEFAULT 0 );" ;
}
