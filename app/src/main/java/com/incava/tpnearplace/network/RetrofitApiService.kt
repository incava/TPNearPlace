package com.incava.tpnearplace.network

import com.incava.tpnearplace.model.KakaoSearchPlaceResponse
import com.incava.tpnearplace.model.NidUserInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitApiService {
    @GET("/v1/nid/me")
    fun getNaveridUserInfo(@Header("Authorization") authorization : String) : Call<NidUserInfoResponse>

    //Kakao의 키워드 검색 API
    @Headers("Authorization: KakaoAK d557272f29e480982a4f5ed3ff9edcd0")
    @GET("v2/local/search/keyword.json")
    fun searchPlace(@Query("query") query: String, @Query("y") latitude:String,@Query("x") longitude : String) : Call<KakaoSearchPlaceResponse>


}