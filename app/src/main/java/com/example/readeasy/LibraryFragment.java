package com.example.readeasy;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.readeasy.Adapters.TopAdapter;
import com.example.readeasy.Models.ModelPdf;
import com.example.readeasy.databinding.LibraryPageBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.zip.Inflater;


public class LibraryFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private LibraryPageBinding binding;
    private static final String TAG = "LIBRARY_PAGE_TAG";

    private ArrayList<ModelPdf> list;
    private ArrayList<ModelPdf> list2;
    private TopAdapter topAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = LibraryPageBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        SwitchCompat switchCompat = view.findViewById(R.id.customSwitch);
        ScrollView scrollView = view.findViewById(R.id.libScrollView);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    load2();

                } else {
                    //scrollView.fullScroll(ScrollView.FOCUS_UP);
                    load();
                }
            }
        });

        load();

        return view;

    }

    private void load() {

        list = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserBookmarks/"+currentUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
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
                binding.topRC.setAdapter(topAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void load2() {

        list2 = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("StartedReading/"+currentUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
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
                binding.topRC.setAdapter(topAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}