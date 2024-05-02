package com.example.musica.Fragment;

import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musica.Adapter.ArtistsAdapter;
import com.example.musica.Adapter.CategoriesAdapter;
import com.example.musica.Model.ArtistsModel;
import com.example.musica.Model.CategoryModel;
import com.example.musica.R;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.cardview.widget.CardView;
public class HomeFragment extends Fragment {

    private List<ArtistsModel> artistsList;
    private RecyclerView recyclerView;
    private ArtistsAdapter adapter;
    private List<CategoryModel> categoriesList;
    private RecyclerView categoriesRecyclerView;
    private CategoriesAdapter categoriesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.artists_recycler_view);
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        prepareArtistsData();
        setupCategoriesRecyclerView();
        prepareCategoriesData();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        artistsList = new ArrayList<>();
        adapter = new ArtistsAdapter(artistsList); // Không truyền Context vào constructor nữa
        recyclerView.setAdapter(adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void prepareArtistsData() {
        CollectionReference artistsRef = FirebaseFirestore.getInstance().collection("artists");
        artistsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                artistsList.clear();  // Clear the list before adding new data
                for (DocumentSnapshot doc : task.getResult()) {
                    String name = doc.getString("name");
                    String imageUrl = doc.getString("imgUrl");
                    ArtistsModel artist = new ArtistsModel(name, imageUrl);
                    artistsList.add(artist);
                }
                Log.d("HomeFragment", "Number of artists retrieved: " + artistsList.size());
                Log.d("HomeFragment", "Artists data:");
                for (ArtistsModel artist : artistsList) {
                    Log.d("HomeFragment", "- Name: " + artist.getName());
                    Log.d("HomeFragment", "  Image URL: " + artist.getImgUrl());
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Failed to load artists: " + task.getException().getMessage(), LENGTH_SHORT).show();
            }
        });
    }

    private void setupCategoriesRecyclerView() {
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoriesList = new ArrayList<>(); // Initialize categories list
        categoriesAdapter = new CategoriesAdapter(getContext(), categoriesList); // Create adapter
        categoriesRecyclerView.setAdapter(categoriesAdapter); // Set adapter to RecyclerView
    }

    @SuppressLint("NotifyDataSetChanged")
    private void prepareCategoriesData() {
        CollectionReference categoriesRef = FirebaseFirestore.getInstance().collection("categories");
        categoriesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                categoriesList.clear();  // Clear the list before adding new data
                for (DocumentSnapshot doc : task.getResult()) {
                    if (doc.contains("name") && doc.contains("imgUrl")) {
                        String name = doc.getString("name");
                        String imageUrl = doc.getString("imgUrl");
                        List<String> songs = (List<String>) doc.get("songs"); // Get list of songs
                        Log.d("HomeFragment", "Number of songs: " + (songs != null ? songs.size() : 0)); // Log the size of songs list
                        CategoryModel category = new CategoryModel(name, imageUrl, songs);
                        categoriesList.add(category);
                    } else {
                        Log.w("HomeFragment", "Skipping category with missing field(s): " + doc.getId());
                    }
                }
                categoriesAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Failed to load categories: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
