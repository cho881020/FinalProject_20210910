package com.neppplus.finalproject_20210910.web

import com.neppplus.finalproject_20210910.datas.BasicResponse
import retrofit2.Call
import retrofit2.http.*

interface ServerAPIService {

    @FormUrlEncoded
    @PUT("/user")
    fun putRequestSignUp(
        @Field("email") email: String,
        @Field("password") pw: String,
        @Field("nick_name") nickname: String) : Call<BasicResponse>

    @FormUrlEncoded
    @POST("/user")
    fun postRequestLogin(
        @Field("email") email: String,
        @Field("password") pw: String ) : Call<BasicResponse>

    @FormUrlEncoded
    @POST("/user/social")
    fun postRequestSocialLogin(
        @Field("provider") provider: String,
        @Field("uid") id: String,
        @Field("nick_name") name: String  ) : Call<BasicResponse>

    @FormUrlEncoded
    @POST("/appointment")
    fun postRequestAppointment(
        @Field("title")  title: String,
        @Field("datetime") datetime:String,
        @Field("place") placeName: String,
        @Field("latitude") lat: Double,
        @Field("longitude") lng: Double
    ) : Call<BasicResponse>


}