package com.neppplus.finalproject_20210910

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.neppplus.finalproject_20210910.databinding.ActivityMySettingBinding
import com.neppplus.finalproject_20210910.datas.BasicResponse
import com.neppplus.finalproject_20210910.utils.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MySettingActivity : BaseActivity() {

    lateinit var binding: ActivityMySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_setting)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.editNicknameLayout.setOnClickListener {

//            응용문제 => AlertDialog로 닉네임을 입력받자.
//             EditText를 사용할 수 있는 방법?

//            PATCH - /user => field : nickname으로 보내서 닉변.

            val customView = LayoutInflater.from(mContext).inflate(R.layout.my_custom_alert_nickname, null)
            val alert = AlertDialog.Builder(mContext)
            alert.setTitle("닉네임 변경")
            alert.setView(customView)
            alert.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->
                val nicknameEdt = customView.findViewById<EditText>(R.id.nicknameEdt)

                apiService.patchRequestMyInfo("nickname", nicknameEdt.text.toString()).enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if (response.isSuccessful) {
                            val basicResponse = response.body()!!
                            GlobalData.loginUser = basicResponse.data.user

                            setUserInfo()
                        }
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                    }

                })

            })
            alert.setNegativeButton("취소", null)
            alert.show()

        }

        binding.readyTimeLayout.setOnClickListener {

//            응용문제 => AlertDialog로 준비시간을 입력받자.
//             EditText를 사용할 수 있는 방법? 구글링

            val customView = LayoutInflater.from(mContext).inflate(R.layout.my_custom_alert_edt, null)

            val alert = AlertDialog.Builder(mContext)

            alert.setTitle("준비 시간 설정")
//            커스텀뷰를 가져와서, 얼럿의 View로 설정.
            alert.setView(customView)
            alert.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->

                val minuteEdt = customView.findViewById<EditText>(R.id.minuteEdt)

//                Toast.makeText(mContext, "${minuteEdt.text.toString()}", Toast.LENGTH_SHORT).show()

                apiService.patchRequestMyInfo("ready_minute", minuteEdt.text.toString()).enqueue(object : Callback<BasicResponse> {
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if (response.isSuccessful) {

//                            내 수정된 정보 파싱. => 로그인한 사용자의 정보로 갱신.
                            val basicResponse = response.body()!!

                            GlobalData.loginUser = basicResponse.data.user

                            setUserInfo()


                        }
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                    }

                })


            })
            alert.setNegativeButton("취소", null)
            alert.show()


        }

    }

    override fun setValues() {

        titleTxt.text = "내 정보 설정"

        setUserInfo()
    }

    fun setUserInfo() {
        binding.nicknameTxt.text = GlobalData.loginUser!!.nickName

//       로그인한사람의 준비시간이 1시간 이상 or 아니냐
        if (GlobalData.loginUser!!.readyMinute >= 60) {
            val hour = GlobalData.loginUser!!.readyMinute / 60
            val minute = GlobalData.loginUser!!.readyMinute % 60

            binding.readyTimeTxt.text = "${hour}시간 ${minute}분"
        }
        else {
            binding.readyTimeTxt.text = "${GlobalData.loginUser!!.readyMinute}분"
        }

//        페북 / 카톡 / 일반 이냐 에 따라 이미지를 다르게  처리.

        when(GlobalData.loginUser!!.provider) {
            "facebook" -> binding.socialLoginImg.setImageResource(R.drawable.facebook_login_icon)
            "kakao" -> binding.socialLoginImg.setImageResource(R.drawable.kakao_login_icon)
            else -> binding.socialLoginImg.visibility = View.GONE
        }

//        일반로그인 (default) 는 비번 변경 UI 표시.
        when (GlobalData.loginUser!!.provider) {
            "default" -> binding.passwordLayout.visibility = View.VISIBLE
            else -> binding.passwordLayout.visibility = View.GONE
        }


    }

}