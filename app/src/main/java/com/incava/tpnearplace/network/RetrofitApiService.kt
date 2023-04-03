package com.incava.tpnearplace.network

import com.incava.tpnearplace.model.NidUserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface RetrofitApiService {


    @GET("/v1/nid/me")
    fun getNaveridUserInfo(@Header("Authorization") authorization : String) : Call<NidUserInfoResponse>

}