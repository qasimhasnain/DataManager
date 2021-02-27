package com.challenge.datamanager.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.challenge.datamanager.Model.MediaItem
import com.google.gson.GsonBuilder

val DATABASENAME = "media_database"
val MEDIA_TABLE = "media_data_table"
val COL_ID = "id"
val COL_VOTE_AVE = "vote_average"
val COL_VOTE_COUNT = "vote_count"
val COL_MEDIA_TYPE = "media_type"
val COL_POSTER = "poster_path"
val COL_POPULARITY = "popularity"
val COL_GENRE_IDS = "genre_ids"
val COL_BACKDROP = "backdrop_path"
val COL_LANGUAGE = "original_language"
val COL_OVERVIEW = "overview"
val COL_VIDEO = "video"
val COL_ADULT = "adult"
val COL_TITLE = "title"
val COL_ORG_TITLE = "original_title"
val COL_RELEASE_DATE = "release_date"
val COL_FIRST_AIR_DATE = "first_air_date"
val COL_ORG_COUNTRY = "origin_country"
val COL_NAME = "name"
val COL_ORG_NAME = "original_name"
val COL_PROFILE_PATH = "profile_path"
val COL_KNOWN_FOR = "known_for"
val COL_PAGE_NO = "page_no"

class DatabaseHelper (context : Context) : SQLiteOpenHelper(context, DATABASENAME, null,1){
    override fun onCreate(p0: SQLiteDatabase?) {
        deletePreviousTable(p0!!)
        createNewTable(p0!!)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
    }

    fun deletePreviousTable(p0: SQLiteDatabase?){
        val deleteTable = "DROP TABLE IF EXISTS $MEDIA_TABLE"
        p0?.execSQL(deleteTable)
    }

    fun createNewTable(p0 : SQLiteDatabase){
        val createTable = "CREATE TABLE IF NOT EXISTS $MEDIA_TABLE ("+ COL_ID +" integer primary key, "+
                COL_MEDIA_TYPE +" text, "+ COL_ADULT+" integer, "+
                COL_VOTE_COUNT +" integer, "+ COL_VOTE_AVE+" real, "+
                COL_POSTER +" text, "+ COL_POPULARITY+" real, "+
                COL_GENRE_IDS +" text, "+ COL_LANGUAGE+" text, "+
                COL_OVERVIEW +" text, "+ COL_VIDEO+" integer, "+
                COL_TITLE +" text, "+ COL_ORG_TITLE+" text, "+
                COL_RELEASE_DATE +" text, "+ COL_ORG_COUNTRY+" text, "+
                COL_NAME +" text, "+ COL_ORG_NAME+" text, "+
                COL_PROFILE_PATH +" text, "+ COL_KNOWN_FOR+" text, "+
                COL_BACKDROP+" text, "+ COL_FIRST_AIR_DATE+" text, " +
                COL_PAGE_NO + " text)"

        p0?.execSQL(createTable)
    }

    fun dropPreviousTable(){
        deletePreviousTable(this.writableDatabase)
    }

