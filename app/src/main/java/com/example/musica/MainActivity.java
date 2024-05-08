package com.example.musica;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;
import static androidx.core.content.ContentProviderCompat.requireContext;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.musica.Fragment.BottomMenuFragment.HomeFragment;
import com.example.musica.Fragment.BottomMenuFragment.LibraryFragment;
import com.example.musica.Fragment.BottomMenuFragment.SearchFragment;
import com.example.musica.Fragment.BottomMenuFragment.UserFragment;
import com.example.musica.Model.PlaylistModel;
import com.example.musica.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String playlistName;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d("MainActivity", "User ID: " + userId);
            // Gọi fragment LibraryFragment và truyền userId vào đó
            LibraryFragment libraryFragment = new LibraryFragment();
            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);
            libraryFragment.setArguments(bundle);
            replaceFragment(libraryFragment);
        } else {
            Log.d("MainActivity", "User not logged in");
        }


        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);
        binding.floatingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreatePlaylistAlertDialog();
            }
        });

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId(); // Store the resource ID in a variable

            if (itemId == R.id.homepage) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.subscriptions) {
                replaceFragment(new SearchFragment());
            } else if (itemId == R.id.library) {
                replaceFragment(new LibraryFragment());
            } else if (itemId == R.id.user) {
                replaceFragment(new UserFragment());
            }
            return true;
        });
    }


    // Trong phương thức replaceFragment của MainActivity
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Lấy thông tin người dùng hiện tại từ Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Tạo Bundle và đặt userId vào đó
            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);

            // Set bundle vào fragment trước khi thay thế
            fragment.setArguments(bundle);

            // Thực hiện thay thế fragment
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.addToBackStack(null); // Add fragment to back stack
            fragmentTransaction.commit();
        } else {
            // Xử lý khi không có người dùng hiện tại
            Log.d("MainActivity", "No current user");
        }
    }
    private void openCreatePlaylistAlertDialog() {
        // Tạo dialog và thiết lập các thành phần của nó
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate layout cho dialog
        View dialogView = inflater.inflate(R.layout.create_playlist_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();
        // Thiết lập các thành phần trong layout dialog
        TextInputEditText inputDialog = dialogView.findViewById(R.id.editTextDialog);
        CardView confirmButton = dialogView.findViewById(R.id.confirm_button);
        CardView cancelButton = dialogView.findViewById(R.id.cancel_button);
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();
        playlistName = String.valueOf(inputDialog.getText());
        if (!playlistName.isEmpty()) {
            // Thêm playlist vào Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference playlistsRef = db.collection("playlists");

            // Tạo một object PlaylistModel
            PlaylistModel playlist = new PlaylistModel(playlistName, userId,"https://firebasestorage.googleapis.com/v0/b/musicproject-53d9d.appspot.com/o/playlist.png?alt=media&token=bf8bce8e-926d-4a15-94cb-488135dcae41",new ArrayList<>());

            // Thêm playlist vào Firestore
            playlistsRef.add(playlist)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Playlist added with ID: " + documentReference.getId());
                            // Đóng dialog sau khi thêm thành công
                            dialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding playlist", e);
                            // Xử lý trường hợp thêm playlist thất bại (nếu cần)
                        }
                    });
        } else {
            // Hiển thị thông báo lỗi nếu tên playlist rỗng
            Toast.makeText(this, "Please enter a playlist name", Toast.LENGTH_SHORT).show();
        }
        // Xử lý sự kiện cho nút xác nhận
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xử lý logic khi nhấn nút cancel_button
                dialog.dismiss(); // Đóng dialog khi nút cancel_button được nhấn
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Thực hiện xử lý khi người dùng nhấn nút xác nhận
                mAuth = FirebaseAuth.getInstance();
                String userId = mAuth.getUid();
                playlistName = String.valueOf(inputDialog.getText());

                // Kiểm tra nếu tên playlist không rỗng
                if (!playlistName.isEmpty()) {
                    // Thêm playlist vào Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference playlistsRef = db.collection("playlists");

                    // Tạo một object PlaylistModel
                    PlaylistModel playlist = new PlaylistModel(playlistName, userId,"https://firebasestorage.googleapis.com/v0/b/musicproject-53d9d.appspot.com/o/playlist.png?alt=media&token=bf8bce8e-926d-4a15-94cb-488135dcae41",new ArrayList<>());

                    // Thêm playlist vào Firestore
                    playlistsRef.add(playlist)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "Playlist added with ID: " + documentReference.getId());
                                    // Đóng dialog sau khi thêm thành công
                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding playlist", e);
                                    // Xử lý trường hợp thêm playlist thất bại (nếu cần)
                                }
                            });
                } else {
                    // Hiển thị thông báo lỗi nếu tên playlist rỗng
                }
            }
        });

    }

}