package com.example.travel_mobile_app;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.services.SharedPreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText register_fullname, register_email, register_password, register_phone;
    Button register_button;
    TextView loginRedirectText;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        register_fullname = findViewById(R.id.register_fullname);
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        register_phone = findViewById(R.id.register_phone);
        register_button = findViewById(R.id.register_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if (fAuth.getCurrentUser() != null) {
            setUserInfoToLocal(fAuth.getCurrentUser().getUid());
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("previous_fragment", "home_screen");
            intent.putExtra("userId", fAuth.getCurrentUser().getUid());
            startActivity(intent);
            finish();
        }

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = register_email.getText().toString().trim();
                String password = register_password.getText().toString().trim();
                String fullname = register_fullname.getText().toString();
                String phone = register_phone.getText().toString();
                String avatarURL = null, address = null;
                String username = null;
                Boolean enableNotification = true, enableUpdate = true;
                ArrayList<String> followers = new ArrayList<>();
                ArrayList<String> following = new ArrayList<>();

                // Kiểm tra trường Full name và Phone không được để trống
                if (TextUtils.isEmpty(fullname)) {
                    register_fullname.setError("Full name không được để trống");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    register_phone.setError("Phone không được để trống");
                    return;
                }

                // Điều kiện cho trường email
                if (!email.matches("[a-zA-Z0-9._%+-]+@gmail\\.com")) {
                    register_email.setError("Email không hợp lệ, phải có định dạng @gmail.com");
                    return;
                }

                // Điều kiện cho trường mật khẩu
                if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$")) {
                    register_password.setError("Mật khẩu phải từ 8 đến 15 ký tự, bao gồm số, chữ thường, chữ in hoa và ký tự đặc biệt, không mang ý nghĩa đi kèm");
                    return;
                }


                if (TextUtils.isEmpty(email)) {
                    register_email.setError("Email không được để trống");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    register_password.setError("Password không được để trống");
                    return;
                }

                if (password.length() < 6) {
                    register_password.setError("Password phải có độ dài lớn hơn 6 ký tự");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Đăng ký tài khoản vào firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Tạo tài khoản thành công", Toast.LENGTH_LONG).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fullName", fullname);
                            user.put("username", username);
                            user.put("email", email);
                            user.put("password", password);
                            user.put("phone", phone);
                            user.put("id", userID);
                            user.put("address", address);
                            user.put("avatarURL", avatarURL);
                            user.put("enableNotification", enableNotification);
                            user.put("enableUpdate", enableUpdate);
                            user.put("followers", followers);
                            user.put("following", following);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: Tạo người dùng từ " + userID);
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), Login.class));

                        } else {
                            Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    private void setUserInfoToLocal(String uId) {
        SharedPreferencesManager.init(getApplicationContext());
        CollectionReference users = fStore.collection("users");
        users.document(uId)
             .get()
             .addOnCompleteListener(taskUser -> {
                 if (taskUser.isSuccessful() && taskUser.getResult() != null) {
                     UserModel userModel = taskUser.getResult().toObject(UserModel.class);
                     SharedPreferencesManager.writeUserInfo(userModel);
                 }
             });
    }
}