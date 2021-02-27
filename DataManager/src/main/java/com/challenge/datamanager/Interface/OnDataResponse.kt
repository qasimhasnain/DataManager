package com.challenge.datamanager.Interface

interface OnDataResponse {

    fun onSuccess(obj:Any?)
    fun onFailure(obj:Any?)
    fun onUpdateMultiSearch(obj:Any?){}

}