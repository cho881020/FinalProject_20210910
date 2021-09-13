package com.neppplus.finalproject_20210910.utils

import android.content.Context

class ContextUtil {

    companion object {

//        토큰 등을 저장하는 메모장의 파일 명

        private val prefName = "FinalProjectPref"

//        여러 항목 저장 가능. => 항목명들도 변수로 저장.

        //        토큰
        val TOKEN = "TOKEN"

//        토큰 저장 / 조회

        fun setToken(context: Context, token: String) {
            val pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            pref.edit().putString(TOKEN, token).apply()
        }


    }

}