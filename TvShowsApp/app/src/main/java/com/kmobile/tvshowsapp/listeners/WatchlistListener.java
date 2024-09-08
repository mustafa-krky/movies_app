package com.kmobile.tvshowsapp.listeners;

import com.kmobile.tvshowsapp.models.TVShow;

public interface WatchlistListener {
    void onTvShowClicked(TVShow tvShow);
    void removeTvShowFromWatchlist(TVShow tvShow, int position);
}