package com.example.musica.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent; // Not used in this update, but included for clarity
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musica.Fragment.SongListFragment;
import com.example.musica.Model.CategoryModel;
import com.example.musica.R;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {

    private Context context;
    private List<CategoryModel> categoriesList;

    public CategoriesAdapter(Context context, List<CategoryModel> categoriesList) {
        this.context = context;
        this.categoriesList = categoriesList;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.categories_item_row, parent, false);
        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        CategoryModel category = categoriesList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the FragmentManager from the Activity (assuming context is an Activity)
                FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

                // Create a new SongListFragment instance and pass the selected category
                CategoryModel selectedCategory = category; // Assign the current category
                SongListFragment songListFragment = SongListFragment.newInstance(selectedCategory);

                // Begin a fragment transaction
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Replace the current fragment with SongListFragment
                transaction.replace(R.id.frame_layout, songListFragment); // Replace with your container ID
                transaction.addToBackStack(null); // Add to back stack (optional)
                transaction.commit();
            }
        });

        // Set the category name to the TextView
        holder.nameCategories.setText(category.getName());

        // Load the category image using Glide into the ImageView
        if (category.getImgUrl() != null) {
            Glide.with(context)
                    .load(category.getImgUrl())
                    .placeholder(R.drawable.saxophone_svgrepo_com) // Placeholder image while loading
                    .into(holder.imgCategories);
        } else {
            // If the image URL is null, set a default placeholder image
            holder.imgCategories.setImageResource(R.drawable.baseline_home_24);
        }
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    class CategoriesViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCategories;
        TextView nameCategories;

        public CategoriesViewHolder(View itemView) {
            super(itemView);
            imgCategories = itemView.findViewById(R.id.imgCategories);
            nameCategories = itemView.findViewById(R.id.nameCategories);
        }
    }
}