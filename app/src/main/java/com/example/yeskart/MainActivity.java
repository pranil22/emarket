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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button registerButton,loginButton;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerButton = findViewById(R.id.register_button);
        loginButton = findViewById(R.id.login_button);

        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Activity", "Register");
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Activity", "Login");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        String email = Paper.book().read("email");
        String password = Paper.book().read("password");
        boolean isBuyer = Paper.book().read("isBuyer");

        if(email != "" && password != "") {
            Log.i("email", String.valueOf(email));
            Log.i("password", String.valueOf(password));
            Log.i("isBuyer", String.valueOf(isBuyer));


            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                navigateUser(email, password, isBuyer);
            }
        }
    }

    public void navigateUser(String email, String password, final boolean isBuyer) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                                if(isBuyer) {
                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
                                    startActivity(intent);
                                }

                            }
                        }

                        loadingBar.dismiss();

                    }
                });
    }
}
