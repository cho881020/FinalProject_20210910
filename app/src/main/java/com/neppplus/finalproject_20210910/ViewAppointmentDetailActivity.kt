package com.neppplus.finalproject_20210910

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.MarkerIcons
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
        setNaverMap()

//        4) 응용 1 - 친구목록 => 레이아웃에 xml inflate해서 하나씩 addView

        val inflater = LayoutInflater.from(mContext)

        for (friend  in  mAppointmentData.invitedFriendList) {

            val friendView = inflater.inflate(R.layout.invited_friends_list_item, null)

            val friendProfileImg = friendView.findViewById<ImageView>(R.id.friendProfileImg)
            val nicknameTxt = friendView.findViewById<TextView>(R.id.nicknameTxt)
            val statusTxt = friendView.findViewById<TextView>(R.id.statusTxt)


            Glide.with(mContext).load(friend.profileImgURL).into(friendProfileImg)
            nicknameTxt.text  =  friend.nickName


            binding.invitedFriendsLayout.addView(friendView)

        }

//        5) 응용 2 - 출발지 좌표도 지도에 설정.  (setNaverMap 함수)


    }

    fun setNaverMap() {
//        지도 관련 코드.

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naverMapFrag) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naverMapFrag, it).commit()
            }

        mapFragment.getMapAsync {
            val naverMap = it


//          - 마커를 하나 생성 => 도착지 좌표에 찍어주기

            val dest = LatLng(mAppointmentData.latitude, mAppointmentData.longitude)

            val marker = Marker()
            marker.position = dest
            marker.icon = MarkerIcons.BLACK
            marker.iconTintColor = Color.RED
            marker.map = naverMap

//          - 카메라 이동 => 도착지 좌표로 카메라 이동.
            val cameraUpdate = CameraUpdate.scrollTo(dest)
            naverMap.moveCamera(cameraUpdate)


        //          - 출발지 마커 찍기

            val startLatLng = LatLng(mAppointmentData.startLatitude, mAppointmentData.startLongitude)

            val startMarker = Marker()
            startMarker.position = startLatLng
            startMarker.map = naverMap

        //          - 출발지 / 도착지 일직선 PathOverlay 그어주기

            val path = PathOverlay()

            val points = ArrayList<LatLng>()
            points.add(startLatLng)

//            둘 사이에서 -> 실제 경유지들을 추가.

            points.add(dest)

            path.coords = points
            path.map = naverMap

//          - 대중교통 API 활용 => 1. 도착 예상시간 표시 (infoWindow)
        //          2. 실제 경유지로 PathOverlay 그어주기. => 도전과제. (마지막시간에 따로 풀이)

        }

    }


}