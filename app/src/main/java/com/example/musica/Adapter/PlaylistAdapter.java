package com.example.musica.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musica.Model.PlaylistModel;
import com.example.musica.PlaylistDetailActivity;
import com.example.musica.R;
import com.example.musica.databinding.ItemPlaylistBinding;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    private boolean isLibraryFragment = true;
    public static List<PlaylistModel> playlistList;
    private String songId;
    private String currentSongId;

    public void setIsLibraryFragment(boolean isLibraryFragment) {
        this.isLibraryFragment = isLibraryFragment;
    }
    // Constructor
    public PlaylistAdapter(List<PlaylistModel> playlistList, Context context) {
        PlaylistAdapter.playlistList = playlistList;

    }
    public List<PlaylistModel> getPlaylistList() {
        return playlistList;
    }
    public void setSongId(String songId) {
        this.songId = songId;
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
    public String getSongId() {
        return currentSongId;
    }
    public void setCurrentSongId(String songId) {
        this.currentSongId = songId;
        notifyDataSetChanged(); // Cập nhật giao diện khi có songId mới
    }
    // ViewHolder class
    public class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private final ItemPlaylistBinding binding;

        public PlaylistViewHolder(@NonNull ItemPlaylistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(view -> {
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
            });
        }
        // Trong phương thức bind() của PlaylistViewHolder
        public void bind(PlaylistModel playlist) {
            // Set data to views

            Log.d("PlaylistAdapter", "Playlist name: " + playlist.getName());
            binding.namePlaylist.setText(playlist.getName());
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

            // Kiểm tra fragment hiện tại và ẩn hiện checkbox tùy thuộc vào đó
            if (isLibraryFragment) {
                // Ẩn checkbox
                binding.checkboxAddToPlaylist.setVisibility(View.GONE);
            } else {
                // Hiển thị checkbox
                binding.checkboxAddToPlaylist.setVisibility(View.VISIBLE);
            }

            // Kiểm tra xem currentSongId có tồn tại trong danh sách songs của playlist hay không
            if (playlist.getSongs() != null && playlist.getSongs().contains(currentSongId)) {
                // If the song is in the playlist, set the checkbox to checked
                binding.checkboxAddToPlaylist.setChecked(true);
                // Log checked state
                Log.d("PlaylistAdapter", "Checkbox checked for song ID: " + currentSongId);
            } else {
                // If the song is not in the playlist, set the checkbox to unchecked
                binding.checkboxAddToPlaylist.setChecked(false);
                // Log unchecked state
                Log.d("PlaylistAdapter", "Checkbox unchecked for song ID: " + currentSongId);
            }
            binding.checkboxAddToPlaylist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    List<String> songs = playlist.getSongs();
                    if (isChecked){
                        if (!songs.contains(currentSongId)){
                            songs.add(currentSongId);
                        }
                    }
                    else {
                        songs.remove(currentSongId);
                    }
                    playlist.setSongs(songs);
                }
            });

        }
    }
}