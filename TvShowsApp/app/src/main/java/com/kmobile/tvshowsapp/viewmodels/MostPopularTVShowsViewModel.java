package com.kmobile.tvshowsapp.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.kmobile.tvshowsapp.repositories.MostPopularTVShowsRepository;
import com.kmobile.tvshowsapp.responses.TVShowsResponse;

public class MostPopularTVShowsViewModel extends ViewModel {
    private MostPopularTVShowsRepository repository;

    public MostPopularTVShowsViewModel(){
        repository = new MostPopularTVShowsRepository();
    }

    public LiveData<TVShowsResponse> getMostPopularTVShows(int page){
        return repository.getMostPopularTVShows(page);
    }
}