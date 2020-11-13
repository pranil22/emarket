package com.example.yeskart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText nameEditText, emailEditText, passwordEditText;
    Button registerButton;
    public ProgressDialog loading;

    public void createAccount() {
        String email,name,password;

        email = emailEditText.getText().toString();
        name = nameEditText.getText().toString();
        password = passwordEditText.getText().toString();

        if(TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
        }
        else {
            loading.setTitle("Create User");
            loading.setMessage("Please wait...");
            loading.setCanceledOnTouchOutside(false);
            loading.show();

            validate(name, email, password);
        }
    }

    public void validate(final String name, final String email, final String password) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference rootRef = database.getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String, Object> userData = new HashMap<>();
                userData.put("email", email);
                userData.put("password", password);
                userData.put("name", name);

                rootRef.child("users").updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_SHORT);

                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT);
                        }
                    }
                });
//
//                if(!(snapshot.child("users").child(email).exists())) {
//
//                    HashMap<String, Object> userData = new HashMap<>();
//                    userData.put("email", email);
//                    userData.put("password", password);
//                    userData.put("name", name);
//
//                    rootRef.child("users").child(email).updateChildren(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if(task.isSuccessful()){
//                                Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_SHORT);
//
//                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                                startActivity(intent);
//                            }
//                            else {
//                                Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT);
//                            }
//                        }
//                    });
//
//
//                }
//                else {
//                    Toast.makeText(RegisterActivity.this, "Email already exists", Toast.LENGTH_SHORT);
//                    loading.dismiss();
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.register_full_name);
        emailEditText = findViewById(R.id.register_email);
        passwordEditText = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.register_button);
        loading = new ProgressDialog(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }
}
