package com.challenge.datamanager.API

import com.challenge.datamanager.Model.MultiSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MediaApi {

    @GET("search/multi")
    fun getMultiSearch(@Query ("api_key" ) api_key : String,
    @Query ("query") query : String,
    @Query ("language") language : String?,
    @Query ("page") page : Int?,
    @Query ("include_adult") include_adult : Boolean?,
    @Query ("region") region : Int?) : Call <MultiSearchResponse>
}