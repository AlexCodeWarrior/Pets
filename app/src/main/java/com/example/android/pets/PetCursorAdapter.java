package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;

/**
 * Created by at on 7/13/17.
 */

public class PetCursorAdapter extends CursorAdapter{

    public PetCursorAdapter(Context context , Cursor c){
        super(context,c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameText = (TextView) view.findViewById(R.id.name);
        TextView summaryText = (TextView) view.findViewById(R.id.summary);

       String name = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetsEntry.COLUMN_PET_NAME));
        String breed = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetsEntry.COLUMN_PET_BREED));

        nameText.setText(name);
        summaryText.setText(breed);


    }


}
