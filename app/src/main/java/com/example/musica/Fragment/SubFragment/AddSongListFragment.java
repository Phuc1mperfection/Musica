package com.example.musica.Fragment.SubFragment;

import static androidx.databinding.adapters.TextViewBindingAdapter.setText;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musica.Adapter.SongListAdapter;
import com.example.musica.databinding.FragmentAddSongListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddSongListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddSongListFragment extends Fragment {
    private List<String> songIdList;

    private static final String TAG = "AddSongListFragment"; // For logging
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentAddSongListBinding binding;
    private FragmentManager fragmentManager;
    public AddSongListFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Ensure fragment is attached to activity before accessing FragmentManager
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddSongListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddSongListFragment newInstance(String param1, String param2) {
        AddSongListFragment fragment = new AddSongListFragment();
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
        binding = FragmentAddSongListBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

        songIdList = new ArrayList<>();

        RecyclerView musicRecyclerViewAdd = binding.musicRecyclerViewAdd;
        SongListAdapter adapter = new SongListAdapter(songIdList);
        musicRecyclerViewAdd.setLayoutManager(new LinearLayoutManager(requireContext()));
        musicRecyclerViewAdd.setAdapter(adapter);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                } else {
                    Log.d(TAG, "Back stack is empty. Navigating to previous fragment or homepage");

                    fragmentManager.popBackStack("previous_fragment_tag", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                }
            }
        });
        getAllSongs();
    }
    private void getAllSongs() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference songsRef = db.collection("songs"); // Replace with your actual collection name

        songsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // No songs found - handle empty list
                            Toast.makeText(getActivity(), "No songs found in the database.", Toast.LENGTH_SHORT).show();
                        } else {
                            songIdList.clear(); // Clear existing songs

                            for (DocumentSnapshot doc : task.getResult()) {
                                String songId = doc.getId();
                                songIdList.add(songId);

                                // Access additional song data if needed (optional)
                                if (doc.getData() != null) {
                                    Log.d(TAG, "Song data for " + songId + ": " + doc.getData());
                                }
                            }

                            // Update UI with the retrieved song IDs
                            updateSongList(songIdList);
                        }
                    } else {
                        Log.w(TAG, "Error getting songs: ", task.getException());
                        // Handle errors appropriately (e.g., display error message)
                    }
                });
    }
    private void updateSongList(List<String> songIdList) {
        // Update the SongListAdapter with the retrieved song IDs
        if (binding != null) {
            SongListAdapter adapter = new SongListAdapter(songIdList);
            binding.musicRecyclerViewAdd.setAdapter(adapter);
        }
    }
}