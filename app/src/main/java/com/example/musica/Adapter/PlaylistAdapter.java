package com.example.musica.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musica.Model.PlaylistModel;
import com.example.musica.PlaylistDetailActivity;
import com.example.musica.R;
import com.example.musica.databinding.ItemArtistsBinding;
import com.example.musica.databinding.ItemPlaylistBinding;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private static List<PlaylistModel> playlistList;
    private Context context;
    private ImageButton imageButton;

    // Constructor
    public PlaylistAdapter(List<PlaylistModel> playlistList, Context context) {
        this.playlistList = playlistList;
        this.context = context;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlaylistBinding binding = ItemPlaylistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PlaylistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        PlaylistModel playlist = playlistList.get(position);
        holder.bind(playlist);

    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    // ViewHolder class
    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private ItemPlaylistBinding binding;
        public PlaylistViewHolder(@NonNull ItemPlaylistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Lấy playlist được chọn tại vị trí position
                        PlaylistModel selectedPlaylist = playlistList.get(position);
                        Log.d("PlaylistAdapter", "Clicked playlist name: " + selectedPlaylist.getName());

                        // Tạo Intent để chuyển sang PlaylistDetailActivity
                        Intent intent = new Intent(view.getContext(), PlaylistDetailActivity.class);
                        // Truyền thông tin playlist thông qua Intent
                        intent.putExtra("playlistName", selectedPlaylist.getName());
                        // Thực hiện chuyển sang PlaylistDetailActivity
                        view.getContext().startActivity(intent);
                    }
                }

            });
        }

        public void bind(PlaylistModel playlist) {
            // Set data to views
            if (playlist.getName().equals("Liked song")) {
                // Nếu tên playlist là "Liked songs", sử dụng ảnh mặc định
                Glide.with(itemView)
                        .load(R.drawable.like_playlist)
                        .into(binding.imgPlaylist);
            } else {
                // Sử dụng ảnh từ URL nếu không phải là "Liked songs"
                Glide.with(itemView)
                        .load(playlist.getImgUrl())
                        .into(binding.imgPlaylist);
            }

            binding.namePlaylist.setText(playlist.getName());

            int songCount = playlist.getSongs().size();
            String songCountText = songCount + " song" + (songCount != 1 ? "s" : "");
            binding.songCount.setText(songCountText);
        }
    }
}
