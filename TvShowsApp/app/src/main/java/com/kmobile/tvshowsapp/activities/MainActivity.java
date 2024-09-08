package com.kmobile.tvshowsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.kmobile.tvshowsapp.R;
import com.kmobile.tvshowsapp.adapters.TVShowsAdapter;
import com.kmobile.tvshowsapp.databinding.ActivityMainBinding;
import com.kmobile.tvshowsapp.listeners.TVShowListener;
import com.kmobile.tvshowsapp.models.TVShow;
import com.kmobile.tvshowsapp.models.TVShowDetails;
import com.kmobile.tvshowsapp.viewmodels.MostPopularTVShowsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TVShowListener {
    private ActivityMainBinding binding;
    private MostPopularTVShowsViewModel viewModel;
    private List<TVShow> tvShowList = new ArrayList<>();
    private TVShowsAdapter adapter;
    private int currentPage = 1;
    private int totalAvailablePages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        doInitialization();
    }

    private void doInitialization(){
        binding.tvShowsRecyclerView.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);

        adapter = new TVShowsAdapter(tvShowList, this);
        binding.tvShowsRecyclerView.setAdapter(adapter);

        binding.tvShowsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!binding.tvShowsRecyclerView.canScrollVertically(1)){
                    if(currentPage <= totalAvailablePages){
                        currentPage += 1;
                        getMostPopularTVShows();
                    }
                }
            }
        });

        binding.imageWatchList.setOnClickListener((v) -> startActivity(new Intent(getApplicationContext(), WatchListActivity.class)));
        binding.imageSearch.setOnClickListener((v) -> startActivity(new Intent(getApplicationContext(), SearchActivity.class)));

        getMostPopularTVShows();
    }

    private void getMostPopularTVShows(){
        toggleLoading();

        viewModel.getMostPopularTVShows(currentPage).observe(this, mostPopularTvShowsResponse -> {
            toggleLoading();

            if(mostPopularTvShowsResponse != null){

                totalAvailablePages = mostPopularTvShowsResponse.getTotalPages();

                if(mostPopularTvShowsResponse.getTvShows() != null){
                    int oldCount = tvShowList.size();
                    tvShowList.addAll(mostPopularTvShowsResponse.getTvShows());
                    adapter.notifyItemRangeInserted(oldCount, tvShowList.size());
                }
            }
        });
    }

    private void toggleLoading(){
        if(currentPage == 1){

            if(binding.getIsLoading() != null && binding.getIsLoading()){
                binding.setIsLoading(false);
            }else {
                binding.setIsLoading(true);
            }

        }else{

            if(binding.getIsLoadingMore() != null && binding.getIsLoadingMore()){
                binding.setIsLoadingMore(false);
            }else{
                binding.setIsLoadingMore(true);
            }

        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }
}