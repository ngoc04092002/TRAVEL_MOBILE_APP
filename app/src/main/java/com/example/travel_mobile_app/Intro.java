package com.example.travel_mobile_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Intro extends AppCompatActivity {

    Button let_go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra xem đã xem intro trước đó hay chưa
        SharedPreferences preferences = getSharedPreferences("intro", MODE_PRIVATE);
        boolean hasViewedIntro = preferences.getBoolean("has_viewed_intro", false);

        if (hasViewedIntro) {
            // Nếu đã xem intro trước đó, chuyển đến Login và kết thúc IntroActivity
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            return; // Kết thúc phương thức onCreate
        }

        // Nếu chưa xem intro, tiếp tục khởi tạo giao diện intro
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        let_go = findViewById(R.id.let_go);
        let_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sau khi nhấn nút "Let's Go", cập nhật SharedPreferences và chuyển đến màn hình đăng nhập
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("has_viewed_intro", true);
                editor.apply();

                Intent myintent = new Intent(Intro.this, Login.class);
                startActivity(myintent);
                finish();
            }
        });
    }
}
