package com.example.readeasy.Adapters;

import static com.example.readeasy.Constants.MAX_BYTES_PDF;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.readeasy.BookDetailsActivity;
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

    class TopHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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

            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                ModelPdf clickedItem = list.get(position);

                // Perform the action you want when an item is clicked
                // For example, you can open a new activity and pass data

                Intent intent = new Intent(context, BookDetailsActivity.class);
                // Pass data using extras
                intent.putExtra("pdfTitle", clickedItem.getTitle());
                intent.putExtra("pdfAuthor", clickedItem.getAuthor());
                // Add more data as needed

                context.startActivity(intent);
            }
        }
    }
}
