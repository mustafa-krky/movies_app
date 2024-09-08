package com.kmobile.tvshowsapp.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kmobile.tvshowsapp.R;
import com.kmobile.tvshowsapp.adapters.EpisodesAdapter;
import com.kmobile.tvshowsapp.adapters.ImageSliderAdapter;
import com.kmobile.tvshowsapp.databinding.ActivityTvshowDetailsBinding;
import com.kmobile.tvshowsapp.databinding.LayoutEpisodesBottomSheetBinding;
import com.kmobile.tvshowsapp.models.TVShow;
import com.kmobile.tvshowsapp.utilities.TempDataHolder;
import com.kmobile.tvshowsapp.viewmodels.TVShowDetailsViewModel;

import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailsActivity extends AppCompatActivity {

    private ActivityTvshowDetailsBinding binding;
    private TVShowDetailsViewModel viewModel;
    private BottomSheetDialog episodesBottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBinding;
    private TVShow tvShow;
    private Boolean isTvShowAvailableInWatchList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tvshow_details);
        doInitialization();
    }

    private void doInitialization() {
        viewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);

        binding.imageBack.setOnClickListener(v -> onBackPressed());

        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");

        checkTvShowInWatchList();
        getTVShowDetails();
    }

    private void checkTvShowInWatchList(){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(viewModel.getTvShowFromWatchList(String.valueOf(tvShow.getId()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mTvShow -> {
                    isTvShowAvailableInWatchList = true;
                    binding.imageWatchList.setImageResource(R.drawable.ic_added);
                    //binding.imageWatchList.setClickable(false);
                    compositeDisposable.dispose();
                }));
    }

    private void getTVShowDetails() {
        binding.setIsLoading(true);
        String tvShowID = String.valueOf(tvShow.getId());

        viewModel.getTVShowDetails(tvShowID).observe(this, tvShowDetailsResponse -> {
            binding.setIsLoading(false);
            if (tvShowDetailsResponse.getTvShowDetails() != null) {

                if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                    loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                }

                binding.setTvShowImageURL(tvShowDetailsResponse.getTvShowDetails().getImagePath());
                binding.imageTvShow.setVisibility(View.VISIBLE);

                binding.setDescription(
                        String.valueOf(
                                HtmlCompat.fromHtml(
                                        tvShowDetailsResponse.getTvShowDetails().getDescription(),
                                        HtmlCompat.FROM_HTML_MODE_LEGACY
                                )
                        )
                );
                binding.textDescription.setVisibility(View.VISIBLE);
                binding.textReadMore.setVisibility(View.VISIBLE);

                binding.textReadMore.setOnClickListener(v -> {
                    if (binding.textReadMore.getText().toString().equals("Read More")) {
                        binding.textDescription.setMaxLines(Integer.MAX_VALUE);
                        binding.textDescription.setEllipsize(null);
                        binding.textReadMore.setText(R.string.read_less);
                    } else {
                        binding.textDescription.setMaxLines(4);
                        binding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                        binding.textReadMore.setText(R.string.read_more);
                    }
                });

                binding.setRating(
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                Double.parseDouble(tvShowDetailsResponse.getTvShowDetails().getRating())
                        )
                );

                if (tvShowDetailsResponse.getTvShowDetails().getGenres() != null) {
                    binding.setGenre(tvShowDetailsResponse.getTvShowDetails().getGenres()[0]);
                } else {
                    binding.setGenre("N/A");
                }

                binding.setRuntime(tvShowDetailsResponse.getTvShowDetails().getRuntime() + " Min");
                binding.viewDivider1.setVisibility(View.VISIBLE);
                binding.layoutMisc.setVisibility(View.VISIBLE);
                binding.viewDivider2.setVisibility(View.VISIBLE);

                binding.buttonWebSite.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(tvShowDetailsResponse.getTvShowDetails().getUrl()));
                    startActivity(intent);
                });

                binding.buttonWebSite.setVisibility(View.VISIBLE);
                binding.buttonEpisodes.setVisibility(View.VISIBLE);

                binding.buttonEpisodes.setOnClickListener(v -> {
                    if (episodesBottomSheetDialog == null) {

                        episodesBottomSheetDialog = new BottomSheetDialog(TVShowDetailsActivity.this);

                        layoutEpisodesBinding = DataBindingUtil.inflate(
                                LayoutInflater.from(TVShowDetailsActivity.this),
                                R.layout.layout_episodes_bottom_sheet,
                                findViewById(R.id.episodesContainer),
                                false
                        );

                        episodesBottomSheetDialog.setContentView(layoutEpisodesBinding.getRoot());
                        layoutEpisodesBinding.episodesRecyclerView.setAdapter(
                                new EpisodesAdapter(tvShowDetailsResponse.getTvShowDetails().getEpisodes())
                        );

                        layoutEpisodesBinding.textTitle.setText(
                                String.format("Episodes | %s", tvShow.getName())
                        );

                        layoutEpisodesBinding.imageClose.setOnClickListener(view -> {
                            episodesBottomSheetDialog.dismiss();
                        });

                        //------ Optional Design ----------
                        FrameLayout frameLayout = episodesBottomSheetDialog.findViewById(
                                com.google.android.material.R.id.design_bottom_sheet
                        );

                        if (frameLayout != null) {
                            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                            bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                        //------ Optional Design ----------
                        episodesBottomSheetDialog.show();
                    }
                });

                binding.imageWatchList.setOnClickListener(view -> {
                    CompositeDisposable compositeDisposable = new CompositeDisposable();

                    if(isTvShowAvailableInWatchList){
                        compositeDisposable.add(viewModel.removeTvShowFromWatchList(tvShow)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(()-> {
                                    TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                    isTvShowAvailableInWatchList = false;
                                    binding.imageWatchList.setImageResource(R.drawable.ic_watchlist);
                                    Toast.makeText(this, "Removed from watchlist", Toast.LENGTH_SHORT).show();
                                    compositeDisposable.dispose();
                                }));
                    }else {
                        compositeDisposable.add(viewModel.addToWatchList(tvShow)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                    binding.imageWatchList.setImageResource(R.drawable.ic_added);
                                    //binding.imageWatchList.setClickable(false);
                                    Toast.makeText(this, "Added to watchlist", Toast.LENGTH_SHORT).show();
                                    compositeDisposable.dispose();
                                }));
                    }
                });

                binding.imageWatchList.setVisibility(View.VISIBLE);

                loadBasicTvShowDetails();
            }
        });
    }

    private void loadImageSlider(String[] sliderImages) {
        binding.sliderViewPager.setOffscreenPageLimit(1);
        binding.sliderViewPager.setAdapter(new ImageSliderAdapter(sliderImages));
        binding.sliderViewPager.setVisibility(View.VISIBLE);
        binding.viewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicators(sliderImages.length);

        binding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });
    }

    private void setupSliderIndicators(int count) {
        ImageView[] indicators = new ImageView[count];

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(), R.drawable.background_slider_indicator_inactive
            ));
            indicators[i].setLayoutParams(params);
            binding.layoutSliderIndicators.addView(indicators[i]);
        }
        binding.layoutSliderIndicators.setVisibility(View.VISIBLE);

        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int position) {
        int childCount = binding.layoutSliderIndicators.getChildCount();

        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) binding.layoutSliderIndicators.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_inactive)
                );
            }
        }
    }

    private void loadBasicTvShowDetails() {
        binding.setTvShowName(tvShow.getName());

        binding.setNetworkCountry(
                tvShow.getNetwork() + " (" + tvShow.getCountry() + ")");

        binding.setStatus(tvShow.getStatus());
        binding.setStartedDate(tvShow.getStartDate());

        binding.textName.setVisibility(View.VISIBLE);
        binding.textNetworkCountry.setVisibility(View.VISIBLE);
        binding.textStarted.setVisibility(View.VISIBLE);
        binding.textStatus.setVisibility(View.VISIBLE);
    }
}