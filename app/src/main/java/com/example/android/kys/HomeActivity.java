package com.example.android.kys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    private Button picture_learn_button;
    private Button picture_test_button;
    private Spinner questions_type_spinner;
    private Spinner answers_type_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        picture_learn_button = findViewById(R.id.button_open_learn);
        picture_test_button = findViewById(R.id.button_open_test);
        questions_type_spinner = findViewById(R.id.spinner_test_question);
        answers_type_spinner = findViewById(R.id.spinner_test_answers);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                getResources().getStringArray(R.array.questions_values));
        questions_type_spinner.setAdapter(spinnerArrayAdapter);
        answers_type_spinner.setAdapter(spinnerArrayAdapter);

        questions_type_spinner.setSelection(2);
        answers_type_spinner.setSelection(1);

        picture_learn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startLearnActivity =
                        new Intent(view.getContext(), LearnActivity.class);
                startActivity(startLearnActivity);
            }
        });

        picture_test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question_type = questions_type_spinner.getSelectedItem().toString();
                String answers_type = answers_type_spinner.getSelectedItem().toString();

                if (question_type.equals(answers_type)) {
                    Toast.makeText(getApplicationContext(),
                            "Can't test on the same values.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent startPictureTestActivity =
                        new Intent(view.getContext(), TestActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.question), question_type);
                bundle.putString(getString(R.string.answers), answers_type);
                startPictureTestActivity.putExtras(bundle);

                startActivity(startPictureTestActivity);
            }
        });
    }
}
