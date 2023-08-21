package com.example.readeasy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.readeasy.Adapters.MidAdapter;
import com.example.readeasy.Adapters.SearchPdfAdapter;
import com.example.readeasy.Adapters.TopAdapter;
import com.example.readeasy.Models.ModelPdf;
import com.example.readeasy.databinding.HomePageBinding;
import com.example.readeasy.databinding.SearchPageBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomePageBinding binding;
    private TopAdapter topAdapter;
    private MidAdapter midAdapter;
    private ArrayList<ModelPdf> list;
    private ArrayList<ModelPdf> list2;
    private static final String TAG = "HOME_PAGE_TAG";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.home_page, container, false);
        binding = HomePageBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        load();
        load2();

        return view;
    }

    private void load() {

        list = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo("1689016539929").limitToFirst(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelPdf model = ds.getValue(ModelPdf.class);
                    list.add(model);

                    Log.d(TAG, "onDataChange: " + model.getCoverUrl());

                    Log.d(TAG, "onDataChange: " + model.getId() + " " + model.getTitle());
                }

                topAdapter = new TopAdapter(getContext(), list);
                binding.hrcv1.setAdapter(topAdapter);
                //binding.hrcv2.setAdapter(topAdapter);
                binding.hrcv3.setAdapter(topAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void load2() {

        list2 = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.orderByChild("categoryId").equalTo("1688955592498").limitToFirst(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list2.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelPdf model = ds.getValue(ModelPdf.class);
                    list2.add(model);

                    Log.d(TAG, "onDataChange: " + model.getCoverUrl());

                    Log.d(TAG, "onDataChange: " + model.getId() + " " + model.getTitle());
                }

                midAdapter = new MidAdapter(getContext(), list2);
                //binding.hrcv1.setAdapter(midAdapter);
                binding.hrcv2.setAdapter(midAdapter);
                binding.hrcv3.setAdapter(midAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}