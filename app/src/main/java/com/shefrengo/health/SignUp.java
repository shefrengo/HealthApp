package com.shefrengo.health;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.shefrengo.health.Models.MyCommunities;
import com.shefrengo.health.Models.Users;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private EditText nameEdit, surnameEdit, usernameEdit, passwordEdit, confirmPasswordEdit, emailEdit;
    private Button signUpBtn, logInBtn;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("Users");
    private static final String TAG = "SignUp";
    private CircleImageView circleImageView;
    private Uri profileUri;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        circleImageView = findViewById(R.id.select_profile);
        nameEdit = findViewById(R.id.edtFirstName);
        surnameEdit = findViewById(R.id.edtLastName);
        usernameEdit = findViewById(R.id.username);
        passwordEdit = findViewById(R.id.edtPassword);
        confirmPasswordEdit = findViewById(R.id.edtConfirmPassword);
        emailEdit = findViewById(R.id.edtEmail);
        progressBar = findViewById(R.id.upload_progress);
        signUpBtn = findViewById(R.id.btnSignUp);
        logInBtn = findViewById(R.id.btnSignIn);

        signUpBtn.setOnClickListener(this);
        logInBtn.setOnClickListener(this);
        circleImageView.setOnClickListener(this);
    }

    private void validate() {

        Log.d(TAG, "validate: click button");

        final String name = nameEdit.getText().toString().trim();
        final String surname = surnameEdit.getText().toString().trim();
        final String username = usernameEdit.getText().toString().trim();
        final String password = passwordEdit.getText().toString().trim();
        final String confirmPassword = confirmPasswordEdit.getText().toString().trim();
        final String email = emailEdit.getText().toString().trim();
        if (name.isEmpty()) {
            nameEdit.setError(getResources().getString(R.string.enter_field));
            nameEdit.requestFocus();
            return;
        }
        if (surname.isEmpty()) {
            usernameEdit.setError(getResources().getString(R.string.enter_field));
            usernameEdit.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            emailEdit.setError(getResources().getString(R.string.enter_field));
            emailEdit.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            usernameEdit.setError(getResources().getString(R.string.enter_field));
            usernameEdit.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordEdit.setError(getResources().getString(R.string.enter_field));
            passwordEdit.requestFocus();
            return;
        }
        if (password.length() < 8) {
            passwordEdit.setError(getResources().getString(R.string.password_legth));
            confirmPasswordEdit.requestFocus();
            return;
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordEdit.setError(getResources().getString(R.string.enter_field));
            confirmPasswordEdit.requestFocus();
            return;
        }
        if (profileUri == null) {
            Toast.makeText(this, "Please upload an avatar", Toast.LENGTH_LONG).show();
            return;
        }

        if (!confirmPassword.equals(password)) {
            confirmPasswordEdit.requestFocus();
            confirmPasswordEdit.setError(getResources().getString(R.string.password_not_matching));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(authResult -> uploadToFirebaseStorage(email, name, username, password, surname)).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: ",e );
            Toast.makeText(SignUp.this, e.toString(), Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public void onClick(View v) {
        if (v == signUpBtn) {
            validate();
        } else if (v == logInBtn) {
            startActivity(new Intent(SignUp.this, LoginActivity.class));
            finish();
        } else if (v == circleImageView) {
            setImage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                profileUri = result.getUri();
                Glide.with(this).asBitmap().placeholder(R.drawable.ic_app_background)
                        .load(profileUri).into(circleImageView);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setImage() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SignUp.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SignUp.this);
            }
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(SignUp.this);
        }

    }

    private void uploadInfo(String uri, String username, String password, String name, String surname, String email) {
        final Query query = userRef.whereEqualTo("username", username);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {

            if (!queryDocumentSnapshots.isEmpty()) {
                Log.d(TAG, "user already exists for singing up: ");
                usernameEdit.requestFocus();
                usernameEdit.setError(getResources().getString(R.string.user_exits));
            } else {
                Log.d(TAG, "validate:no user found ");
                FirebaseUser firebaseUser = auth.getCurrentUser();
                Log.d(TAG, "user created : successfully ");
                Users users1 = new Users();
                users1.setPassword(password);
                users1.setUsername(username);
                users1.setName(name);
                users1.setSurname(surname);
                assert firebaseUser != null;
                users1.setUserId(firebaseUser.getUid());
                users1.setPosts(0);
                users1.setEmail(email);
                users1.setFollowers(0);
                users1.setAdmin(false);
                users1.setProfilePhotoUrl(uri);
                users1.setCommunities(0);
                users1.setFollowing(0);
                userRef.document(firebaseUser.getUid()).set(users1).addOnSuccessListener(aVoid -> {


                    MyCommunities myCommunities = new MyCommunities();
                    myCommunities.setUserid(firebaseUser.getUid());
                    myCommunities.setCommunityId(getResources().getString(R.string.health_community));
                    db.collection("Users").document(firebaseUser.getUid()).collection("MyCommunities")
                            .document(getResources().getString(R.string.health_community)).set(myCommunities).addOnSuccessListener(aVoid1 -> {
                        Intent intent = new Intent(SignUp.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    });


                }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));


            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: ", e);
            ;
        });
        
    }

    private static final int ONE_BILLION = 100000000;
    private StorageTask storageTask;

    private void uploadToFirebaseStorage(String email, String name, String username, String password, String surname) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Avatars/");
        Random random = new Random();
        int randomNum = random.nextInt(ONE_BILLION);
        final StorageReference uniquename = storageReference.child(System.currentTimeMillis() + randomNum + ".jpg");
        storageTask = uniquename.putFile(profileUri).addOnSuccessListener(taskSnapshot -> {
            Handler handler = new Handler();
            progressBar.setVisibility(View.VISIBLE);
            handler.postDelayed(() -> progressBar.setProgress(0), 5000);
            uniquename.getDownloadUrl().addOnSuccessListener(uri1 -> {
                String downloadUri = uri1.toString();

                uploadInfo(downloadUri, username, password, name, surname, email);
            });

        }).addOnProgressListener(taskSnapshot -> {
            progressBar.setVisibility(View.VISIBLE);
            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
            progressBar.setProgress((int) progress);

        }).addOnFailureListener(e -> Toast.makeText(SignUp.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());

    }

}