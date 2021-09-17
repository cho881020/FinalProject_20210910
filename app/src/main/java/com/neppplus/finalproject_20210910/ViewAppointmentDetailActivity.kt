package com.neppplus.finalproject_20210910

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.neppplus.finalproject_20210910.databinding.ActivityViewAppointmentDetailBinding
import com.neppplus.finalproject_20210910.datas.AppointmentData
import java.text.SimpleDateFormat

class ViewAppointmentDetailActivity : BaseActivity() {

    lateinit var binding: ActivityViewAppointmentDetailBinding

    lateinit var mAppointmentData : AppointmentData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_appointment_detail)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        titleTxt.text = "약속 상세 확인"

        mAppointmentData = intent.getSerializableExtra("appointment") as AppointmentData

        binding.titleTxt.text = mAppointmentData.title
        binding.placeTxt.text = mAppointmentData.placeName

//        1) 참여인원 수 => "(참여인원 : ?명)"  => 본인 빼고 초대된 사람들 수만.
        binding.invitedFriendsCountTxt.text = "(참여인원 : ${mAppointmentData.invitedFriendList.size}명)"

//        2) 약속 시간 (Date) => "9/3 오후 6:06"  (String) 양식으로 가공 // 15:20 까지 진행
//         simpledateformat 활용.

        val sdf= SimpleDateFormat("M/d a h:mm")
        binding.timeTxt.text =  sdf.format(mAppointmentData.datetime)

//        3) 도착지 좌표 지도에 설정
//          - 마커를 하나 생성 => 좌표에 찍어주기
//          - 카메라 이동 => 도착지 좌표로 카메라 이동.

//        4) 응용 1 - 친구목록 => 레이아웃에 xml inflate해서 하나씩 addView
//        5) 응용 2 - 출발지 좌표도 지도에 설정.
//          - 마커 찍기 3)과 동일
//          - 출발지 / 도착지 일직선 PathOverlay 그어주기
//          - 대중교통 API 활용 => 1. 도착 예상시간 표시 (infoWindow),  2. 실제 경유지로 PathOverlay 그어주기.


    }

}