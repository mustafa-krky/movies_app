package com.kmobile.tvshowsapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kmobile.tvshowsapp.database.TVShowsDatabase;
import com.kmobile.tvshowsapp.models.TVShow;
import com.kmobile.tvshowsapp.repositories.TVShowDetailsRepository;
import com.kmobile.tvshowsapp.responses.TVShowDetailsResponse;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class TVShowDetailsViewModel extends AndroidViewModel {

    private TVShowDetailsRepository repository;
    private TVShowsDatabase tvShowsDatabase;

    public TVShowDetailsViewModel(@NonNull Application application){
        super(application);
        tvShowsDatabase = TVShowsDatabase.getTvShowsDatabase(application);
        repository = new TVShowDetailsRepository();
    }

    public LiveData<TVShowDetailsResponse> getTVShowDetails(String tvShowID){
        return repository.getTVShowDetails(tvShowID);
    }

    public Completable addToWatchList(TVShow tvShow){
        return tvShowsDatabase.tvShowDao().addToWatchList(tvShow);
    }

    public Flowable<TVShow> getTvShowFromWatchList(String tvShowId){
        return tvShowsDatabase.tvShowDao().getTvShowFromWatchlist(tvShowId);
    }

    public Completable removeTvShowFromWatchList(TVShow tvShow){
        return tvShowsDatabase.tvShowDao().removeFromWatchList(tvShow);
    }
}