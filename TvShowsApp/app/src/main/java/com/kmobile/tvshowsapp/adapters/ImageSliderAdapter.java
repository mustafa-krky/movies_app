package com.kmobile.tvshowsapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kmobile.tvshowsapp.R;
import com.kmobile.tvshowsapp.databinding.ItemContainerSliderImageBinding;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder>{

    private String[] sliderImages;
    private LayoutInflater layoutInflater;

    public ImageSliderAdapter(String[] sliderImages) {
        this.sliderImages = sliderImages;
    }

    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(layoutInflater == null){
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemContainerSliderImageBinding binding = DataBindingUtil.inflate(
                layoutInflater, R.layout.item_container_slider_image, parent, false
        );

        return new ImageSliderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder holder, int position) {
        holder.bindSliderImage(sliderImages[position]);
    }

    @Override
    public int getItemCount() {
        return sliderImages.length;
    }

    class ImageSliderViewHolder extends RecyclerView.ViewHolder {
        private ItemContainerSliderImageBinding binding;

        public ImageSliderViewHolder(ItemContainerSliderImageBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindSliderImage(String imageURL){
            binding.setImageURL(imageURL);
        }
    }
}