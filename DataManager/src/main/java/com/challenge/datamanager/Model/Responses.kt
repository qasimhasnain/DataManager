package com.challenge.datamanager.Model

import com.google.gson.annotations.SerializedName

data class MultiSearchResponse (
  @SerializedName ("page") val page : Int,
  @SerializedName("total_results") val total_results : Int,
  @SerializedName ("total_pages") val total_pages : Int,
  @SerializedName ("results") val results : List<MediaItem>?
 )

data class MediaItem(
    @SerializedName("id") val id : Int,
    @SerializedName("vote_average") val vote_average : Double,
    @SerializedName("vote_count") val vote_count : Int,
    @SerializedName("media_type") val media_type : String,
    @SerializedName("poster_path") val poster_path : String,
    @SerializedName("popularity") val popularity : Double,
    @SerializedName("genre_ids") val genre_ids : List<Int>,
//    @SerializedName("genre_ids") val genre_ids : String,
    @SerializedName("backdrop_path") val backdrop_path : String,
    @SerializedName("original_language") val original_language : String,
    @SerializedName("overview") val overview : String,


    @SerializedName("video") val video : Boolean? = false,
    @SerializedName("adult") val adult : Boolean? = false,
    @SerializedName("title") val title : String? = "",
    @SerializedName("original_title") val original_title : String? = "",
    @SerializedName("release_date") val release_date : String? = "",

    @SerializedName("first_air_date") val first_air_date : String? = "",
    @SerializedName("origin_country") val origin_country : List<String>? = null,
//    @SerializedName("origin_country") val origin_country : String? = null,
    @SerializedName("name") val name : String? = "",
    @SerializedName("original_name") val original_name : String? = "",

    @SerializedName("profile_path") val profile_path : String? = "",
    @SerializedName("known_for") val known_for : List<MediaItem>? = null
//    @SerializedName("known_for") val known_for : String? = null
)