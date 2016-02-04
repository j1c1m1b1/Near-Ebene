package com.training.prodigious.nearebene;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Julio Mendoza on 2/3/16.
 */
public class MainActivity extends AppCompatActivity {

    private Pattern pattern;

    private boolean userNameValid;
    private boolean passwordValid;

    private EditText etUserName;
    private EditText etPassword;

    private TextInputLayout tiUserName;
    private TextInputLayout tiPassword;

    private Button btnOk;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        String emailPattern = getString(R.string.email_pattern);

        etUserName = (EditText) findViewById(R.id.etUserName);

        tiUserName = (TextInputLayout) findViewById(R.id.tiUserName);

        tiUserName.setHint(getString(R.string.userName));

        etPassword = (EditText) findViewById(R.id.etPassword);

        tiPassword = (TextInputLayout) findViewById(R.id.tiPassword);

        btnOk = (Button) findViewById(R.id.btnOk);

        pattern = Pattern.compile(emailPattern);

        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = etUserName.getText().toString();
                if (etUserName.getText().toString().length() < 8) {
                    tiUserName.setError(getString(R.string.username_error));
                    userNameValid = false;
                } else {
                    userNameValid = true;
                    tiUserName.setError(null);
                }
            }
        });


        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etPassword.getText().toString().length() < 8) {
                    passwordValid = false;
                    tiPassword.setError(getString(R.string.username_error));
                } else {
                    passwordValid = true;
                    tiPassword.setError(null);
                }


                if (passwordValid && userNameValid) {
                    btnOk.setEnabled(true);
                }
            }
        });

        tiPassword.setHint(getString(R.string.password));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
