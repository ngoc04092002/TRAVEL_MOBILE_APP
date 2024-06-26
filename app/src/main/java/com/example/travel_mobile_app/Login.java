package com.example.travel_mobile_app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travel_mobile_app.MainActivity;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.Register;
import com.example.travel_mobile_app.models.UserModel;
import com.example.travel_mobile_app.services.SharedPreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Timer;
import java.util.TimerTask;

public class Login extends AppCompatActivity {

    EditText login_email, login_password;
    Button login_button;
    private FirebaseFirestore db;

    TextView signupRedirectText, forgotPassword;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    private long lastLoginTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.progressBar);
        login_button = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        forgotPassword = findViewById(R.id.forgotPassword);

        fAuth = FirebaseAuth.getInstance();

        // Lấy thời gian cuối cùng người dùng đăng nhập thành công từ SharedPreferences
        lastLoginTime = SharedPreferencesManager.getLastLoginTime(getApplicationContext());

        // Kiểm tra nếu đã đăng nhập trước đó và thời gian đã trôi qua từ lần đăng nhập cuối cùng
        // vượt quá 30 phút, hiển thị hộp thoại yêu cầu đăng nhập lại
        if (fAuth.getCurrentUser() != null && System.currentTimeMillis() - lastLoginTime > 90 * 60 * 1000) {
            showLoginRequiredDialog();
        } else if (fAuth.getCurrentUser() != null) {
            // Người dùng đã đăng nhập trước đó, chuyển sang MainActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("previous_fragment", "home_screen");
            intent.putExtra("userId", fAuth.getCurrentUser().getUid());
            startActivity(intent);
            finish(); // Kết thúc activity hiện tại
        }

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
                            setUserInfoToLocal(task.getResult().getUser().getUid());
                            lastLoginTime = System.currentTimeMillis();
                            SharedPreferencesManager.setLastLoginTime(getApplicationContext(), lastLoginTime);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("previous_fragment", "home_screen");
                            intent.putExtra("userId", task.getResult().getUser().getUid());
                            startActivity(intent);
                            finish(); // Kết thúc activity hiện tại
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

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Đặt lại mật khẩu?");
                passwordResetDialog.setMessage("Nhập vào email của bạn để lấy lại mật khẩu.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString();
                        if (mail.isEmpty()) {
                            Toast.makeText(Login.this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                        } else {
                            //gửi link về email
                            fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Login.this, "Link đã gửi về email của bạn.", Toast.LENGTH_SHORT).show();
                                    // Không làm gì để dialog vẫn hiển thị
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login.this, "Error! Link chưa được gửi đi. Vui lòng kiểm tra lại Email! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss(); // Ẩn dialog khi gửi thất bại
                                }
                            });
                        }
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close thẻ dialog
                        dialog.dismiss(); // Ẩn dialog khi người dùng chọn "No"
                    }
                });

                AlertDialog alertDialog = passwordResetDialog.create();
                alertDialog.show();
            }
        });
    }

    private void showLoginRequiredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yêu cầu đăng nhập lại");
        builder.setMessage("Bạn đã không hoạt động trong một khoảng thời gian dài, vui lòng đăng nhập lại.");
        builder.setCancelable(true);
        builder.setPositiveButton("Đăng nhập", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Login.this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();

//                Intent intent = new Intent(Login.this, Login.class);

//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

//                startActivity(intent);
//                finish();

                // Chuyển hướng đến màn hình đăng nhập
//                Intent intent = new Intent(Login.this, Login.class);
//                startActivity(intent);
//                finish();
                // Không kết thúc activity hiện tại ngay lập tức
            }
        });
        builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Đóng ứng dụng
                finishAffinity();
            }
        });
        builder.show();
    }

    private void setUserInfoToLocal(String uId) {
        SharedPreferencesManager.init(getApplicationContext());
        CollectionReference users = db.collection("users");
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
