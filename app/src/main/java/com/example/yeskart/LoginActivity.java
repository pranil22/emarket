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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FirebaseFirestore db;
    private ProgressDialog loadingBar;
    private TextView sellerTextView, buyerTextView;
    private boolean isBuyer;




    public void login() {
        db = FirebaseFirestore.getInstance();

        final String email, password;
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();


        if(TextUtils.isEmpty(email)) {
            Toast.makeText(LoginActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();

        }
        else {
            loadingBar.setTitle("Logging you");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            db.collection("users")
                    .whereEqualTo("email", email)
                    .whereEqualTo("password", password)
                    .whereEqualTo("isBuyer", isBuyer)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            boolean isCorrect = false;
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                if(document.exists()) {
                                    Log.i("Msg", "Success");
                                    isCorrect = true;
                                    Paper.book().write("email", email);
                                    Paper.book().write("password", password);
                                    Paper.book().write("isBuyer", isBuyer);
                                    if(isBuyer) {
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        Intent intent = new Intent(LoginActivity.this, AddProductActivity.class);
                                        startActivity(intent);
                                    }

                                }
                            }

                            if(!isCorrect) {
                                Toast.makeText(LoginActivity.this, "Invalid Credientials", Toast.LENGTH_SHORT).show();
                            }

                            loadingBar.dismiss();

                        }
                    });
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        buyerTextView = findViewById(R.id.buyer);
        sellerTextView = findViewById(R.id.seller);
        loadingBar = new ProgressDialog(this);

        isBuyer = true;
        Paper.init(this);

        sellerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sellerTextView.setVisibility(View.INVISIBLE);
                loginButton.setText("Login as Seller");
                isBuyer = false;
                buyerTextView.setVisibility(View.VISIBLE);
            }
        });

        buyerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyerTextView.setVisibility(View.INVISIBLE);
                loginButton.setText("Login as Buyer");
                isBuyer = true;
                sellerTextView.setVisibility(View.VISIBLE);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

}
