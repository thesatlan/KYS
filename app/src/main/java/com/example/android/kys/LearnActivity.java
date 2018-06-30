package com.example.android.kys;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.support.design.widget.TabLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class LearnActivity extends AppCompatActivity {
    private Context this_context;
    private ViewPager person_images_view;
    private ProgressBar mLoadingIndicator;
    private TabLayout dots_tab;
    private TextView person_name_text;
    private TextView person_number_text;
    private Button prev_button;
    private Button next_button;
    private PersonRepo repo;
    private SparseArray<String> ids_and_names;
    private int image_position = 0;
    private int started_image_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        this_context = this;

        repo = new PersonRepo(getBaseContext());
        ids_and_names = repo.getAllPeople();

        person_images_view = findViewById(R.id.view_person_images);
        dots_tab = findViewById(R.id.tab_dots);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        person_name_text = findViewById(R.id.person_name);
        person_number_text = findViewById(R.id.person_number);

        prev_button = findViewById(R.id.button_prev);
        next_button = findViewById(R.id.button_next);

        new GenerateNewPersonTask().execute(1);
    }

    public class GenerateNewPersonTask extends AsyncTask<Integer, Void, ArrayList<Bitmap>> {
        Integer current_person_number;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(Integer... params) {
            //try {Thread.sleep(1000);} catch (InterruptedException e) {}

            current_person_number = params[0];
            return repo.getPicturesByID(current_person_number);
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> chosen_person_pictures) {
            person_number_text.setText(current_person_number.toString());
            person_name_text.setText(ids_and_names.get(current_person_number));

            prev_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (current_person_number > 1) {
                        new GenerateNewPersonTask().execute(current_person_number - 1);
                    }
                }
            });
            next_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (current_person_number < ids_and_names.size()) {
                        new GenerateNewPersonTask().execute(current_person_number + 1);
                    }
                }
            });

            // Saving the position in the pictures so after the change we will
            // move it to the same place.
            int cur_pos = person_images_view.getCurrentItem();
            if (started_image_position != cur_pos) {
                image_position = cur_pos;
            }

            ImageAdapter imageAdapter = new ImageAdapter(
                    this_context,
                    chosen_person_pictures);
            person_images_view.setAdapter(imageAdapter);
            person_images_view.setCurrentItem(image_position);
            started_image_position = person_images_view.getCurrentItem();

            dots_tab.setupWithViewPager(person_images_view, true);

            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }
}
