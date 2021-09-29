package com.neppplus.finalproject_20210910

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.neppplus.finalproject_20210910.utils.FontChanger
import com.neppplus.finalproject_20210910.web.ServerAPI
import com.neppplus.finalproject_20210910.web.ServerAPIService
import retrofit2.Retrofit

abstract class BaseActivity : AppCompatActivity() {

    lateinit var mContext : Context

//    모든화면에 레트로핏 / API서비스를 미리 만들어서 물려주자.
//    각 화면에서는 apiService 변수를 불러내서 사용만 하면 되도록.
    private lateinit var retrofit : Retrofit
    lateinit var apiService : ServerAPIService

//    액션바에 있는 UI요소들을 상속시켜주자.
    lateinit var profileImg : ImageView
    lateinit var titleTxt : TextView
    lateinit var addBtn : ImageView
    lateinit var companyLogoImg : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this

        retrofit = ServerAPI.getRetrofit(mContext)
        apiService = retrofit.create(ServerAPIService::class.java)

        supportActionBar?.let {
            setCustomActionBar()
        }

    }

//    만들어지고 (onCreate) -> 화면에 나가기는 전 (onResume)

    override fun onStart() {
        super.onStart()

//        (액티비티의 최상위 태그) rootView 받아와서 폰트변경기에 의뢰.
        val rootView = window.decorView.rootView
        FontChanger.setGlobalFont(mContext, rootView)

    }


    abstract fun setupEvents()
    abstract fun setValues()

    fun setCustomActionBar() {
        val defActionBar = supportActionBar!!

        defActionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        defActionBar.setCustomView(R.layout.my_custom_action_bar)

        val toolBar = defActionBar.customView.parent as Toolbar
        toolBar.setContentInsetsAbsolute(0,0)

        profileImg = defActionBar.customView.findViewById(R.id.profileImg)
        titleTxt = defActionBar.customView.findViewById(R.id.titleTxt)
        addBtn = defActionBar.customView.findViewById(R.id.addBtn)
        companyLogoImg = defActionBar.customView.findViewById(R.id.companyLogoImg)

    }

}