package com.neppplus.finalproject_20210910

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
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

//        프로필사진 누르면 => 프사 변경의 의미로 활용. => 갤러리로 프사 선택하러 진입.
//        안드로이드가 제공하는 갤러리 화면 활용. Intent (4) 추가 항목
//        어떤사진? 결과를 얻기 위해 화면 이동. Intent (3) 활용.

        binding.profileImg.setOnClickListener {

//            갤러리를 개발자가 이용 : 허락 받아야 볼 수 있다. => 권한 세팅 필요.
//            TedPermission 라이브러리.

            val permissionListener = object : PermissionListener {
                override fun onPermissionGranted() {
//                    권한이 OK 일때.
//                    갤러리로 사진을 가지러 이동. (추가 작업)
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
//                    (최종으로) 권한 거절되었을때. => 토스트로 안내만.
                    Toast.makeText(mContext, "권한이 거부되어 갤러리에 접근이 불가능합니다.", Toast.LENGTH_SHORT).show()
                }

            }

//            실제로 권한 체크.
//            1)  Manifest에 권한 등록
//            2) 실제로 라이브러리로 질문.

            TedPermission.create()
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .setDeniedMessage("[설정] > [권한]에서 갤러리 권한을 열어주세요.")
                .check()


        }


        binding.myPlacesLayout.setOnClickListener { 
            val myIntent = Intent(mContext, ViewMyPlaceListActivity::class.java)
            startActivity(myIntent)
        }

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


//        로그인한 사용자는 프로필 사진 경로(URL - String)도 들고 있다. => profileImg에 적용. (Glide)

        Glide.with(mContext).load(GlobalData.loginUser!!.profileImgURL).into(binding.profileImg)

    }

}