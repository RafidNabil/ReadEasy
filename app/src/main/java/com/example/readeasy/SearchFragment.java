package com.example.readeasy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readeasy.Models.ModelCategory;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    //categories arraylist
    private ArrayList<ModelCategory> categoryArrayList;

    //ChipGroup chipGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.search_page, container, false);

        loadCategories();

        //chipGroup = getView().findViewById(R.id.chipGrp);


        return view;
    }

    private void loadCategories() {

        categoryArrayList = new ArrayList<>();

        //get categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();

                for (DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    ModelCategory model = ds.getValue(ModelCategory.class);

                    //add to array
                    categoryArrayList.add(model);

                    float Height = 110;

                    for(int i=0; i<categoryArrayList.size(); i++)
                    {
                        ChipGroup chipGroup = requireView().findViewById(R.id.chipGrp);

                        Chip chip = new Chip(requireContext());
                        chip.setId(View.generateViewId());
                        chip.setChipMinHeight(Height);
                        chip.setText(categoryArrayList.get(i).getCategory());
                        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral2));
                        chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                        chip.setChipBackgroundColorResource(R.color.white);
                        chip.setChipCornerRadius(getResources().getDimension(R.dimen.chip_corner_radius));
                        chip.setRippleColorResource(R.color.primary5);

                        // Add the Chip to the ChipGroup
                        chipGroup.addView(chip);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}