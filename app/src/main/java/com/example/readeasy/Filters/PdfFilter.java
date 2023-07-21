package com.example.readeasy.Filters;

import android.widget.Filter;

import com.example.readeasy.Adapters.SearchPdfAdapter;
import com.example.readeasy.Models.ModelPdf;

import java.util.ArrayList;

public class PdfFilter extends Filter {

    ArrayList<ModelPdf>  filterList;

    SearchPdfAdapter searchPdfAdapter;

    public PdfFilter(ArrayList<ModelPdf> filterList, SearchPdfAdapter searchPdfAdapter) {
        this.filterList = filterList;
        this.searchPdfAdapter = searchPdfAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint){
        FilterResults results = new FilterResults();
        //value should not be null or empty
        if(constraint != null && constraint.length()>0){

            //change to uppercase or lowercase
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelPdf> filterModels = new ArrayList<>();

            for(int i=0; i<filterList.size(); i++){
                //validate
                if(filterList.get(i).getTitle().toUpperCase().contains(constraint)){
                    //add to filtered list
                    filterModels.add(filterList.get(i));
                }
            }

            results.count = filterModels.size();
            results.values = filterModels;
        }
        else{
            results.count = filterList.size();
            results.values = filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results){
        //apply filter changes
        //searchPdfAdapter.pdfArrayList = (ArrayList<ModelPdf>)results.values;
        searchPdfAdapter.pdfArrayList.clear();
        searchPdfAdapter.pdfArrayList.addAll((ArrayList<ModelPdf>) results.values);

        //notify changes
        searchPdfAdapter.notifyDataSetChanged();
    }


}
