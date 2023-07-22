package com.example.readeasy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private ArrayList<ModelPdf> list;
    private static final String TAG = "HOME_PAGE_TAG";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.home_page, container, false);
        binding = HomePageBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        load();

        return view;
    }

    private void load() {

        list = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelPdf model = ds.getValue(ModelPdf.class);
                            list.add(model);

                            Log.d(TAG, "onDataChange: "+model.getCoverUrl());

                            Log.d(TAG, "onDataChange: "+model.getId()+" "+model.getTitle());
                        }

                        topAdapter = new TopAdapter(requireContext(), list);
                        binding.hrcv1.setAdapter(topAdapter);
                        binding.hrcv2.setAdapter(topAdapter);
                        binding.hrcv3.setAdapter(topAdapter);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}