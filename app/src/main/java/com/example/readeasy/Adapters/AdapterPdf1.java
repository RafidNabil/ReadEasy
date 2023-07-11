package com.example.readeasy.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readeasy.Models.ModelPdf;
import com.example.readeasy.databinding.PdfViewBinding;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdf1 extends RecyclerView.Adapter<AdapterPdf1.PdfHolder1>{

    private Context context;

    private ArrayList<ModelPdf> pdfArrayList;

    private PdfViewBinding pdfViewBinding;

    public AdapterPdf1(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
    }

    @NonNull
    @Override
    public PdfHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        pdfViewBinding = PdfViewBinding.inflate(LayoutInflater.from(context), parent, false);
        return new PdfHolder1(pdfViewBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull PdfHolder1 holder, int position) {

    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    class PdfHolder1 extends RecyclerView.ViewHolder{

        PDFView pdfView;
        ProgressBar progressBar;
        TextView searchTitle, searchAuthor, searchDescription;


        public PdfHolder1(@NonNull View itemView) {
            super(itemView);

            pdfView = pdfViewBinding.pdfview;
            progressBar = pdfViewBinding.progressbar;
            searchTitle = pdfViewBinding.searchTitle;
            searchAuthor = pdfViewBinding.searchAuthor;
            searchDescription = pdfViewBinding.searchDescription;
        }
    }
}
