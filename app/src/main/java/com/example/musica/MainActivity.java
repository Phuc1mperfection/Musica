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
import com.example.musica.Fragment.SubFragment.AddSongToPlaylistFragment;
import com.example.musica.Model.PlaylistModel;
import com.example.musica.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
            AddSongToPlaylistFragment addSongToPlaylistFragment = new AddSongToPlaylistFragment();

            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);

            libraryFragment.setArguments(bundle);
            addSongToPlaylistFragment.setArguments(bundle);
            replaceFragment(libraryFragment);
            replaceFragment(addSongToPlaylistFragment);
        } else {
            Log.d("MainActivity", "User not logged in");
        }

        // Kiểm tra intent để xác định xem cần mở HomeFragment hay không
        if (getIntent().getBooleanExtra("goToHomeFragment", false)) {
            replaceFragment(new HomeFragment());
        } else {
            replaceFragment(new HomeFragment());  // Load HomeFragment mặc định
        }

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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);

            fragment.setArguments(bundle);

            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Log.d("MainActivity", "No current user");
        }
    }

    private void openCreatePlaylistAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.create_playlist_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        TextInputEditText inputDialog = dialogView.findViewById(R.id.editTextDialog);
        CardView confirmButton = dialogView.findViewById(R.id.confirm_button);
        CardView cancelButton = dialogView.findViewById(R.id.cancel_button);
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getUid();
        playlistName = String.valueOf(inputDialog.getText());

        if (!playlistName.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference playlistsRef = db.collection("playlists");

            PlaylistModel playlist = new PlaylistModel(playlistName, userId,"https://firebasestorage.googleapis.com/v0/b/musicproject-53d9d.appspot.com/o/playlist.png?alt=media&token=bf8bce8e-926d-4a15-94cb-488135dcae41",new ArrayList<>());

            playlistsRef.add(playlist)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Playlist added with ID: " + documentReference.getId());
                            dialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding playlist", e);
                        }
                    });
        } else {
            Toast.makeText(this, "Please enter a playlist name", Toast.LENGTH_SHORT).show();
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                String userId = mAuth.getUid();
                playlistName = String.valueOf(inputDialog.getText());

                if (!playlistName.isEmpty()) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference playlistsRef = db.collection("playlists");

                    // Kiểm tra xem tên playlist đã tồn tại chưa
                    playlistsRef.whereEqualTo("name", playlistName)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().size() > 0) {
                                        } else {
                                            // Nếu tên playlist chưa tồn tại, thêm vào Firestore
                                            PlaylistModel playlist = new PlaylistModel(playlistName, userId, "https://firebasestorage.googleapis.com/v0/b/musicproject-53d9d.appspot.com/o/playlist.png?alt=media&token=bf8bce8e-926d-4a15-94cb-488135dcae41", new ArrayList<>());

                                            playlistsRef.add(playlist)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.d(TAG, "Playlist added with ID: " + documentReference.getId());
                                                            dialog.dismiss();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error adding playlist", e);
                                                        }
                                                    });
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
            }
        });

    }
}
