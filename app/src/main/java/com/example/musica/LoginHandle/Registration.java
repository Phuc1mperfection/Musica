package com.example.musica.LoginHandle;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musica.R;
import com.google.android.material.textfield.TextInputEditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musica.MainActivity;
import com.example.musica.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
public class Registration extends AppCompatActivity {
    Button btnRegist;
    TextView toLogin;
    private TextInputEditText editTextName, editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        // Find views by ID
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnRegist = findViewById(R.id.button_register);
        toLogin = findViewById(R.id.to_login);
        // Set up login link click listener
//        toLogin.setOnClickListener(v -> startActivity(new Intent(Registration.this, Login.class)));  // Concise way using lambda expression
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
        // Register button click listener
        btnRegist.setOnClickListener(v -> {
            String name = String.valueOf(editTextName.getText());
            String email = String.valueOf(editTextEmail.getText());
            String password = String.valueOf(editTextPassword.getText());

            // Input validation
            if (isEmpty(name)) {
                showError(editTextName, "Please enter your name!");
                return;
            }
            if (isEmpty(email)) {
                showError(editTextEmail, "Please enter your email!");
                return;
            }
            if (isEmpty(password)) {
                showError(editTextPassword, "Please enter your password!");
                return;
            }


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Registration.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                            // Handle successful registration (e.g., navigate to main activity)
                        } else {
                            handleRegistrationError(task.getException());
                        }
                    });

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is already signed in (same logic as before)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Helper methods for error handling and validation
    private boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    private void showError(TextInputEditText editText, String error) {
        TextInputLayout inputLayout = (TextInputLayout) editText.getParent();  // Assuming the EditText is inside a TextInputLayout
        inputLayout.setErrorEnabled(true);
        inputLayout.setError(error);
    }

    private void handleRegistrationError(Exception e) {
        if (e instanceof FirebaseAuthUserCollisionException) {
            showError(editTextEmail, "User with this email already exists!");
        } else {
            Toast.makeText(Registration.this, "Registration failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
