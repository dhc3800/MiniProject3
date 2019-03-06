package com.dhc3800.mp3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText password;
    private EditText email;
    private TextView login;
    private EditText password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        final Button signup = findViewById(R.id.SignUp);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        email = findViewById(R.id.email);
        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, MainActivity.class));
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = password.getText().toString();
                String pass2 = password2.getText().toString();
                if (pass.length() < 6) {
                    Toast.makeText(SignUp.this, "Password is not strong enough", Toast.LENGTH_LONG).show();
                } else if (!pass.equals(pass2)) {
                    Toast.makeText(SignUp.this, "Passwords do not match" + password.getText().toString(), Toast.LENGTH_LONG).show();
                } else {
                    signUp(email.getText().toString(), password.getText().toString());
                }
            }
        });


    }

    /**
     * signing the user up with firebase, assigining a unique id and uploading the information to the database
     * @param email
     * @param pass
     */
    public void signUp(String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(SignUp.this, EventsPage.class));
                } else {
                    Toast.makeText(SignUp.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}

