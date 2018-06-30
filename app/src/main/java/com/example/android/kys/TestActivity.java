package com.example.android.kys;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.support.design.widget.TabLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestActivity extends AppCompatActivity {
    // The nulled pointers are the ones that won't necessarily have a value.
    private Context this_context;
    private TextView person_text_view = null;
    private ViewPager person_images_view = null;
    private ProgressBar loading_indicator = null;
    private TabLayout dots_tab = null;
    // This is a "View" array because "View" is the common ancestor of "Button" and "ImageButton".
    private View buttons[] = new View[4];
    private int chosen_person_index;
    private Random rand = new Random();
    private PersonRepo repo;
    private SparseArray<String> ids_and_names;
    private int image_position = 0;
    private int started_image_position;

    private String number_question_value;
    private String name_question_value;
    private String picture_question_value;
    private String question_type;
    private String answers_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        this_context = this;

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        question_type = bundle.getString(getString(R.string.question));
        answers_type = bundle.getString(getString(R.string.answers));

        number_question_value = getResources().getStringArray(R.array.questions_values)[0];
        name_question_value = getResources().getStringArray(R.array.questions_values)[1];
        picture_question_value = getResources().getStringArray(R.array.questions_values)[2];

        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout parent =
                (RelativeLayout) inflater.inflate(R.layout.activity_test, null);

        View question;
        View answers;

        if (question_type.equals(picture_question_value)) {
            question = inflater.inflate(R.layout.question_picture, null);
        } else {
            question = inflater.inflate(R.layout.question_text, null);
        }

        if (answers_type.equals(picture_question_value)) {
            answers = inflater.inflate(R.layout.answers_picture, null);
        } else {
            answers = inflater.inflate(R.layout.answers_text, null);
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (!answers_type.equals(picture_question_value)) {
            params.addRule(RelativeLayout.ABOVE, R.id.answers_texts_demo);
        }

        parent.addView(question, params);
        parent.addView(answers);
        setContentView(parent);

        repo = new PersonRepo(getBaseContext());
        ids_and_names = repo.getAllPeople();

        if (question_type.equals(picture_question_value)) {
            person_images_view = findViewById(R.id.view_person_images);
            dots_tab = findViewById(R.id.tab_dots);

            loading_indicator = findViewById(R.id.pb_loading_indicator);
        } else {
            person_text_view = findViewById(R.id.text_person_text);
        }

        if (answers_type.equals(picture_question_value)) {
            buttons[0] = findViewById(R.id.button_picture_1);
            buttons[1] = findViewById(R.id.button_picture_2);
            buttons[2] = findViewById(R.id.button_picture_3);
            buttons[3] = findViewById(R.id.button_picture_4);
        } else {
            buttons[0] = findViewById(R.id.button_text_1);
            buttons[1] = findViewById(R.id.button_text_2);
            buttons[2] = findViewById(R.id.button_text_3);
            buttons[3] = findViewById(R.id.button_text_4);
        }

        new GenerateNewQuestionTask().execute();
    }

    private void enable_buttons(boolean enable) {
        for (View button: buttons) {
            button.setEnabled(enable);
        }
    }

    public class GenerateNewQuestionTask extends AsyncTask<Void, Void, ArrayList<Bitmap>> {
        private List<Integer> shuffled_ids = new ArrayList<>();
        private int chosen_person_id;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (loading_indicator != null) {
                loading_indicator.setVisibility(View.VISIBLE);
            }

            // Shuffle the wanted keys to choose from.
            for (int i = 0; i < ids_and_names.size(); i++) {
                int id = ids_and_names.keyAt(i);
                shuffled_ids.add(id);
            }
            Collections.shuffle(shuffled_ids);

            chosen_person_index = rand.nextInt(buttons.length);
            chosen_person_id = shuffled_ids.get(chosen_person_index);
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(Void... params) {
            //try {Thread.sleep(1000);} catch (InterruptedException e) {}

            if (question_type.equals(picture_question_value)) {
                return repo.getPicturesByID(chosen_person_id);
            }

            if (answers_type.equals(picture_question_value)) {
                return repo.getFirstPicturesByIDs(shuffled_ids.subList(0, 4));
            }

            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> pictures) {
            // The pictures are either all of the pictures of the questioned person or
            // the pictures of all the answers persons.

            for (int i = 0; i < buttons.length; ++i) {
                if (answers_type.equals(picture_question_value)) {
                    buttons[i].setAlpha((float)1);
                    ((ImageButton)buttons[i]).setImageBitmap(pictures.get(i));
                } else {
                    ((Button)buttons[i]).setTextColor(Color.BLACK);
                    if (answers_type.equals(number_question_value)) {
                        ((Button)buttons[i]).setText(Integer.toString(shuffled_ids.get(i)));

                    } else if (answers_type.equals(name_question_value)) {
                        ((Button)buttons[i]).setText(ids_and_names.get(shuffled_ids.get(i)));
                    }

                }


                if (chosen_person_index == i) {
                    buttons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            enable_buttons(false);
                            if (answers_type.equals(picture_question_value)) {

                            } else {
                                ((Button) view).setTextColor(Color.rgb(0, 153, 51));
                            }

                            new GenerateNewQuestionTask().execute();
                        }
                    });
                } else {
                    buttons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (answers_type.equals(picture_question_value)) {
                                view.setAlpha((float)0.4);
                            } else {
                                ((Button) view).setTextColor(Color.RED);
                            }
                        }
                    });
                }
            }

            if (question_type.equals(number_question_value)) {
                person_text_view.setText(String.valueOf(chosen_person_id));

            } else if (question_type.equals(name_question_value)) {
                person_text_view.setText(ids_and_names.get(chosen_person_id));

            } else if (question_type.equals(picture_question_value)) {
                // Saving the position in the pictures so after the change we will
                // move it to the same place.
                int cur_pos = person_images_view.getCurrentItem();
                if (started_image_position != cur_pos) {
                    image_position = cur_pos;
                }

                ImageAdapter imageAdapter = new ImageAdapter(
                        this_context,
                        pictures);
                person_images_view.setAdapter(imageAdapter);
                person_images_view.setCurrentItem(image_position);
                started_image_position = person_images_view.getCurrentItem();

                dots_tab.setupWithViewPager(person_images_view, true);

                if (loading_indicator != null) {
                    loading_indicator.setVisibility(View.INVISIBLE);
                }
            }

            enable_buttons(true);
        }
    }
}
