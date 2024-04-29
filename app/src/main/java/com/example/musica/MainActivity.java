package com.example.musica;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.musica.Fragment.LibraryFragment;
import com.example.musica.Fragment.SongListFragment;
import com.example.musica.Fragment.SubscriptionsFragment;
import com.example.musica.Fragment.UserFragment;
import com.example.musica.LoginHandle.Login;
import com.example.musica.databinding.ActivityMainBinding;

import com.example.musica.Fragment.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Store the resource ID in a variable

            if (itemId == R.id.homepage) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.subscriptions) {
                replaceFragment(new SubscriptionsFragment());
            } else if (itemId == R.id.library) {
                replaceFragment(new LibraryFragment());
            } else if (itemId == R.id.user) {
                replaceFragment(new UserFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null); // Add fragment to back stack
        fragmentTransaction.commit();
    }
}