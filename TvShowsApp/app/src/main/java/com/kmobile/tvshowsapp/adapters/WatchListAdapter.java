package com.kmobile.tvshowsapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kmobile.tvshowsapp.R;
import com.kmobile.tvshowsapp.databinding.ItemContainerTvShowBinding;
import com.kmobile.tvshowsapp.listeners.WatchlistListener;
import com.kmobile.tvshowsapp.models.TVShow;

import java.util.List;

public class WatchListAdapter extends RecyclerView.Adapter<WatchListAdapter.WatchListViewHolder>{

    private List<TVShow> tvShows;
    private LayoutInflater layoutInflater;
    private WatchlistListener watchlistListener;

    public WatchListAdapter(List<TVShow> tvShows, WatchlistListener watchlistListener){
        this.tvShows = tvShows;
        this.watchlistListener = watchlistListener;
    }

    @NonNull
    @Override
    public WatchListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemContainerTvShowBinding tvShowBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.item_container_tv_show, parent, false
        );

        return new WatchListViewHolder(tvShowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchListViewHolder holder, int position) {
        holder.bindTVShow(tvShows.get(position));
    }

    @Override
    public int getItemCount() {
        return tvShows.size();
    }

    class WatchListViewHolder extends RecyclerView.ViewHolder{

        private ItemContainerTvShowBinding itemContainerTvShowBinding;

        public WatchListViewHolder(ItemContainerTvShowBinding itemContainerTvShowBinding){
            super(itemContainerTvShowBinding.getRoot());

            this.itemContainerTvShowBinding = itemContainerTvShowBinding;
        }

        public void bindTVShow(TVShow tvShow){
            itemContainerTvShowBinding.setTvShow(tvShow);
            itemContainerTvShowBinding.executePendingBindings();

            itemContainerTvShowBinding.getRoot().setOnClickListener(v -> {
                watchlistListener.onTvShowClicked(tvShow);
            });

            itemContainerTvShowBinding.imageDelete.setVisibility(View.VISIBLE);

            itemContainerTvShowBinding.imageDelete.setOnClickListener(v -> {
                watchlistListener.removeTvShowFromWatchlist(tvShow, getAdapterPosition());
            });
        }
    }
}