package com.example.readeasy;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.profile, container, false);

        Button button = view.findViewById(R.id.upBtn);
        TextView name = view.findViewById(R.id.userName);
        TextView mail = view.findViewById(R.id.userEmail);

        USER_INFORMATION o = new USER_INFORMATION();


        mail.setText(o.getMail().toString().trim());

        if(o.getType().equals("user")){
            name.setText(o.getName().toString().trim());
            button.setVisibility(View.GONE);
        }
        else{
            name.setText(o.getName().toString().trim()+" (Admin) ");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), UploadActivity.class));
                }
            });
        }

        return view;
    }
}