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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText nameEditText, emailEditText, passwordEditText, moblieNoEditText;
    Button registerButton;
    private ProgressDialog loading;
    private FirebaseFirestore db;

    public void createAccount() {
        String email,name,password, mobileNo;

        email = emailEditText.getText().toString();
        name = nameEditText.getText().toString();
        password = passwordEditText.getText().toString();
        mobileNo = moblieNoEditText.getText().toString();


        if(TextUtils.isEmpty(email)) {
            Toast.makeText(RegisterActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(name)) {
            Toast.makeText(RegisterActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(mobileNo)) {
            Toast.makeText(RegisterActivity.this, "Please enter mobileNo", Toast.LENGTH_SHORT).show();
        }
        else {
            loading.setTitle("Creating User");
            loading.setMessage("Please wait...");
            loading.setCanceledOnTouchOutside(false);
            loading.show();

            validate(name, email, password, mobileNo);
        }
    }

    public void validate(final String name, final String email, final String password, final String mobileNo) {
        db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            boolean isPresent = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.exists()) {
                                    isPresent = true;
                                }
                                Log.d("Message", document.getId() + " => " + document.getData());
                            }

                            if (!isPresent) {
                                addUser(name, mobileNo, email, password);
                            }
                            else {
                                Toast.makeText(RegisterActivity.this, "User already present with email", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Somethong went wrong", Toast.LENGTH_SHORT).show();
                        }
                        loading.dismiss();
                    }
                });
//
//        Map<String, Object> user = new HashMap<>();
//        user.put("first", "Ada");
//        user.put("last", "Lovelace");
//        user.put("born", 1815);
//
//        db.collection("users")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d("tag", "DocumentSnapshot added with ID: " + documentReference.getId());
//                        loading.dismiss();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("tag", "Error adding document", e);
//                        loading.dismiss();
//                    }
//                });


    }

    public void addUser(String name, String mobileNo, String email, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("mobileNo", mobileNo);
        user.put("email", email);
        user.put("password", password);


        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(RegisterActivity.this, "Succesfully added user", Toast.LENGTH_SHORT).show();
                        Log.d("Msg", "DocumentSnapshot added with ID: " + documentReference.getId());

                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("tag", "Error adding document", e);
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
        moblieNoEditText = findViewById(R.id.register_mobile_no);
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
