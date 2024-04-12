package com.example.travel_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText login_email, login_password;
    Button login_button;
    TextView signupRedirectText;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.progressBar);
        login_button = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        fAuth = FirebaseAuth.getInstance();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = login_email.getText().toString().trim();
                String password = login_password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    login_email.setError("Email không được để trống");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    login_password.setError("Password không được để trống");
                    return;
                }

                if (password.length() < 6) {
                    login_password.setError("Password phải có độ dài lớn hơn 6 ký tự");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate the user
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Đăng nhập thành công!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Toast.makeText(Login.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });
    }
}