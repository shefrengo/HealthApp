package com.shefrengo.health;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
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
        forgortPassword.setOnClickListener(this);
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
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
       if (e instanceof FirebaseAuthInvalidUserException){
           Toast.makeText(LoginActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
           return;
       }
            Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onClick(View v) {
        if (v == signUp) {
            startActivity(new Intent(LoginActivity.this, SignUp.class));
            finish();
        } else if (v == logIn) {
            signIn();
        } else if (v == forgortPassword) {
            setDailog();
        }
    }

    private void setDailog() {

        android.app.Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.forgot_password_layout);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.show();
        MaterialButton button = dialog.findViewById(R.id.btnForgotPassword);
        EditText editText = dialog.findViewById(R.id.edtForgotEmaildiag);


        ProgressBar progressBar = dialog.findViewById(R.id.password_progress);
        button.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = editText.getText().toString().trim();

            if (email.isEmpty()) {

                progressBar.setVisibility(View.GONE);
                editText.setError("Provide an email");
                return;
            }

            auth.sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Please check your email", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                dialog.cancel();
            }).addOnFailureListener(e -> {
                Log.e(TAG, "onFailure: ",e );
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            });
        });
    }
}