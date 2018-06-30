package com.example.android.kys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private Button login_button;
    private TextView password_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(0, 0);

        login_button = findViewById(R.id.login_button);
        password_text = findViewById(R.id.password_text);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password_text.getText().toString().equals(
                        getResources().getString(R.string.app_password)))
                {
                    Intent startHomeActivity =
                            new Intent(view.getContext(), HomeActivity.class);

                    startActivity(startHomeActivity);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Wrong password!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
