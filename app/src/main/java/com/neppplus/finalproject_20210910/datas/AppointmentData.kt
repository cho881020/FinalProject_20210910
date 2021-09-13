package com.neppplus.finalproject_20210910.datas

import com.google.gson.annotations.SerializedName

class AppointmentData(
    var id: Int,
    @SerializedName("user_id")
    var userId: Int,
    var title: String
) {
}