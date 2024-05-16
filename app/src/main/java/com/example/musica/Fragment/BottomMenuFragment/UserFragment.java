package com.example.musica.Fragment.BottomMenuFragment;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.musica.LoginHandle.Login;
import com.example.musica.MainActivity;
import com.example.musica.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {
    FirebaseUser users;
    TextView textView;
    FirebaseAuth auth;
    Button button;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        super.onViewCreated(view, savedInstanceState);
        button = requireView().findViewById(R.id.signout);
        textView = requireView().findViewById(R.id.welcome);
        users = auth.getCurrentUser();
        if (users == null) {
            Intent intent = new Intent(getContext(), MainActivity.class);

            startActivity(intent);
        }else{
            textView.setText("Hello "+ users.getEmail());
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // Khởi tạo các button và thêm sự kiện cho chúng
        LinearLayout emailButton = view.findViewById(R.id.emailChangeBtn);
        LinearLayout imageButton = view.findViewById(R.id.avatarChangeBtn);
        LinearLayout passwordButton = view.findViewById(R.id.passwordChangeBtn);

        users = auth.getCurrentUser();
        if (users == null) {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        } else {
            textView.setText("Hello " + users.getEmail());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        emailButton.setOnClickListener(view1 -> showEmailChangeDialog());
        imageButton.setOnClickListener(view12 -> showImageChangeDialog());
        passwordButton.setOnClickListener(view13 -> showPasswordChangeDialog());
    }

    private void showEmailChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.change_email_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        TextInputEditText currentEmail = dialogView.findViewById(R.id.editTextDialog);
        TextInputEditText newEmail = dialogView.findViewById(R.id.editTextDialog2);
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String email = currentUser.getEmail();
            currentEmail.setText(email);
            currentEmail.setEnabled(false);  // Đặt trạng thái không thể chỉnh sửa
        }

        CardView cancelButton = dialogView.findViewById(R.id.cancelBtn);
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        CardView confirmButton = dialogView.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(v -> {
            String newEmailStr = newEmail.getText().toString().trim();
            if (newEmailStr.isEmpty()) {
                newEmail.setError("Email cannot be empty");
                newEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmailStr).matches()) {
                newEmail.setError("Invalid email format");
                newEmail.requestFocus();
            } else {
                // Gửi email xác thực tới địa chỉ email mới
                currentUser.verifyBeforeUpdateEmail(newEmailStr)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Verification email sent to " + newEmailStr, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error sending verification email", e);
                            Toast.makeText(getContext(), "Failed to send verification email", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }



    private void showImageChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.change_image_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        CardView cancelButton = dialogView.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void showPasswordChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.change_password_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        TextInputLayout emailLayout1 = dialogView.findViewById(R.id.emailLayout1);  // Layout cho mật khẩu hiện tại
        TextInputLayout emailLayout2 = dialogView.findViewById(R.id.emailLayout2);
        TextInputLayout emailLayout3 = dialogView.findViewById(R.id.emailLayout3);
        TextInputEditText currentPasswordEditText = dialogView.findViewById(R.id.editTextDialog1);  // EditText cho mật khẩu hiện tại
        TextInputEditText newPasswordEditText = dialogView.findViewById(R.id.editTextDialog2);
        TextInputEditText confirmPasswordEditText = dialogView.findViewById(R.id.editTextDialog3);

        CardView cancelButton = dialogView.findViewById(R.id.cancelBtn);
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        CardView changeButton = dialogView.findViewById(R.id.confirm_button);
        changeButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (currentPassword.isEmpty()) {
                emailLayout1.setError("Current password cannot be empty");
                emailLayout1.setErrorEnabled(true);
                return;
            }

            if (newPassword.isEmpty()) {
                emailLayout2.setError("Password cannot be empty");
                emailLayout2.setErrorEnabled(true);
                emailLayout3.setError(null);
                emailLayout3.setErrorEnabled(false);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                emailLayout2.setError(null);
                emailLayout2.setErrorEnabled(false);
                emailLayout3.setError("Passwords do not match");
                emailLayout3.setErrorEnabled(true);
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Reauthenticate the user with the current password before updating the password
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                user.reauthenticate(credential)
                        .addOnSuccessListener(aVoid -> {
                            // Reauthentication successful, now update the password
                            user.updatePassword(newPassword)
                                    .addOnSuccessListener(aVoid1 -> {
                                        Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error updating password", e);
                                        Toast.makeText(getContext(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error reauthenticating", e);
                            Toast.makeText(getContext(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }





    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);

    }

}