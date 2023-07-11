package com.example.readeasy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.readeasy.databinding.LibraryPageBinding;

import java.util.zip.Inflater;


public class LibraryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.library_page, container, false);

        SwitchCompat switchCompat = view.findViewById(R.id.customSwitch);
        ScrollView scrollView = view.findViewById(R.id.libScrollView);

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
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);

                } else {
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            }
        });

        return view;

    }

}