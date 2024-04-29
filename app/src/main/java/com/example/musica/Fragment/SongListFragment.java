package com.example.musica.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.musica.Model.CategoryModel;
import com.example.musica.R;
import com.example.musica.databinding.FragmentSongListBinding;

public class SongListFragment extends Fragment {

    private static final String ARG_PARAM_CATEGORY = "category";
    private FragmentSongListBinding binding; // View Binding reference
    private CategoryModel selectedCategory;

    public SongListFragment() {
        // Required empty public constructor
    }

    public static SongListFragment newInstance(CategoryModel category) {
        SongListFragment fragment = new SongListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedCategory = getArguments().getParcelable(ARG_PARAM_CATEGORY);
            if (selectedCategory != null) {
                Log.d("SongListFragment", "Received Category: " + selectedCategory.getName());
                Log.d("SongListFragment", "Received Category: " + selectedCategory.getImgUrl());

            } else {
                Log.w("SongListFragment", "No CategoryModel object received!");
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout using View Binding
        binding = FragmentSongListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Access views using binding
        ImageView imgCategory = binding.imgCategories;
        TextView txtCategoryName = binding.nameCategories;

        if (selectedCategory != null) {
            txtCategoryName.setText(selectedCategory.getName());

            // Check for image URL and use Glide (assuming proper Glide initialization)
            if (selectedCategory.getImgUrl() != null) {
                Glide.with(requireContext())
                        .load(selectedCategory.getImgUrl())
                        .placeholder(R.drawable.saxophone_svgrepo_com) // Placeholder image while loading
                        .into(imgCategory);
            } else {
                // If the image URL is null, set a default placeholder image
                imgCategory.setImageResource(R.drawable.baseline_home_24);
            }
        } else {
            Log.w("SongListFragment", "No CategoryModel object received!");
        }
    }
}
