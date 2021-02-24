package com.challenge.datamanager

import android.util.Log
import com.challenge.datamanager.API.MediaApi
import com.challenge.datamanager.Interface.OnDataResponse
import com.challenge.datamanager.Model.MediaItem
import com.challenge.datamanager.Model.MultiSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

 class DataManager  private constructor() {
        companion object {
            @Volatile private var instance: DataManager? = null
            fun getInstance() =
                    instance ?: synchronized(this) {
                        instance ?: DataManager().also { instance = it }
                    }
        }

    lateinit var onResponse: OnDataResponse
    var totalItems : Int = 0
    var pageNumber : Int = 0
    var totalPages : Int = 0
    lateinit var sections : ArrayList<String>
    lateinit var mediaItems : ArrayList<MediaItem>

    fun getSearchResults( apiKey: String, query: String, page : Int, onDataResponse: OnDataResponse) {
        onResponse = onDataResponse
        var dataService : DataService = DataService()
        var mediaApi : MediaApi = dataService.getMediaService()
        var call : Call<MultiSearchResponse> = mediaApi.getMultiSearch(apiKey, query, null, page, false, null)
        call.enqueue(object : Callback<MultiSearchResponse> {
            override fun onFailure(call: Call<MultiSearchResponse>, t: Throwable) {
                Log.e("HomeScreen","onFailure::  "+t.localizedMessage)
                handleError()
            }

            override fun onResponse(
                call: Call<MultiSearchResponse>,
                response: Response<MultiSearchResponse>
            ) {
                Log.e("HomeScreen","onResponse::  "+response.isSuccessful)
                response?.body()?.let {
                    pageNumber = it.page
                    totalItems = it.total_results
                    totalPages = it.total_pages
                    if (pageNumber > 1)
                        it.results?.let { it1 -> mediaItems.addAll(it1) }
                    else
                        mediaItems = it.results as ArrayList<MediaItem>

                    getSections(it.results!!)
                    onResponse.onSuccess(sections)
                }
            }
        })
    }

     fun getSections(results : List<MediaItem>){
         if (!this::sections.isInitialized)
             sections = ArrayList<String>()
         for (item in results){
             if (!sections.contains(item.media_type))
                 sections.add(item.media_type)
         }
     }

     fun getItemsForMediaType(mediaType : String) : List<MediaItem>{
         var list : ArrayList<MediaItem> = ArrayList()
         if(mediaItems != null && mediaItems.size > 0){
             for (item in mediaItems){
                 if (item.media_type.equals(mediaType, true))
                     list.add(item)
             }
         }
         return list
     }

     fun getItemsForId(id : Int) : MediaItem?{
         if(this::mediaItems.isInitialized  && mediaItems.size > 0){
             for (item in mediaItems){
                 if (item.id == id)
                     return item
             }
         }
         return null
     }

    fun handleError(){
        Log.e("HomeScreen","handleError::  ")
    }
}
