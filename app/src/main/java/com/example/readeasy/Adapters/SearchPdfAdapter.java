package com.example.readeasy.Adapters;

import static com.example.readeasy.Constants.MAX_BYTES_PDF;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readeasy.Filters.PdfFilter;
import com.example.readeasy.Models.ModelPdf;
import com.example.readeasy.R;
import com.example.readeasy.databinding.SearchResultsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchPdfAdapter extends RecyclerView.Adapter<SearchPdfAdapter.SearchPdfHolder> implements Filterable {

    private Context context;
    public ArrayList<ModelPdf> pdfArrayList, filterList;

    private SearchResultsBinding binding;

    private PdfFilter filter;

    private static final String TAG = "PDF_ADAPTER";

    public SearchPdfAdapter(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
    }

    @NonNull
    @Override
    public SearchPdfHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*binding = LibPdfViewBinding.inflate(LayoutInflater.from(context), parent, false);
        return new SearchPdfHolder(binding.getRoot());*/
        View view = LayoutInflater.from(context).inflate(R.layout.search_results, parent, false);
        return new SearchPdfHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchPdfHolder holder, int position) {
        //get data
        ModelPdf model = pdfArrayList.get(position);

        String title = model.getTitle();
        String author = model.getAuthor();

        holder.searchTitle.setText(title);
        holder.searchAuthor.setText(author);

        loadPdfFromUrl(model, holder);
    }

    private void loadPdfFromUrl(ModelPdf model, SearchPdfHolder holder) {

        //String pdfUrl = model.getUrl();
        String coverUrl = model.getCoverUrl();
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(coverUrl);
        ref.getBytes(MAX_BYTES_PDF)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Log.d(TAG, "onSuccess: " + model.getTitle() + "Successfully got the file");

                        Picasso.get().load(coverUrl).into(holder.imageView);

                        //set pdfview
                        /*holder.pdfview.fromBytes(bytes)
                                .pages(0)
                                .spacing(0)
                                .swipeHorizontal(false)
                                .onError(new OnErrorListener() {
                                    @Override
                                    public void onError(Throwable t) {
                                        holder.progressbar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onError: " + t.getMessage());
                                    }
                                })
                                .onPageError(new OnPageErrorListener() {

                                    @Override
                                    public void onPageError(int page, Throwable t) {
                                        holder.progressbar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "onPageError: " + t.getMessage());
                                    }
                                })
                                .onLoad(new OnLoadCompleteListener() {
                                    @Override
                                    public void loadComplete(int nbPages) {
                                        holder.progressbar.setVisibility(View.INVISIBLE);
                                        Log.d(TAG, "loadComplete: pdf loaded");
                                    }
                                })
                                .load();*/
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
        Log.d(TAG, "getItemCount: "+"Nigga***"+pdfArrayList.size());
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {

        if (filter == null) {
            filter = new PdfFilter(filterList, this);
        }
        return filter;
    }

    class SearchPdfHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ProgressBar progressbar;
        TextView searchTitle;
        TextView searchAuthor;

        public SearchPdfHolder(@NonNull View itemView) {
            super(itemView);

            /*pdfview = binding.pdfview;
            progressbar = binding.progressbar;
            searchTitle = binding.searchTitle;
            searchAuthor = binding.searchAuthor;*/
            imageView = itemView.findViewById(R.id.searchCover);
            //progressbar = itemView.findViewById(R.id.progressbar);
            searchTitle = itemView.findViewById(R.id.searchTitle);
            searchAuthor = itemView.findViewById(R.id.searchAuthor);
        }
    }
}
