package com.shefrengo.health;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.shefrengo.health.Models.Communities;
import com.shefrengo.health.Models.Posts;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Admin extends AppCompatActivity implements View.OnClickListener {

    private MaterialButton postBtn;
    private EditText titleEdit, descriptionEdit;
    private RelativeLayout relativeLayout;
    private Uri profileImageUri;
    private ImageView imageView;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private static final String TAG = "Admin";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;
    private StorageTask storageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        progressBar = findViewById(R.id.progressbar);
        imageView = findViewById(R.id.addImage);
        postBtn = findViewById(R.id.postbtn);
        titleEdit = findViewById(R.id.qestion_title_editext);
        descriptionEdit = findViewById(R.id.question_post_edit);
        relativeLayout = findViewById(R.id.add_iamge_relative);
        relativeLayout.setOnClickListener(this);
        postBtn.setOnClickListener(this);


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
            Toast.makeText(Admin.this, "upload is in progress", Toast.LENGTH_SHORT).show();
            return;
        }
        if (profileImageUri == null) {
            Toast.makeText(this, "Select Photo", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        uploadToFirebaseStorage();

    }

    @Override
    public void onClick(View v) {
        if (v == postBtn) {
            validate();
        } else if (v == relativeLayout) {
            setImage();
        }
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
        }).addOnFailureListener(e -> Toast.makeText(Admin.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());

    }

    private void uploadPostInfo(String imageUri) {

        CollectionReference collectionReference = db.collection("Communities");
        String title = titleEdit.getText().toString().trim();
        String description = descriptionEdit.getText().toString().trim();
        Communities communities = new Communities();
        communities.setName(title);
        communities.setDescription(description);
        communities.setPosts(0);
        communities.setAdminUserid(user.getUid());
        communities.setMembers(0);
        communities.setCommunityId(StringManipulation.getSaltString());
        communities.setImageUrl(imageUri);
        collectionReference.add(communities).addOnSuccessListener(documentReference -> {


            Client client = new Client(getString(R.string.Agloia_application_id), getString(R.string.Admiin_api_key));
            Index index = client.getIndex(getString(R.string.agolia_index_name));
            List<JSONObject> communityList = new ArrayList<JSONObject>();

            try {
                communityList.add(new JSONObject()
                        .put("name", title)
                        .put("profilePhotoUrl", imageUri)
                        .put("objectID", documentReference.getId()));


            } catch (JSONException e) {
                Toast.makeText(Admin.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            index.addObjectsAsync(new JSONArray(communityList), (jsonObject, e) -> {
                Toast.makeText(Admin.this, "Posted", Toast.LENGTH_SHORT).show();
                finish();
            });

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
            if (ContextCompat.checkSelfPermission(Admin.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Admin.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Admin.this);
            }
        } else {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(Admin.this);
        }

    }

}