package com.example.readeasy.Adapters;

import static com.example.readeasy.Constants.MAX_BYTES_PDF;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.readeasy.Models.ModelPdf;
import com.example.readeasy.R;
import com.example.readeasy.databinding.PdfTilesBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TopAdapter extends RecyclerView.Adapter<TopAdapter.TopHolder> {

    private Context context;
    public ArrayList<ModelPdf> list;

    private PdfTilesBinding binding;

    private static final String TAG = "TOP_ADAPTER";

    public TopAdapter(Context context, ArrayList<ModelPdf> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TopAdapter.TopHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_tiles, parent, false);
        return new TopAdapter.TopHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopHolder holder, int position) {
        ModelPdf model = list.get(position);

        String title = model.getTitle();
        String author = model.getAuthor();

        holder.title.setText(title);
        holder.author.setText(author);

        setImg(model, holder);
    }

    private void setImg(ModelPdf model, TopAdapter.TopHolder holder) {

        String coverUrl = model.getCoverUrl();

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(coverUrl);
        ref.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG, "onSuccess: " + model.getTitle() + "Successfully got the file");

                        //set pdfview
                        Picasso.get().load(coverUrl).into(holder.imageView);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: failed getting file from url due to " + e.getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class TopHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView title;
        TextView author;

        public TopHolder(@NonNull View itemView) {
            super(itemView);

           /* imageView = binding.bookCover;
            title = binding.bookTitle;
            author = binding.bookAuthor;*/
            imageView = itemView.findViewById(R.id.bookCover);
            title = itemView.findViewById(R.id.bookTitle);
            author = itemView.findViewById(R.id.bookAuthor);

        }
    }
}