    fun createNewTable(){
        createNewTable(this.writableDatabase)
    }
    fun getAllMedia() : MutableList<MediaItem>{
        val list: MutableList<MediaItem> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $MEDIA_TABLE"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val genreIds : List<Int> = getGenreIds(result.getString(result.getColumnIndex(COL_GENRE_IDS)))
                val orgCountry : List<String> = getCountries(result.getString(result.getColumnIndex(COL_ORG_COUNTRY)))
                val isVideo : Boolean = result.getInt(result.getColumnIndex(COL_VIDEO)) > 0
                val isAdult : Boolean = result.getInt(result.getColumnIndex(COL_ADULT)) > 0
                val knownFor : List<MediaItem> = getMediaItems(result.getString(result.getColumnIndex(COL_KNOWN_FOR)))

                val mediaItem = MediaItem(result.getInt(result.getColumnIndex(COL_ID)),
                        result.getDouble(result.getColumnIndex(COL_VOTE_AVE)),
                        result.getInt(result.getColumnIndex(COL_VOTE_COUNT)),
                        result.getString(result.getColumnIndex(COL_MEDIA_TYPE)),
                        result.getString(result.getColumnIndex(COL_POSTER)),
                        result.getDouble(result.getColumnIndex(COL_POPULARITY)),
                        genreIds,
                        result.getString(result.getColumnIndex(COL_BACKDROP)),
                        result.getString(result.getColumnIndex(COL_LANGUAGE)),
                        result.getString(result.getColumnIndex(COL_OVERVIEW)),
                        isVideo,
                        isAdult,
                        result.getString(result.getColumnIndex(COL_TITLE)),
                        result.getString(result.getColumnIndex(COL_ORG_TITLE)),
                        result.getString(result.getColumnIndex(COL_RELEASE_DATE)),
                        result.getString(result.getColumnIndex(COL_FIRST_AIR_DATE)),
                        orgCountry,
                        result.getString(result.getColumnIndex(COL_NAME)),
                        result.getString(result.getColumnIndex(COL_ORG_NAME)),
                        result.getString(result.getColumnIndex(COL_PROFILE_PATH)),
                        knownFor
                )
                list.add(mediaItem)
            }
            while (result.moveToNext())
        }
        return list
    }

    fun getAllMediaForMediaType(mediaType : String) : MutableList<MediaItem>{
        val list: MutableList<MediaItem> = ArrayList()
        val db = this.readableDatabase
//        val query = "Select * from $MEDIA_TABLE Where $COL_MEDIA_TYPE "
//        val result = db.rawQuery(query, null)
        val selection = "$COL_MEDIA_TYPE = ?"
        val selectionArgs = arrayOf(mediaType)

        val result = db.query(
                MEDIA_TABLE,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        )



        if (result.moveToFirst()) {
            do {
                val genreIds : List<Int> = getGenreIds(result.getString(result.getColumnIndex(COL_GENRE_IDS)))
                val orgCountry : List<String> = getCountries(result.getString(result.getColumnIndex(COL_ORG_COUNTRY)))
                val isVideo : Boolean = result.getInt(result.getColumnIndex(COL_VIDEO)) > 0
                val isAdult : Boolean = result.getInt(result.getColumnIndex(COL_ADULT)) > 0
                val knownFor : List<MediaItem> = getMediaItems(result.getString(result.getColumnIndex(COL_KNOWN_FOR)))

                val mediaItem = MediaItem(result.getInt(result.getColumnIndex(COL_ID)),
                    result.getDouble(result.getColumnIndex(COL_VOTE_AVE)),
                    result.getInt(result.getColumnIndex(COL_VOTE_COUNT)),
                    result.getString(result.getColumnIndex(COL_MEDIA_TYPE)),
                    result.getString(result.getColumnIndex(COL_POSTER)),
                    result.getDouble(result.getColumnIndex(COL_POPULARITY)),
                    genreIds,
                    result.getString(result.getColumnIndex(COL_BACKDROP)),
                    result.getString(result.getColumnIndex(COL_LANGUAGE)),
                    result.getString(result.getColumnIndex(COL_OVERVIEW)),
                    isVideo,
                    isAdult,
                    result.getString(result.getColumnIndex(COL_TITLE)),
                    result.getString(result.getColumnIndex(COL_ORG_TITLE)),
                    result.getString(result.getColumnIndex(COL_RELEASE_DATE)),
                    result.getString(result.getColumnIndex(COL_FIRST_AIR_DATE)),
                    orgCountry,
                    result.getString(result.getColumnIndex(COL_NAME)),
                    result.getString(result.getColumnIndex(COL_ORG_NAME)),
                    result.getString(result.getColumnIndex(COL_PROFILE_PATH)),
                    knownFor
                )
                list.add(mediaItem)
            }
            while (result.moveToNext())
        }
        return list
    }

    fun getAllMediaForMediaType(pageNo: Int,mediaType : String) : MutableList<MediaItem>{
        val list: MutableList<MediaItem> = ArrayList()
        val db = this.readableDatabase
//        val query = "Select * from $MEDIA_TABLE Where $COL_MEDIA_TYPE "
//        val result = db.rawQuery(query, null)
        val selection = "$COL_MEDIA_TYPE = ? AND $COL_PAGE_NO = ?"
        val selectionArgs = arrayOf(mediaType, pageNo.toString())

        val result = db.query(
                MEDIA_TABLE,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        )



        if (result.moveToFirst()) {
            do {
                val genreIds : List<Int> = getGenreIds(result.getString(result.getColumnIndex(COL_GENRE_IDS)))
                val orgCountry : List<String> = getCountries(result.getString(result.getColumnIndex(COL_ORG_COUNTRY)))
                val isVideo : Boolean = result.getInt(result.getColumnIndex(COL_VIDEO)) > 0
                val isAdult : Boolean = result.getInt(result.getColumnIndex(COL_ADULT)) > 0
                val knownFor : List<MediaItem> = getMediaItems(result.getString(result.getColumnIndex(COL_KNOWN_FOR)))

                val mediaItem = MediaItem(result.getInt(result.getColumnIndex(COL_ID)),
                        result.getDouble(result.getColumnIndex(COL_VOTE_AVE)),
                        result.getInt(result.getColumnIndex(COL_VOTE_COUNT)),
                        result.getString(result.getColumnIndex(COL_MEDIA_TYPE)),
                        result.getString(result.getColumnIndex(COL_POSTER)),
                        result.getDouble(result.getColumnIndex(COL_POPULARITY)),
                        genreIds,
                        result.getString(result.getColumnIndex(COL_BACKDROP)),
                        result.getString(result.getColumnIndex(COL_LANGUAGE)),
                        result.getString(result.getColumnIndex(COL_OVERVIEW)),
                        isVideo,
                        isAdult,
                        result.getString(result.getColumnIndex(COL_TITLE)),
                        result.getString(result.getColumnIndex(COL_ORG_TITLE)),
                        result.getString(result.getColumnIndex(COL_RELEASE_DATE)),
                        result.getString(result.getColumnIndex(COL_FIRST_AIR_DATE)),
                        orgCountry,
                        result.getString(result.getColumnIndex(COL_NAME)),
                        result.getString(result.getColumnIndex(COL_ORG_NAME)),
                        result.getString(result.getColumnIndex(COL_PROFILE_PATH)),
                        knownFor
                )
                list.add(mediaItem)
            }
            while (result.moveToNext())
        }
        return list
    }


    fun saveMediaItem(pageNo : Int, list : List<MediaItem>){

        for (item in list){
            val db = this.writableDatabase
            val values = ContentValues().apply {
                put(COL_ID, item.id)
                put(COL_NAME, item.name)
                put(COL_PROFILE_PATH, item.profile_path)
                put(COL_ORG_NAME, item.original_name)
                put(COL_ORG_COUNTRY, getStringFromStringList(item.origin_country))
                put(COL_FIRST_AIR_DATE, item.first_air_date)
                put(COL_RELEASE_DATE, item.release_date)
                put(COL_ORG_TITLE, item.original_title)
                put(COL_TITLE, item.title)
                put(COL_ADULT, item.adult)
                put(COL_VIDEO, item.video)
                put(COL_OVERVIEW, item.overview)
                put(COL_KNOWN_FOR, getStringFromList(item.known_for))
                put(COL_BACKDROP, item.backdrop_path)
                put(COL_GENRE_IDS, getStringFromIntList(item.genre_ids))
                put(COL_LANGUAGE, item.original_language)
                put(COL_POPULARITY, item.popularity)
                put(COL_POSTER, item.poster_path)
                put(COL_MEDIA_TYPE, item.media_type)
                put(COL_VOTE_COUNT, item.vote_count)
                put(COL_VOTE_AVE, item.vote_average)
                put(COL_PAGE_NO, pageNo)
            }

            val newRowId = db?.insert(MEDIA_TABLE, null, values)
        }
    }

    private fun getGenreIds(jsonString : String): List<Int>{
//        return  jsonString.map { it.toInt() }
        return  emptyList()
    }

    private fun getCountries(jsonString : String): List<String>{
//        return jsonString.map { it.toString() }
        return emptyList()
    }

    private fun getMediaItems(jsonString : String): List<MediaItem>{
//        val gson = GsonBuilder().create()
//        return gson.fromJson(jsonString,Array<MediaItem>::class.java).toList()
        return emptyList()
    }

    private fun getStringFromList(list : List<MediaItem>?) : String {
        return " "
    }

    private fun getStringFromIntList(list : List<Int>) : String{
//        return list.joinToString()
        return " "
    }

    private fun getStringFromStringList(list : List<String>?) : String{
//        return list.joinToString()
        return " "
    }

}