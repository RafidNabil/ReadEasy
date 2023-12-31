package com.example.readeasy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;


import com.example.readeasy.Models.ModelCategory;
import com.example.readeasy.databinding.UploadPageBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class UploadActivity extends AppCompatActivity {

    //view Binding
    private UploadPageBinding uploadPageBinding;

    //Firebase Auth
    private FirebaseAuth firebaseAuth;

    //Categories arraylist
    ArrayList<String> categoryTitleArrayList, categoryIdArrayList;

    //Progress Dialog
    private ProgressDialog progressDialog;

    //TAG for debugging
    private static final String TAG = "ADD_PDF_TAG";

    private static final int PDF_PICK_CODE = 1000;

    private Uri pdfUri = null;
    private Uri coverUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.upload_page);
        uploadPageBinding = UploadPageBinding.inflate(getLayoutInflater());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(uploadPageBinding.getRoot());

        uploadPageBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Init Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        loadPdfCategories();

        //Configure Progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        //Begin upload category
        uploadPageBinding.categoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        uploadPageBinding.pdfUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateUploadData();
            }
        });

        uploadPageBinding.fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PdfPick();
            }
        });

        uploadPageBinding.pdfCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryPickDialog();
            }
        });

        uploadPageBinding.pdfCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoverPick();
            }
        });

    }

    String category = "";

    private void validate() {
        //Validate data

        category = uploadPageBinding.categoryInput.getText().toString().trim();

        //validate if empty
        if (TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Please insert category", Toast.LENGTH_SHORT).show();
        } else {
            addCategory();
        }
    }

    private void addCategory() {
        //show progress
        progressDialog.setMessage("Adding category...");
        progressDialog.show();

        //get timestamp
        long timestamp = System.currentTimeMillis();

        //set up info
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", "" + timestamp);
        hashMap.put("category", "" + category);
        hashMap.put("timestamp", timestamp);
        hashMap.put("uid", "" + firebaseAuth.getUid());

        //add to firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child("" + timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //category add success
                        progressDialog.dismiss();
                        Toast.makeText(UploadActivity.this, "Category added successfully...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //category add failed
                progressDialog.dismiss();
                Toast.makeText(UploadActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String title = "", description = "", author = "";

    private void validateUploadData() {
        Log.d(TAG, "validateUploadData: Validating data...");

        //get data
        title = uploadPageBinding.pdfTitle.getText().toString().trim();
        description = uploadPageBinding.pdfDesc.getText().toString().trim();
        author = uploadPageBinding.pdfAuthor.getText().toString().trim();


        //validate
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Enter Title...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Enter Description...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(author)) {
            Toast.makeText(this, "Enter Author...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedCategoryTitle)) {
            Toast.makeText(this, "Pick Category...", Toast.LENGTH_SHORT).show();
        } else if (pdfUri == null) {
            Toast.makeText(this, "Select PDF...", Toast.LENGTH_SHORT).show();
        } else if (coverUri == null) {
            Toast.makeText(this, "Select Cover img...", Toast.LENGTH_SHORT).show();
        } else {
            uploadPdfToStorage();
        }
    }

    private void loadPdfCategories() {
        Log.d(TAG, "loadPdfCategories: Loading pdf categories...");
        categoryTitleArrayList = new ArrayList<>();
        categoryIdArrayList = new ArrayList<>();

        //db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();
                categoryIdArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    //get id and title of category
                    String categoryId = "" + ds.child("id").getValue();
                    String categoryTitle = "" + ds.child("category").getValue();

                    //add to arraylist
                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArrayList.add(categoryId);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //selected category id and title
    private String selectedCategoryId, selectedCategoryTitle;

    private void categoryPickDialog() {
        Log.d(TAG, "categoryPickDialog: showing category pick dialog");

        String[] categoriesArray = new String[categoryTitleArrayList.size()];

        for (int i = 0; i < categoryTitleArrayList.size(); i++)
            categoriesArray[i] = categoryTitleArrayList.get(i);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Category")
                .setItems(categoriesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        selectedCategoryTitle = categoryTitleArrayList.get(which);
                        selectedCategoryId = categoryIdArrayList.get(which);
                        uploadPageBinding.pdfCategory.setText(selectedCategoryTitle);

                        Log.d(TAG, "onClick: Selected category:" + selectedCategoryId + " " + selectedCategoryTitle);
                    }
                }).show();
    }

    private void uploadPdfToStorage() {
        Log.d(TAG, "uploadPdfToStorage: Uploading pdf to storage");

        //show progress
        progressDialog.setMessage("Uploading PDF");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        //pdf path
        String filePath = "Books/" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePath);
        storageReference.putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: PDF uploaded to storage...");
                        Log.d(TAG, "onSuccess: getting pdf url");

                        //Get pdf url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uploadedPdfUrl = "" + uriTask.getResult();

                        uploadCoverToStorage(uploadedPdfUrl);

                        //upload to db
                        //uploadPdfToDB(uploadedPdfUrl, timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: PDF upload failed due to " + e.getMessage());
                        Toast.makeText(UploadActivity.this, "PDF upload failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadCoverToStorage(String uploadedPdfUrl) {
        Log.d(TAG, "uploadCoverToStorage: Uploading cover to storage");

        //show progress
        progressDialog.setMessage("Uploading Cover");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        //pdf path
        String filePath = "Covers/" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePath);
        storageReference.putFile(coverUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: Cover img uploaded to storage...");
                        Log.d(TAG, "onSuccess: getting cover img url");

                        //Get pdf url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uploadedCoverUrl = "" + uriTask.getResult();

                        //upload to db
                        uploadPdfToDB(uploadedPdfUrl, uploadedCoverUrl, timestamp);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: PDF upload failed due to " + e.getMessage());
                        Toast.makeText(UploadActivity.this, "PDF upload failed due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPdfToDB(String uploadedPdfUrl, String uploadedCoverUrl, long timestamp) {

        Log.d(TAG, "uploadPdfToDB: Uploading pdf to DB");

        progressDialog.setMessage("Uploading PDF...");

        String uid = firebaseAuth.getUid();

        //setup data
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + uid);
        hashMap.put("id", "" + timestamp);
        hashMap.put("title", "" + title);
        hashMap.put("author", "" + author);
        hashMap.put("description", "" + description);
        hashMap.put("categoryId", "" + selectedCategoryId);
        hashMap.put("pdfUrl", "" + uploadedPdfUrl);
        hashMap.put("coverUrl", "" + uploadedCoverUrl);
        hashMap.put("timestamp", "" + timestamp);

        //db ref
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child("" + timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: Successfully uploaded...");
                        Toast.makeText(UploadActivity.this, "Successfully uploaded...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Failed to upload due to" + e.getMessage());
                        Toast.makeText(UploadActivity.this, "Failed to upload due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void PdfPick() {
        Log.d(TAG, "PdfPick: starting pdf pick intent");
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF_PICK_CODE);
    }

    private void CoverPick() {
        Log.d(TAG, "CoverPick: starting cove img pick intent");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select cover img"), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PDF_PICK_CODE) {
                Log.d(TAG, "onActivityResult : PDF Picked");

                pdfUri = data.getData();

                Log.d(TAG, "onActivityResult: URI: " + pdfUri);
            } else if (requestCode == 100) {
                Log.d(TAG, "onActivityResult : Cover img Picked");

                coverUri = data.getData();

                Log.d(TAG, "onActivityResult: URI: " + coverUri);
            }
        } else {
            Log.d(TAG, "onActivityResult: cancelled picking");
            Toast.makeText(this, "Cancelled picking", Toast.LENGTH_SHORT).show();
        }
    }
}