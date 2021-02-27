package com.challenge.datamanager

import android.content.Context
import android.os.Build
import android.util.Log
import com.challenge.datamanager.API.MediaApi
import com.challenge.datamanager.Database.DatabaseHelper
import com.challenge.datamanager.Exceptions.NoInternet
import com.challenge.datamanager.Interface.OnDataResponse
import com.challenge.datamanager.Model.DataItem
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

     private lateinit var onResponse: OnDataResponse
     private var totalItems : Int = 0
     private var pageNumber : Int = 0
     private var totalPages : Int = 0
     private lateinit var sections : ArrayList<String>
     private lateinit var mediaItems : ArrayList<MediaItem>
     private lateinit var apiKey : String
     private lateinit var allItems : ArrayList<MediaItem>
     private lateinit var allData : ArrayList<DataItem>
     private lateinit var context: Context
     private lateinit var dbHelper : DatabaseHelper

     private fun moreAvailable(): Boolean{
         return if (!this::allItems.isInitialized || allItems.isNullOrEmpty()) true
         else allItems.size < totalItems
     }
     fun setKey(context: Context,key : String){
         apiKey = key
         this.context = context
         dbHelper = DatabaseHelper(context)
     }

     @Throws(NoInternet::class)
     fun getMultiSearchResults( query: String, page : Int, onDataResponse: OnDataResponse) {
         onResponse = onDataResponse
         if(Utils.getInstance().isConnectedToInternet(context)){
             if (moreAvailable()){
                 var dataService = DataService()
                 var mediaApi : MediaApi = dataService.getMediaService()
                 val lang : String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                     context.resources.configuration.locales[0].language
                 } else {
                     context.resources.configuration.locale.toString()
                 }
                 var call : Call<MultiSearchResponse> = mediaApi.getMultiSearch(apiKey, query, lang, page, false, null)
                 call.enqueue(object : Callback<MultiSearchResponse> {
                     override fun onFailure(call: Call<MultiSearchResponse>, t: Throwable) {
                         onResponse.onFailure(t.localizedMessage)
                     }

                     override fun onResponse(
                             call: Call<MultiSearchResponse>,
                             response: Response<MultiSearchResponse>
                     ) {
                         Log.e("HomeScreen","onResponse::  "+response.isSuccessful)
                         val code : Int = response.code()
                         when(response.isSuccessful){
                             true ->{ response?.body()?.let { it_outer ->
                                 pageNumber = it_outer.page
                                 totalItems = it_outer.total_results
                                 totalPages = it_outer.total_pages
                                 mediaItems = it_outer.results as ArrayList<MediaItem>

                                 if (pageNumber > 1) {
                                    allItems.addAll(mediaItems)
                                 }
                                 else {
                                     if (this@DataManager::allItems.isInitialized && !allItems.isNullOrEmpty())
                                         allItems.clear()
                                     else
                                         allItems = ArrayList()
                                     allItems.addAll(mediaItems)
                                 }

                                 getSections(mediaItems)
                                 sections.sortBy { it.toString() }

                                 onResponse.onSuccess(getDataList(sections))
                             }}
                             false ->{
                                 onResponse.onFailure(response.message())
                             }
                         }
                     }
                 })
             }else{
                 onResponse.onFailure("Items not available")
             }
         }else{
             throw NoInternet()
         }
     }

     @Throws(NoInternet::class)
     fun getMoreResultsForMediaType(query: String, onDataResponse: OnDataResponse) {
         onResponse = onDataResponse
             if(Utils.getInstance().isConnectedToInternet(context)){
                 if (moreAvailable()){
                     var call : Call<MultiSearchResponse> = getMediaApi().getMultiSearch(apiKey, query, getLanguage(), pageNumber + 1, false, null)
                     call.enqueue(object : Callback<MultiSearchResponse> {
                         override fun onFailure(call: Call<MultiSearchResponse>, t: Throwable) {
                             onResponse.onFailure(t.localizedMessage)
                         }
                         override fun onResponse(
                                 call: Call<MultiSearchResponse>,
                                 response: Response<MultiSearchResponse>
                         ) {
                             Log.e("DataManager","onResponse::  "+response.isSuccessful)
                             val code : Int = response.code()
                             when(response.isSuccessful){
                                 true ->{ response?.body()?.let { it_outer ->
                                     pageNumber = it_outer.page
                                     totalItems = it_outer.total_results
                                     totalPages = it_outer.total_pages
                                     mediaItems = it_outer.results as ArrayList<MediaItem>

                                     allItems.addAll(mediaItems)

                                     updateSections(mediaItems)
                                     onResponse.onSuccess(getAllDataList(sections))
                                 }}
                                 false ->{
                                     onResponse.onFailure(response.message())
                                 }
                             }
                         }
                     })
                 }else{
                     onResponse.onFailure("Items not available")
                 }
             }else{
                 throw NoInternet()
             }
//         }
     }


    private fun getSections(results : List<MediaItem>){
         if (!this::sections.isInitialized) sections = ArrayList<String>()
         else if (pageNumber == 1) sections.clear()
         for (item in results){
             if (!sections.contains(item.media_type)) {
                 sections.add(item.media_type)
             }
         }
     }

     private fun updateSections(results : List<MediaItem>) : Boolean{
         var updated : Boolean = false
         if (!this::sections.isInitialized) sections = ArrayList<String>()
         else if (pageNumber == 1) sections.clear()
         for (item in results){
             if (!sections.contains(item.media_type)) {
                 sections.add(item.media_type)
                 updated = true
             }
         }

         sections.sortBy { it.toString() }
         return updated
     }

    private fun getItemsForMediaType(mediaType : String) : ArrayList<MediaItem>{
         var list : ArrayList<MediaItem> = ArrayList()
         if(this::mediaItems.isInitialized && mediaItems.size > 0){
             for (item in mediaItems){
                 if (item.media_type.equals(mediaType, true))
                     list.add(item)
             }
         }
         return list
     }

     private fun getAllItemsForMediaType(mediaType : String) : ArrayList<MediaItem>{
         var list : ArrayList<MediaItem> = ArrayList()
         if(this::allItems.isInitialized && allItems.size > 0){
             for (item in allItems){
                 if (item.media_type.equals(mediaType, true))
                     list.add(item)
             }
         }
         return list
     }

     fun getItemForId(id : Int, onDataResponse: OnDataResponse){
         if(this::allItems.isInitialized  && allItems.size > 0){
             try {
                 val item = allItems.first { it.id == id }
                 onDataResponse.onSuccess(item)
             }catch (e : NoSuchElementException){
                 onDataResponse.onFailure("No Data Found!")
             }
         }else{
             onDataResponse.onFailure("No Data Found!")
         }
     }

     private fun getDataList(list : List<String>) : List<DataItem>{
         return list.map { DataItem(it, getItemsForMediaType(it)) }
     }

     private fun getAllDataList(list : List<String>) : List<DataItem>{
         return list.map { DataItem(it, getAllItemsForMediaType(it)) }
     }

     private fun getMediaApi() : MediaApi{
         var dataService = DataService()
         return dataService.getMediaService()
     }

     private fun getLanguage() : String{
         return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
             context.resources.configuration.locales[0].language
         } else {
             context.resources.configuration.locale.toString()
         }
     }
 }
