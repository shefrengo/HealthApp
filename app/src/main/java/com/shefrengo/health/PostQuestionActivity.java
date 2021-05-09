package com.shefrengo.health;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import com.shefrengo.health.Models.Posts;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

public class PostQuestionActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView privacyTip;
    private MaterialButton postBtn;
    private EditText titleEdit, descriptionEdit;
    private RelativeLayout relativeLayout;
    private Uri profileImageUri;
    private ImageView imageView;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private static final String TAG = "PostQuestionActivity";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;

    private String community;
    private String id;
    private Intent intent;
    private StorageTask storageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_question);
        intent = getIntent();
        progressBar = findViewById(R.id.progressbar);
        imageView = findViewById(R.id.addImage);
        privacyTip = findViewById(R.id.privacy_tip_text);
        postBtn = findViewById(R.id.postbtn);
        titleEdit = findViewById(R.id.qestion_title_editext);
        descriptionEdit = findViewById(R.id.question_post_edit);
        relativeLayout = findViewById(R.id.add_iamge_relative);
        relativeLayout.setOnClickListener(this);
        postBtn.setOnClickListener(this);
        privacyTip.setOnClickListener(this);
        if (intent.hasExtra("community")) {
            community = intent.getStringExtra("community");
        }
        if (intent.hasExtra("communityId")) {
            id = intent.getStringExtra("communityId");
        }


    }

    private void validate() {
        String title = titleEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();
        if (title.isEmpty()) {
            titleEdit.requestFocus();
            titleEdit.setError(getResources().getString(R.string.enter_field));
            return;
        }

        if (description.isEmpty()) {
            descriptionEdit.requestFocus();
            descriptionEdit.setError(getResources().getString(R.string.enter_field));
            return;
        }

        if (storageTask != null && storageTask.isInProgress()) {
            Toast.makeText(PostQuestionActivity.this, "upload is in progress", Toast.LENGTH_SHORT).show();
            return;
        }
        if (profileImageUri == null) {
            uploadPostInfo("");
        } else {
            progressBar.setVisibility(View.VISIBLE);
            uploadToFirebaseStorage();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == privacyTip) {

            displayTip();
        } else if (v == postBtn) {
            validate();
        } else if (v == relativeLayout) {

            setImage();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void displayTip() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.tip_layout);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.show();
    }


    private static final int ONE_BILLION = 100000000;

    private void uploadToFirebaseStorage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Photos/" + user.getUid());
        Random random = new Random();
        int randomNum = random.nextInt(ONE_BILLION);
        final StorageReference uniquename = storageReference.child(System.currentTimeMillis() + randomNum + user.getUid() + ".jpg");
        storageTask = uniquename.putFile(profileImageUri).addOnSuccessListener(taskSnapshot -> {
            Handler handler = new Handler();
            handler.postDelayed(() -> progressBar.setProgress(0), 5000);
            uniquename.getDownloadUrl().addOnSuccessListener(uri1 -> {
                String downloadUri = uri1.toString();
                uploadPostInfo(downloadUri);

            });

        }).addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
            progressBar.setProgress((int) progress);

        }).addOnFailureListener(e -> Toast.makeText(PostQuestionActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());

    }

    private void uploadPostInfo(String imageUri) {
        CollectionReference collectionReference = db.collection("Posts");
        String title = titleEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();
        Posts posts = new Posts();
        posts.setCategory(community);
        posts.setDescription(description);
        posts.setReplyCount(0);
        posts.setCommunity(id);
        posts.setTitle(title);
        posts.setUserid(user.getUid());
        posts.setImageUrl(imageUri);
        collectionReference.add(posts).addOnSuccessListener(documentReference -> {

            db.collection("Users").document(user.getUid()).update("posts", FieldValue.increment(1));
            Toast.makeText(this, "Posted", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PostQuestionActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                profileImageUri = result.getUri();
                Glide.with(this).asBitmap().placeholder(R.drawable.ic_app_background)
                        .load(profileImageUri).into(imageView);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setImage() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(PostQuestionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PostQuestionActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(PostQuestionActivity.this);
            }
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(PostQuestionActivity.this);
        }

    }


}