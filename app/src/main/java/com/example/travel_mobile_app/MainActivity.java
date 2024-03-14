package com.example.travel_mobile_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.travel_mobile_app.databinding.ActivityMainBinding;
import com.example.travel_mobile_app.fragments.AccountFragment;
import com.example.travel_mobile_app.fragments.NotificationFragment;
import com.example.travel_mobile_app.fragments.SocialFragment;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.readableBottomBar.setOnItemSelectedListener(item -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            int itemId = item.getItemId();

            if (itemId == R.id.insta) { // Replace this with the correct ID for the social item
                transaction.replace(R.id.container, new SocialFragment());
//                Toast.makeText(MainActivity.this, "social", Toast.LENGTH_LONG).show();
            } else if(itemId == R.id.bell){
                transaction.replace(R.id.container, new NotificationFragment());
            } else if (itemId == R.id.user) {
                transaction.replace(R.id.container, new AccountFragment());
            }

            transaction.commit();
            return true;
        });

    }
}