package com.example.android.kys;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Person {
    public static final String TABLE_NAME = "people";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PICTURES = "pictures";
    private String name;
    private ArrayList<Bitmap> images;

   public Person(String name, ArrayList<Bitmap> images)
   {
       this.name = name;
       this.images = images;
   }

    public ArrayList<Bitmap> getImages() {
        return images;
    }

    public String getName() {
        return name;
    }
}
