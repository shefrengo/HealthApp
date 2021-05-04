package com.shefrengo.health;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView forgortPassword;
    private EditText emailEdit;
    private EditText passwordEdit;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button signUp, logIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        forgortPassword = findViewById(R.id.tvForget);
        passwordEdit = findViewById(R.id.edtPassword);
        emailEdit = findViewById(R.id.edtEmail);
        auth = FirebaseAuth.getInstance();
        signUp = findViewById(R.id.btnSignUp);
        progressBar = findViewById(R.id.login_progress);
        logIn = findViewById(R.id.btnSignIn);
        logIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
    }

    private void signIn() {
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        if (email.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            emailEdit.setError(getResources().getString(R.string.enter_field));
            emailEdit.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            passwordEdit.setError(getResources().getString(R.string.enter_field));
            passwordEdit.requestFocus();
            return;
        }
        if (password.length() < 8) {
            progressBar.setVisibility(View.GONE);
            passwordEdit.setError(getResources().getString(R.string.password_legth));
            passwordEdit.requestFocus();
            return;
        }
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }).addOnFailureListener(e -> Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onClick(View v) {
        if (v == signUp) {
            startActivity(new Intent(LoginActivity.this, SignUp.class));
            finish();
        } else if (v == logIn) {
            signIn();
        }
    }
}