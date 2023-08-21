package com.example.readeasy;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.readeasy.Adapters.SearchPdfAdapter;
import com.example.readeasy.Models.ModelCategory;
import com.example.readeasy.Models.ModelPdf;
import com.example.readeasy.databinding.SearchPageBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class SearchFragment extends Fragment {

    //binding
    private SearchPageBinding binding;

    //categories arraylist
    private ArrayList<ModelCategory> categoryArrayList;

    //pdf arraylist
    private ArrayList<ModelPdf> pdfArrayList;

    //adapter
    private SearchPdfAdapter searchPdfAdapter;

    private String categoryId , categoryTitle;

    private static final String TAG = "SEARCH_LIST_TAG";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.search_page, container, false);
        binding = SearchPageBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //categoryId = "1689016539929";
        //categoryTitle = "Popular Science";

        //loadPdfList();

        loadCategories();

        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(requireContext(), binding.searchBar.getQuery().toString().trim(), Toast.LENGTH_SHORT).show();
                searchQ(binding.searchBar.getQuery().toString().trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //searchPdfAdapter = new SearchPdfAdapter(requireContext(), pdfArrayList);
        //binding.SearchPageRecyclerView.setAdapter(searchPdfAdapter);

       /* binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //search as and usertype each letter
                try{
                    searchPdfAdapter.getFilter().filter(s);
                }
                catch(Exception e){
                    Log.d(TAG, "onTextChanged: "+e.getMessage());
                }

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        return view;
    }

    private void loadPdfList(String ctgr) {

        pdfArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo(ctgr)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelPdf model = ds.getValue(ModelPdf.class);
                            pdfArrayList.add(model);

                            Log.d(TAG, "onDataChange: "+model.getId()+" "+model.getTitle());
                        }
                        searchPdfAdapter = new SearchPdfAdapter(requireContext(), pdfArrayList);
                        binding.SearchPageRecyclerView.setAdapter(searchPdfAdapter);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void searchQ(String bookTitle) {

        pdfArrayList = new ArrayList<>();
        pdfArrayList.clear();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        pdfArrayList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelPdf model = ds.getValue(ModelPdf.class);
                            if(model.getTitle().toLowerCase().equals(bookTitle.toLowerCase())){
                                pdfArrayList.add(model);
                                Log.d(TAG, "onDataChange: "+model.getId()+" "+model.getTitle());
                            }

                        }
                        searchPdfAdapter = new SearchPdfAdapter(requireContext(), pdfArrayList);
                        binding.SearchPageRecyclerView.setAdapter(searchPdfAdapter);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadCategories() {

        categoryArrayList = new ArrayList<>();

        //get categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get data
                    ModelCategory model = ds.getValue(ModelCategory.class);

                    //add to array
                    categoryArrayList.add(model);

                    float Height = 110;

                    ChipGroup chipGroup = requireView().findViewById(R.id.chipGrp);

                    Chip chip = new Chip(requireContext());
                    chip.setId(View.generateViewId());
                    chip.setChipMinHeight(Height);
                    chip.setText(model.getCategory());
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral2));
                    chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                    chip.setChipBackgroundColorResource(R.color.white);
                    chip.setChipCornerRadius(getResources().getDimension(R.dimen.chip_corner_radius));
                    chip.setRippleColorResource(R.color.primary5);
                    chip.setElevation(getResources().getDimension(R.dimen.ELEVATION));
                    chip.setCheckable(true);
                    chip.setCheckedIcon(null);
                    chip.setAccessibilityClassName(model.getId());



                    // Add the Chip to the ChipGroup
                    chipGroup.addView(chip);

                    chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                chip.setChipStrokeWidth(getResources().getDimension(R.dimen.chip_stroke_width));
                                chip.setChipStrokeColor(ColorStateList.valueOf(getResources().getColor((R.color.primary1))));
                                loadPdfList(chip.getAccessibilityClassName().toString());
                            } else {
                                chip.setChipStrokeWidth(0);
                                pdfArrayList.clear();
                                searchPdfAdapter.notifyDataSetChanged();
                                //chip.setChipStrokeColor(ColorStateList.valueOf(getResources().getColor((R.color.primary1))));
                            }
                        }
                    });



                        chip.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(getContext(), "Checked", Toast.LENGTH_SHORT).show();

                            }
                        });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}