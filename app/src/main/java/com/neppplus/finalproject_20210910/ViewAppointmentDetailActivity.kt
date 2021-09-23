package com.neppplus.finalproject_20210910

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.MarkerIcons
import com.neppplus.finalproject_20210910.databinding.ActivityViewAppointmentDetailBinding
import com.neppplus.finalproject_20210910.datas.AppointmentData
import com.neppplus.finalproject_20210910.datas.BasicResponse
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat

class ViewAppointmentDetailActivity : BaseActivity() {

    lateinit var binding: ActivityViewAppointmentDetailBinding

    lateinit var mAppointmentData : AppointmentData

//    버튼이 눌리면 => API 전송해달라고 표시 flag
    var needLocationSendServer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_appointment_detail)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.arrivalBtn.setOnClickListener {

//            서버에 위치를 보내야한다고 flag값을 true
            needLocationSendServer = true


//            내 위치를 파악. (현재 위치 위도/경도 추출)

//            위치를 받아오 될지 권한부터 물어보자.

            val pl = object : PermissionListener {
                override fun onPermissionGranted() {
//                    실제 위치 물어보기 (안드로이드 폰에게)


                    if (ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            mContext,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

//                        권한이 하나라도 없다면 밑의 코드 실행 X.
                        return
                    }

                    //                    위치관리자부터 가져오자.
                    val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0L,
                        0f,
                        object : LocationListener {
                            override fun onLocationChanged(p0: Location) {


                                if (needLocationSendServer) {

//                                    서버에 위경도값 보내주기.
                                    Log.d("위도", p0.latitude.toString())
                                    Log.d("경도", p0.longitude.toString())

                                    apiService.postRequestArrival(
                                        mAppointmentData.id,
                                        p0.latitude,
                                        p0.longitude).enqueue(object : Callback<BasicResponse> {
                                        override fun onResponse(
                                            call: Call<BasicResponse>,
                                            response: Response<BasicResponse>
                                        ) {
                                            if (response.isSuccessful) {

//                                              응답이 성공적으로 돌아오면 => 서버에 안보내기.
                                                needLocationSendServer = false

                                                Toast.makeText(
                                                    mContext,
                                                    "약속 인증에 성공했습니다.",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()

                                            }
                                            else {
//                                                서버가 알려주는 인증 실패 사유 출력

                                                val jsonObj = JSONObject(response.errorBody()!!.string())
                                                Log.d("응답전문", jsonObj.toString())

                                                val message = jsonObj.getString("message")

                                                Toast.makeText(mContext, message, Toast.LENGTH_SHORT)
                                                    .show()

                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<BasicResponse>,
                                            t: Throwable
                                        ) {

                                        }

                                    })



                                }

                            }

                            override fun onStatusChanged(
                                provider: String?,
                                status: Int,
                                extras: Bundle?
                            ) {

                            }

                            override fun onProviderEnabled(provider: String) {

                            }

                            override fun onProviderDisabled(provider: String) {

                            }

                        })

                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(mContext, "현재 위치 정보를 파악해야 약속 도착 인증이 가능합니다.", Toast.LENGTH_SHORT)
                        .show()
                }

            }

            TedPermission.create()
                .setPermissionListener(pl)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check()

        }

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

        getAppointmentFromServer()


//        5) 응용 2 - 출발지 좌표도 지도에 설정.  (setNaverMap 함수)


    }

    fun getAppointmentFromServer() {

//        친구 목록등의 내용을 서버에서 새로 받자.

        apiService.getRequestAppointmentDetail(mAppointmentData.id).enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                val basicResponse = response.body()!!

                mAppointmentData = basicResponse.data.appointment

                //        받고 나서 API 응답 성공시 친구 목록 새로고침

                val inflater = LayoutInflater.from(mContext)

                val sdf = SimpleDateFormat("H:mm 도착")

                for (friend  in  mAppointmentData.invitedFriendList) {

                    val friendView = inflater.inflate(R.layout.invited_friends_list_item, null)

                    val friendProfileImg = friendView.findViewById<ImageView>(R.id.friendProfileImg)
                    val nicknameTxt = friendView.findViewById<TextView>(R.id.nicknameTxt)
                    val statusTxt = friendView.findViewById<TextView>(R.id.statusTxt)

                    if (friend.arrivedAt == null) {
//                아직 도착 X
                        statusTxt.text = "도착 전"
                    }
                    else {
//                도착 시간 OK
                        statusTxt.text = sdf.format(friend.arrivedAt!!)
                    }

                    Glide.with(mContext).load(friend.profileImgURL).into(friendProfileImg)
                    nicknameTxt.text  =  friend.nickName


                    binding.invitedFriendsLayout.addView(friendView)

                }


            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })


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
//            val cameraUpdate = CameraUpdate.scrollTo(dest)
//            naverMap.moveCamera(cameraUpdate)


        //          - 출발지 마커 찍기

            val startLatLng = LatLng(mAppointmentData.startLatitude, mAppointmentData.startLongitude)

            val startMarker = Marker()
            startMarker.position = startLatLng
            startMarker.map = naverMap

        //          - 출발지 / 도착지 일직선 PathOverlay 그어주기



