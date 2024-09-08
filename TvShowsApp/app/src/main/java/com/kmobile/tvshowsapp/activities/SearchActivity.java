package com.kmobile.tvshowsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.kmobile.tvshowsapp.R;
import com.kmobile.tvshowsapp.adapters.TVShowsAdapter;
import com.kmobile.tvshowsapp.databinding.ActivitySearchBinding;
import com.kmobile.tvshowsapp.listeners.TVShowListener;
import com.kmobile.tvshowsapp.models.TVShow;
import com.kmobile.tvshowsapp.responses.TVShowsResponse;
import com.kmobile.tvshowsapp.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity implements TVShowListener {

    private ActivitySearchBinding binding;
    private SearchViewModel viewModel;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter adapter;
    private int currentPage = 1;
    private int totalAvailablePage = 1;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        doInitialization();
    }

    private void doInitialization(){
        binding.imageBack.setOnClickListener((v) -> onBackPressed());
        binding.tvShowsRecyclerView.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        adapter = new TVShowsAdapter(tvShows, this);
        binding.tvShowsRecyclerView.setAdapter(adapter);

        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(timer != null){
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().trim().isEmpty()){
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    currentPage = 1;
                                    totalAvailablePage = 1;
                                    searchTvShow(s.toString());
                                }
                            });
                        }
                    }, 800);
                }else {
                    tvShows.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        binding.tvShowsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!binding.tvShowsRecyclerView.canScrollVertically(1)){
                    if(!binding.inputSearch.getText().toString().isEmpty()){
                        if(currentPage < totalAvailablePage){
                            currentPage += 1;
                            searchTvShow(binding.inputSearch.getText().toString());
                        }
                    }
                }
            }
        });

        binding.inputSearch.requestFocus();
    }

    private void searchTvShow(String query){
        toggleLoading();

        viewModel.searchTVShow(query, currentPage).observe(this, new Observer<TVShowsResponse>() {
            @Override
            public void onChanged(TVShowsResponse tvShowsResponse) {
                toggleLoading();

                if(tvShowsResponse != null){
                    totalAvailablePage = tvShowsResponse.getTotalPages();
                    if(tvShowsResponse.getTvShows() != null){
                        int oldCount = tvShows.size();
                        tvShows.addAll(tvShowsResponse.getTvShows());
                        adapter.notifyItemRangeInserted(oldCount, tvShows.size());
                    }
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