package com.kmobile.tvshowsapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.kmobile.tvshowsapp.R;
import com.kmobile.tvshowsapp.adapters.WatchListAdapter;
import com.kmobile.tvshowsapp.databinding.ActivityWatchListBinding;
import com.kmobile.tvshowsapp.listeners.WatchlistListener;
import com.kmobile.tvshowsapp.models.TVShow;
import com.kmobile.tvshowsapp.utilities.TempDataHolder;
import com.kmobile.tvshowsapp.viewmodels.WatchlistViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WatchListActivity extends AppCompatActivity implements WatchlistListener {

    private ActivityWatchListBinding binding;
    private WatchlistViewModel viewModel;
    private WatchListAdapter adapter;
    private List<TVShow> watchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_watch_list);
        doInitialization();
    }

    private void doInitialization(){
        viewModel = new ViewModelProvider(this).get(WatchlistViewModel.class);
        binding.imageBack.setOnClickListener((v) -> onBackPressed());
        watchList = new ArrayList<>();
        loadWatchList();
    }

    private void loadWatchList(){
        binding.setIsLoading(true);

        CompositeDisposable compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(viewModel.loadWatchList().subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShows -> {
                    binding.setIsLoading(false);

                    if(!watchList.isEmpty()){
                        watchList.clear();
                    }

                    watchList.addAll(tvShows);
                    adapter = new WatchListAdapter(watchList, this);
                    binding.watchlistRecyclerView.setAdapter(adapter);
                    binding.watchlistRecyclerView.setVisibility(View.VISIBLE);
                    compositeDisposable.dispose();

                }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(TempDataHolder.IS_WATCHLIST_UPDATED){
            loadWatchList();
            TempDataHolder.IS_WATCHLIST_UPDATED = false;
        }
    }

    @Override
    public void onTvShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
        intent.putExtra("tvShow",tvShow);
        startActivity(intent);
    }

    @Override
    public void removeTvShowFromWatchlist(TVShow tvShow, int position) {
        CompositeDisposable compositeDisposableForDelete = new CompositeDisposable();

        compositeDisposableForDelete.add(viewModel.removeTvShowFromWatchList(tvShow)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    watchList.remove(tvShow);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                    compositeDisposableForDelete.dispose();
                }));
    }
}