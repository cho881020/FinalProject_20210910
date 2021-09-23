package com.neppplus.finalproject_20210910.datas

import android.util.Log
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class AppointmentData(
    var id: Int,
    @SerializedName("user_id")
    var userId: Int,
    var title: String,
    var datetime: Date, // 일단 String -> 파싱 기능 수정 => Date형태로 받자. (Calendar와 엮어서 사용)
    @SerializedName("start_place")
    var startPlace: String,
    @SerializedName("start_latitude")
    var startLatitude: Double,
    @SerializedName("start_longitude")
    var startLongitude: Double,
    @SerializedName("place")
    var placeName: String,
    var latitude: Double,
    var longitude: Double,
    @SerializedName("created_at")
    var createdAt: Date,
    var user: UserData,
    @SerializedName("invited_friends")
    var invitedFriendList: List<UserData>
) : Serializable {

//    함수 추가. => 현재 시간 ~ 약속시간 남은 시간에 따라 다른 문구를 리턴.

    fun getFormattedDateTime() :  String {

//        현재 시간:
        val now = Calendar.getInstance() // 현재 일시

//        약속시간 (utc) => 폰 설정 타임존 변환 - 현재시간 (폰 설정 타임존) : 몇시간?


        val dateTimeToTimeZone = this.datetime.time + now.timeZone.rawOffset

        val diff = dateTimeToTimeZone - now.timeInMillis

//        몇시간 차이인가?
        val diffHour = diff / 1000 / 60 / 60

        Log.d("시차", diffHour.toString())

        if (diffHour < 1) {

//            몇분 남음
            val diffMinute = diff / 1000 / 60
            return "${diffMinute}분 남음"

        }
        else if (diffHour < 5) {
            return "${diffHour}시간 남음"
        }
        else {
            val sdf = SimpleDateFormat("M/d a h:mm")
            return sdf.format(dateTimeToTimeZone)
        }


    }


}