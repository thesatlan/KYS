package com.example.android.kys;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PersonRepo {
    private DatabaseHelper dbHelper;

    public PersonRepo(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public static int toInt(byte[] bytes, int offset) {
        int ret = 0;

        for (int i = 0; i < 4 && offset + i < bytes.length; i++) {
            ret <<= 8;
            ret |= (int)bytes[offset + i] & 0xFF;
        }
        return ret;
    }

    public SparseArray<String> getAllPeople() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String getPeopleQuery =  "SELECT " +
                Person.KEY_ID + "," + Person.KEY_NAME +
                " FROM " + Person.TABLE_NAME;

        SparseArray<String> ids_and_names = new SparseArray<>();

        Cursor cursor = db.rawQuery(getPeopleQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ids_and_names.put(cursor.getInt(cursor.getColumnIndex(Person.KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(Person.KEY_NAME)));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return ids_and_names;
    }

    public ArrayList<Bitmap> getFirstPicturesByIDs(List<Integer> persons_ids) {
        StringBuilder persons_ids_string = new StringBuilder("(");
        String prefix = "";
        for (Integer i: persons_ids) {
            persons_ids_string.append(prefix);
            persons_ids_string.append(i.toString());
            prefix = ",";
        }
        persons_ids_string.append(")");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT " +
                Person.KEY_ID + ", " + Person.KEY_PICTURES +
                " FROM " + Person.TABLE_NAME +
                " WHERE " + Person.KEY_ID + " in " + persons_ids_string.toString();

        Bitmap[] pictures = new Bitmap[persons_ids.size()];

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                byte[] blob = cursor.getBlob(cursor.getColumnIndex(Person.KEY_PICTURES));

                int image_length = toInt(blob, 0);

                int test = persons_ids.indexOf(cursor.getInt(cursor.getColumnIndex(Person.KEY_ID)));
                pictures[persons_ids.indexOf(
                        cursor.getInt(cursor.getColumnIndex(Person.KEY_ID)))] =
                        BitmapFactory.decodeByteArray(blob, 4, image_length);

            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return new ArrayList<>(Arrays.asList(pictures));
    }

    public ArrayList<Bitmap> getPicturesByID(int person_id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT " +
                Person.KEY_PICTURES +
                " FROM " + Person.TABLE_NAME +
                " WHERE " + Person.KEY_ID + " = " + Integer.toString(person_id);

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        ArrayList<Bitmap> pictures = new ArrayList<>();

        // We need a full profile only to the chosen person.
        byte[] blob = cursor.getBlob(cursor.getColumnIndex(Person.KEY_PICTURES));
        int offset = 0;

        // Getting the images
        while (offset < blob.length) {
            int image_length = toInt(blob, offset);
            pictures.add(BitmapFactory.decodeByteArray(blob,
                    offset + 4,
                    image_length));
            offset += image_length + 4;
        }

        cursor.close();
        db.close();

        return pictures;
    }
}

