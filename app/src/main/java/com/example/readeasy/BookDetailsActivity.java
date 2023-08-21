package com.example.readeasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import android.view.View;
import android.widget.Button;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.readeasy.Models.ModelPdf;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;



public class BookDetailsActivity extends AppCompatActivity {

    String TAG = "DETAILS_TAG";

    private ModelPdf book;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    String pdfTitle;
    String pdfAuthor;

    private DownloadManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.book_details);
        //https://firebasestorage.googleapis.com/v0/b/readeasy-e7030.appspot.com/o/Books%2F1690032187636?alt=media&token=bd547af0-1767-49d3-9bae-195247d1ae0f


        TextView bookTitle = findViewById(R.id.detailsTitle);
        Button readBtn = findViewById(R.id.readBtn);
        ImageView back = findViewById(R.id.detailsBackBtn);
        ImageView bookmark = findViewById(R.id.bookmarkImg);
        ImageView downloadBtn = findViewById(R.id.downloadImg);

        Intent intent = getIntent();
        pdfTitle = intent.getStringExtra("pdfTitle");
        pdfAuthor = intent.getStringExtra("pdfAuthor");

        bookTitle.setText(pdfTitle);

        load (pdfTitle);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        readBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findInfo(49);
                Intent intent2 = new Intent(BookDetailsActivity.this, ReadActivity.class);
                intent2.putExtra("Book", pdfTitle);
                startActivity(intent2);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(BookDetailsActivity.this, "Bookmark icon clicked", Toast.LENGTH_SHORT).show();

                isBookMarked();

            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile();
            }
            
        });

    }

    private void downloadFile() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");

        ref.orderByChild("title").equalTo(pdfTitle).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelPdf model = ds.getValue(ModelPdf.class);
                    book = model;

                    startDownload(model.getUrl());

                    Log.d(TAG, "Download link : "+model.getCoverUrl());
                    break;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void startDownload(String pdfUrl) {
        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(pdfUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        long reference = manager.enqueue(request);
    }

    private void load(String pdfTitle) {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");

        ref.orderByChild("title").equalTo(pdfTitle).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelPdf model = ds.getValue(ModelPdf.class);
                    book = model;

                    TextView pages = findViewById(R.id.pagenumTV);

                    //TextView description = findViewById(R.id.detailDesc);
                    //description.setText(model.getDescription());

                    //TextView rating = findViewById(R.id.rating);
                    //double randomDouble = ThreadLocalRandom.current().nextDouble((5.0-3.0+1.0)+3.0);
                    //rating.setText(String.valueOf(randomDouble).substring(0, 3));

                    ImageView img = findViewById(R.id.detailsCover);
                    Picasso.get().load(model.getCoverUrl()).into(img);

                    //Toast.makeText(BookDetailsActivity.this, "Load method is called", Toast.LENGTH_LONG).show();
                    apiCall(pdfTitle);

                    Log.d(TAG, "onDataChange: "+model.getCoverUrl());
                    Log.d(TAG, "onDataChange: "+model.getId()+" "+model.getTitle());
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void apiCall(String title) {

        String api = "https://www.googleapis.com/books/v1/volumes?q={"+title+"}";

// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Log.d(TAG, "onErrorResponse: "+response.toString());
                        try {

                            JSONObject obj = new JSONObject(response);
                            JSONArray arr = obj.getJSONArray("items");
                            obj = arr.getJSONObject(0).getJSONObject("volumeInfo");

                            String description = obj.get("description").toString();
                            String author = obj.getJSONArray("authors").getString(0);
                            String pageCount = obj.getString("pageCount");

                            for(int i=0; i<5; i++)
                            {
                                JSONObject obj2 = arr.getJSONObject(i).getJSONObject("volumeInfo");
                                pageCount = obj2.getString("pageCount");
                                if(!pageCount.equals("0"))
                                {
                                    break;
                                }
                            }

                            if(obj.has("averageRating"))
                            {
                                String rating = obj.getString("averageRating");
                                TextView rtv = findViewById(R.id.rating);
                                rtv.setText(rating);
                            }


                            TextView dtv = findViewById(R.id.detailDesc);
                            dtv.setText(description);



                            TextView ptv = findViewById(R.id.pagenumTV);
                            ptv.setText(pageCount);

                            TextView atv = findViewById(R.id.detailsAuthor);
                            atv.setText(author);

                            Log.d(TAG, "onResponse: "+api);
                            Log.d(TAG, "onResponse: "+description);
                            Log.d(TAG, "onResponse: "+author);
                            Log.d(TAG, "onResponse: "+pageCount);
                           // Log.d(TAG, "onResponse: "+rating);
                            //Toast.makeText(BookDetailsActivity.this, "Api method is called", Toast.LENGTH_LONG).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: "+e.getMessage());
                            //Toast.makeText(BookDetailsActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error.getLocalizedMessage());
                //Toast.makeText(BookDetailsActivity.this, error.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void findInfo(int callCode){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");

        ref.orderByChild("title").equalTo(pdfTitle).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelPdf model = ds.getValue(ModelPdf.class);
                    book = model;

                    if(callCode == 49)
                    {
                        uploadPdfToDB(model, "StartedReading/" + currentUser.getUid());
                    }
                    else if(callCode  == 59)
                    {
                        uploadPdfToDB(model, "UserBookmarks/" + currentUser.getUid());
                    }

                    Log.d(TAG, "onDataChange: "+model.getCoverUrl());
                    Log.d(TAG, "onDataChange: "+model.getId()+" "+model.getTitle());
                    break;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void isBookMarked() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserBookmarks/"+currentUser.getUid());

        ref.orderByChild("title").equalTo(pdfTitle).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    Toast.makeText(BookDetailsActivity.this, "Book exists in bookmark", Toast.LENGTH_SHORT).show();
                    //removeBookmark();
                } else {
                    Toast.makeText(BookDetailsActivity.this, "Book dosen't exist in bookmark", Toast.LENGTH_SHORT).show();
                    findInfo(59);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    /*private void removeBookmark() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserBookmarks/"+currentUser.getUid());

        ref.orderByChild("title").equalTo(pdfTitle).limitToFirst(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){

                    ds.getRef().removeValue();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    private void uploadPdfToDB(ModelPdf model, String path) {

        Log.d(TAG, "uploadPdfToDB: Uploading pdf to DB");

        //progressDialog.setMessage("Uploading PDF...");

        String uid = firebaseAuth.getUid();
        String userUID = currentUser.getUid();

        String timestamp;
        timestamp = String.valueOf(System.currentTimeMillis());

        //setup data
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", "" + uid);
        hashMap.put("id", "" + timestamp);
        hashMap.put("title", "" + model.getTitle());
        hashMap.put("author", "" + model.getAuthor());
        hashMap.put("description", "" + model.getDescription());
        hashMap.put("categoryId", "" + model.getCategoryId());
        hashMap.put("pdfUrl", "" + model.getUrl());
        hashMap.put("coverUrl", "" + model.getCoverUrl());
        hashMap.put("timestamp", "" + timestamp);

        //db ref
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
        ref.orderByChild("title").equalTo(pdfTitle).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    ref.child("" + timestamp)
                            .setValue(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //progressDialog.dismiss();
                                    Log.d(TAG, "onSuccess: Successfully uploaded...");
                                    Toast.makeText(BookDetailsActivity.this, "Successfully uploaded...", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Failed to upload due to" + e.getMessage());
                                    Toast.makeText(BookDetailsActivity.this, "Failed to upload due to" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(BookDetailsActivity.this, "Nah", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}