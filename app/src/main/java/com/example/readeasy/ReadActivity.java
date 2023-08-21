package com.example.readeasy;

import static com.example.readeasy.Constants.MAX_BYTES_PDF;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.readeasy.Adapters.SearchPdfAdapter;
import com.example.readeasy.Models.ModelPdf;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.link.DefaultLinkHandler;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ThreadLocalRandom;

public class ReadActivity extends AppCompatActivity {

    private static String TAG = "READ_TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.read_page);

        Intent intent = getIntent();

        //PDFView pdfView = findViewById(R.id.readPdfView);

        if(intent != null)
        {
            String book = intent.getStringExtra("Book");
            Log.d(TAG, "onCreate: Function called for "+book);
            load(book);
        }
        else
        {
            Log.d(TAG, "onCreate: "+"Book is null");
        }

    }

    private void load(String pdfTitle) {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");

        ref.orderByChild("title").equalTo(pdfTitle).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelPdf model = ds.getValue(ModelPdf.class);

                    loadPdfFromUrl(ds.child("pdfUrl").getValue(String.class));

                    Log.d(TAG, "onDataChange: "+ds.child("pdfUrl").getValue(String.class));
                    Log.d(TAG, "onDataChange: "+model.getId()+" "+model.getTitle());
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPdfFromUrl(String pdfUrl) {
        ProgressBar progressbar = findViewById(R.id.readPB);
        PDFView pdfView = findViewById(R.id.readPdfView);

        //String pdfUrl = model.getUrl();
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        //Log.d(TAG, "onSuccess: " + model.getTitle() + "Successfully got the file");

                        //set pdfview
                        pdfView.fromBytes(bytes)
                                .spacing(10)
                                .enableSwipe(true)
                                .enableAnnotationRendering(true)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        progressbar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onError: " + t.getMessage());
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {

                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        progressbar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onPageError: " + t.getMessage());
                                    }
                                })
                                .onLoad(new OnLoadCompleteListener() {
                                    @Override
                                    public void loadComplete(int nbPages) {
                                        progressbar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "loadComplete: pdf loaded");
                                    }
                                })
                                .load();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: failed getting file from url due to " + e.getMessage());
                    }
                });
    }
}