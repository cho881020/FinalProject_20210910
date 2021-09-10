package com.neppplus.finalproject_20210910

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "ac036c2d991f2044c19455528bb9e9ff")

    }

}