//             두 좌표의 중간점으로 카메라 이동?
            val centerOfStartAndDest = LatLng(
                (mAppointmentData.startLatitude + mAppointmentData.latitude)/2,
                (mAppointmentData.startLongitude + mAppointmentData.longitude)/2
            )
            val cameraUpdate = CameraUpdate.scrollTo(centerOfStartAndDest)
            naverMap.moveCamera(cameraUpdate)

//            거리에 따른 줌 레벨 변경 (도전과제)

            val zoomLevel = 11.0   // 두 좌표의 직선거리에 따라 어느 줌 레벨이 적당한지 계산해줘야함.
            naverMap.moveCamera(  CameraUpdate.zoomTo(zoomLevel)  )





//          - 대중교통 API 활용 => 1. 도착 예상시간 표시 (infoWindow)
        //          2. 실제 경유지로 PathOverlay 그어주기. => 도전과제. (마지막시간에 따로 풀이)

            val infoWindow = InfoWindow()

            val myODsayService = ODsayService.init(mContext, "UqivPrD/2a9zX6LAlrVto3HvYEXgv/BCT+0xVMjCVCg")

            myODsayService.requestSearchPubTransPath(
                mAppointmentData.startLongitude.toString(),
                mAppointmentData.startLatitude.toString(),
                mAppointmentData.longitude.toString(),
                mAppointmentData.latitude.toString(),
                null,
                null,
                null,
                object : OnResultCallbackListener {
                    override fun onSuccess(p0: ODsayData?, p1: API?) {

                        val jsonObj = p0!!.json
                        val resultObj = jsonObj.getJSONObject("result")
                        val pathArr = resultObj.getJSONArray("path")


                        val firstPath = pathArr.getJSONObject(0)

//                        출발점 ~ 경유지목록 ~ 도착지를 이어주는 Path 객체를 추가.
                        val points = ArrayList<LatLng>()
//                        출발지부터 추가.
                        points.add(  LatLng(mAppointmentData.startLatitude, mAppointmentData.startLongitude)  )

//                        경유지목록 파싱 -> for문으로 추가.
                        val subPathArr = firstPath.getJSONArray("subPath")
                        for (i  in  0 until subPathArr.length()) {
                            val subPathObj = subPathArr.getJSONObject(i)
                            Log.d("응답내용", subPathObj.toString())
                            if (!subPathObj.isNull("passStopList")) {

                                val passStopListObj = subPathObj.getJSONObject("passStopList")
                                val stationsArr = passStopListObj.getJSONArray("stations")
                                for ( j  in  0 until  stationsArr.length() ) {
                                    val stationObj = stationsArr.getJSONObject(j)
                                    Log.d("정거장목록", stationObj.toString())

//                                    각 정거장의 GPS좌표 추출 -> 네이버지도의 위치객체로 변환.
                                    val latLng = LatLng(stationObj.getString("y").toDouble(), stationObj.getString("x").toDouble())

//                                    지도의 선을 긋는 좌표 목록에 추가.
                                    points.add(latLng)

                                }

                            }
                        }


//                        모든 정거장 추가 => 실제 목적지 좌표 추가.
                        points.add( LatLng(mAppointmentData.latitude, mAppointmentData.longitude) )


//                        모든 경로 설정 끝. => 네이버 지도에 선으로 이어주자.
                        val path = PathOverlay()
                        path.coords = points
                        path.map =  naverMap



                        val infoObj = firstPath.getJSONObject("info")

                        val totalTime = infoObj.getInt("totalTime")

                        Log.d("총 소요시간", totalTime.toString())

                        val hour = totalTime / 60
                        val minute = totalTime % 60

                        Log.d("예상시간", hour.toString())
                        Log.d("예상분", minute.toString())

                        infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(mContext) {
                            override fun getContentView(p0: InfoWindow): View {

                                val myView = LayoutInflater.from(mContext).inflate(R.layout.my_custom_info_window, null)

                                val placeNameTxt = myView.findViewById<TextView>(R.id.placeNameTxt)
                                val arrivalTimeTxt = myView.findViewById<TextView>(R.id.arrivalTimeTxt)

                                placeNameTxt.text = mAppointmentData.placeName

                                if (hour == 0) {
                                    arrivalTimeTxt.text = "${minute}분 소요 예정"
                                }
                                else {
                                    arrivalTimeTxt.text = "${hour}시간 ${minute}분 소요 예정"
                                }

                                return myView
                            }

                        }

                        infoWindow.open(marker)

                    }

                    override fun onError(p0: Int, p1: String?, p2: API?) {
//                                실패시 예상시간 받아오지 못했다는 안내.

                        Log.d("예상시간실패", p1!!)
                    }

                })


        }

    }


}