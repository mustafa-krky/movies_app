package com.kmobile.tvshowsapp.responses;

import com.google.gson.annotations.SerializedName;
import com.kmobile.tvshowsapp.models.TVShowDetails;

public class TVShowDetailsResponse {

    @SerializedName("tvShow")
    private TVShowDetails tvShowDetails;

    public TVShowDetails getTvShowDetails() {
        return tvShowDetails;
    }
}