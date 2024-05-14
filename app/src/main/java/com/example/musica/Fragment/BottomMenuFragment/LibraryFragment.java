package com.example.musica.Fragment.BottomMenuFragment;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import  com.example.musica.databinding.FragmentLibraryBinding;
import com.example.musica.Adapter.PlaylistAdapter;
import com.example.musica.Model.PlaylistModel;
import com.example.musica.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {
    private FirebaseAuth mAuth;
    private boolean isLibraryFragment = true;



    private String playlistName;
    private static final int TYPE_PLAYLIST = 1;
    private static final int TYPE_ADD_SONG_TO_PLAYLIST = 2;

    private FragmentLibraryBinding binding;
    private List<PlaylistModel> playlistList;
    private PlaylistAdapter playlistAdapter;
    private CheckBox checkbox;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Lấy userId từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String userId = bundle.getString("userId");

            // Khởi tạo danh sách playlistList
            playlistList = new ArrayList<>();
            isLibraryFragment = true;

            // Khởi tạo adapter và set cho RecyclerView
            playlistAdapter = new PlaylistAdapter(playlistList, requireContext());
            binding.musicRecyclerViewLibrary.setAdapter(playlistAdapter);

            // Set LayoutManager cho RecyclerView
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            binding.musicRecyclerViewLibrary.setLayoutManager(layoutManager);

            // Set isLibraryFragment cho adapter
            playlistAdapter.setIsLibraryFragment(isLibraryFragment);

            loadPlaylistData(userId);
        } else {
            Log.d("LibraryFragment", "Bundle is null");
        }
        binding.addPlaylistBtn.setOnClickListener(v -> openCreatePlaylistAlertDialog());
    }

    private void loadPlaylistData(String userId) {
        Log.d("LibraryFragment", "Loading playlist data for user: " + userId);
        CollectionReference playlistsRef = FirebaseFirestore.getInstance().collection("playlists");

        playlistsRef.whereEqualTo("userID", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        playlistList.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            String playlistId = doc.getId();
                            String name = doc.getString("name");
                            String userID = doc.getString("userID");
                            String imgUrl = doc.getString("imgUrl");
                            List<String> songs = (List<String>) doc.get("songs");
                            Log.d("LibraryFragment", "Playlist ID: " + playlistId);
                            Log.d("LibraryFragment", "Document data: " + doc.getData());

                            if (userId.equals(userID)) {
                                PlaylistModel playlist = new PlaylistModel(name, userID, imgUrl, songs);
                                // Thêm playlist vào danh sách
                                playlist.setPlaylistId(playlistId); // Đặt playlistId cho playlist
                                // Thêm playlist vào danh sách
                                playlistList.add(playlist);
                            }
                        }

                        // Cập nhật Adapter để hiển thị danh sách mới
                        playlistAdapter.notifyDataSetChanged();
                    } else {
                        // Hiển thị thông báo lỗi nếu không thành công
                        Toast.makeText(getContext(), "Failed to load playlists: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void openCreatePlaylistAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.create_playlist_dialog, null);
        builder.setView(dialogView);

        TextInputEditText inputDialog = dialogView.findViewById(R.id.editTextDialog);
        CardView confirmButton = dialogView.findViewById(R.id.confirm_button);
        CardView cancelButton  = dialogView.findViewById(R.id.cancel_button);
        AlertDialog alertDialog = builder.create();
        cancelButton.setOnClickListener(v -> alertDialog.dismiss());


        confirmButton.setOnClickListener(view -> {
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
                                alertDialog.dismiss();
                                loadPlaylistData(userId);
                            }
                        })
                        .addOnFailureListener(e -> Log.w(TAG, "Error adding playlist", e));
            } else {
                // Hiển thị thông báo lỗi nếu tên playlist rỗng
                Toast.makeText(requireContext(), "Please enter a playlist name", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

