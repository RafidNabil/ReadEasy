package com.example.readeasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginSignUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    EditText editName;
    EditText editEmail;
    EditText editPass;

    String username;
    String useremail;
    String userpass;
    String TAG = "LOG_IN_PAGE";

    String state;

    TextView textView;
    TextView msgTv;
    TextView nameTv;
    EditText nameET;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.login_signup);

        textView = findViewById(R.id.loginTV);
        msgTv = findViewById(R.id.msgTV);
        nameTv = findViewById(R.id.nameTv);
        nameET = findViewById(R.id.nameET);

        editName = findViewById(R.id.nameET);
        editEmail = findViewById(R.id.emailET);
        editPass = findViewById(R.id.passwordET);

        progressDialog = new ProgressDialog(this);


        firebaseAuth = FirebaseAuth.getInstance();

        Button continueBtn = findViewById(R.id.continueBtn);

        Intent intent = getIntent();

        // Check if the Intent has the extra with the specified key
        if (intent.hasExtra("PAGE_NAME")) {
            // Get the string data from the Intent
            String receivedData = intent.getStringExtra("PAGE_NAME");
            state = receivedData;

            // Now, you can use the receivedData as needed in your second activity
            // For example, you can display it in a TextView

            textView.setText(receivedData);

            if (receivedData.equals("Sign Up")) {
                msgTv.setText("Please Sign Up to continue");
            } else {
                msgTv.setText("It's great to see you again. Please login to continue");
                nameTv.setVisibility(textView.GONE);
                nameET.setVisibility(EditText.GONE);
            }
        }

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.equals("Sign Up")) {
                    validateData();
                } else if (state.equals("Log In")) {
                    logInUser();
                }
            }
        });
    }

    private void logInUser() {

        useremail = editEmail.getText().toString().trim();
        userpass = editPass.getText().toString().trim();

        if (TextUtils.isEmpty(useremail)) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userpass)) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
        } else {
            verifyUser();
        }
    }


    private void validateData() {

        username = editName.getText().toString().trim();
        useremail = editEmail.getText().toString().trim();
        userpass = editPass.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
        } else if (!validEmail(useremail)) {
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
        } else if (!validPassword(userpass)) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
        } else {
            createUser();
        }
    }

    private boolean validEmail(String email) {
        String e;
        e = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(e);

        if(email == null)
            return false;

        if(pat.matcher(email).matches() && (email.contains("gmail") || email.contains("outlook") || email.contains("yahoo")))
            return true;
        else
            return false;
    }

    private boolean validPassword(String password) {

        String pass;
        pass = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=.*])"
                + "(?=\\S+$).{8,20}$";

        Pattern p = Pattern.compile(pass);

        if(password == null)
            return false;

        Matcher m = p.matcher(password);

        return m.matches();
    }

    private void verifyUser() {
        progressDialog.setMessage("Verifying User...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(useremail, userpass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginSignUpActivity.this, "User verification successful.", Toast.LENGTH_SHORT).show();
                        
                        checkAdmin();
                        
                        startActivity(new Intent(LoginSignUpActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginSignUpActivity.this, "Wrong email or password" , Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAdmin() {

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User Info");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String userType = ""+snapshot.child("UserType").getValue();
                        String name = ""+snapshot.child("name").getValue();
                        String email = ""+snapshot.child("Email").getValue();
                        if(userType.equals("admin")) {

                            USER_INFORMATION o = new USER_INFORMATION();

                            o.setType("admin");
                            o.setName(name);
                            o.setMail(email);


                        }else if(userType.equals("")){
                            USER_INFORMATION o = new USER_INFORMATION();

                            o.setType("user");
                            o.setName(name);
                            o.setMail(email);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void createUser() {

        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(editEmail.getText().toString().trim(), editPass.getText().toString().trim())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        updateUserInfo();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginSignUpActivity.this, ""+e.getMessage().trim(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateUserInfo() {

        progressDialog.setMessage("Setting info...");

        long timestamp = System.currentTimeMillis();

        String uid = firebaseAuth.getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("name", username);
        hashMap.put("Email", useremail);
        hashMap.put("Password", userpass);
        hashMap.put("UserType", "");
        hashMap.put("Timestamp", timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User Info");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginSignUpActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginSignUpActivity.this, MainActivity.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginSignUpActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}