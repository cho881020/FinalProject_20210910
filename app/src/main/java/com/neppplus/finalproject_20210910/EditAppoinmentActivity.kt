package com.neppplus.finalproject_20210910

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import com.neppplus.finalproject_20210910.adapters.MyFriendSpinnerAdapter
import com.neppplus.finalproject_20210910.adapters.StartPlaceSpinnerAdapter
import com.neppplus.finalproject_20210910.databinding.ActivityEditAppoinmentBinding
import com.neppplus.finalproject_20210910.datas.BasicResponse
import com.neppplus.finalproject_20210910.datas.PlaceData
import com.neppplus.finalproject_20210910.datas.UserData
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditAppoinmentActivity : BaseActivity() {

    lateinit var binding: ActivityEditAppoinmentBinding

//    선택한 약속 일시를 저장할 변수.
    val mSelectedDateTime = Calendar.getInstance()  // 기본값 : 현재 시간

//    선택한 약속장소를 저장할 변수.
    var mSelectedLat = 0.0 // Double을 넣을것임.
    var mSelectedLng = 0.0 // Double

//    출발지 목록을 담아둘 리스트.
    val mStartPlaceList = ArrayList<PlaceData>()
    lateinit var mSpinnerAdapter : StartPlaceSpinnerAdapter

//    내 친구 목록을 담아둘 리스트.
    val mMyFriendsList = ArrayList<UserData>()
    lateinit var mFriendSpinnerAdapter :  MyFriendSpinnerAdapter

//    선택된 출발지를 담아줄 변수.
    lateinit var mSelectedStartPlace: PlaceData

//    선택된 출발지를 보여줄 마커
    val mStartPlaceMarker = Marker()

//    화면에 그려질 출발~도착지 연결 선
    val mPath = PathOverlay()


//    선택된 도착지를 보여줄 마커 하나만 생성.
    val selectedPointMarker = Marker()

//    도착지에 보여줄 정보창
    val mInfoWindow = InfoWindow()

//    네이버 지도를 멤버변수로 담자.
    var mNaverMap : NaverMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appoinment)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

//        지도 영역에 손을 대면 => 스크롤뷰를 정지.
//        대안 : 지도 위에 겹쳐둔 텍스트뷰에 손을대면 => 스크롤뷰를 정지.

        binding.scrollHelpTxt.setOnTouchListener { view, motionEvent ->

            binding.scrollView.requestDisallowInterceptTouchEvent(true)

//            터치 이벤트만 먹히게? X. => 뒤에 가려진 지도 동작도 같이 실행.
            return@setOnTouchListener false
        }


//        스피너의 선택 이벤트.
        binding.startPlaceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

//                화면이 뜨면 자동으로 0번 아이템이 선택된다.
                Log.d("선택된위치", position.toString())

//                스피너의 위치에 맞는 장소를 선택된 출발지점으로 선정.
                mSelectedStartPlace = mStartPlaceList[position]

                Log.d("출발지위경도",  "${mSelectedStartPlace.latitude},  ${mSelectedStartPlace.longitude}")

                mNaverMap?.let {
                    drawStartPlaceToDestination(it)
                }


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }


//        날짜 선택
        binding.dateTxt.setOnClickListener {

//            DatePicker 띄우기 -> 입력 완료되면,  연/월/일을 제공해줌.
//            mSelec... 에 연/월/일 저장

            val dateSetListener = object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {

//                    선택된 날짜로서 지정.
                    mSelectedDateTime.set(year, month, day)

//                    선택된 날짜로 문구 변경. => 2021. 9. 8 (월) => SimpleDateFormat

                    val sdf = SimpleDateFormat("yyyy. M. d (E)")
                    binding.dateTxt.text = sdf.format( mSelectedDateTime.time )

                }

            }

            val dpd = DatePickerDialog(mContext, dateSetListener,
                mSelectedDateTime.get(Calendar.YEAR),
                mSelectedDateTime.get(Calendar.MONTH),
                mSelectedDateTime.get(Calendar.DAY_OF_MONTH))

            dpd.show()


        }

//        시간 선택
        binding.timeTxt.setOnClickListener {
//            TimePicker 띄우기 -> 입력 완료되면   시/분 제공
//             mSelect... 에 시/분 저장

            val tsl = object  : TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {

                    mSelectedDateTime.set(Calendar.HOUR_OF_DAY, hour)
                    mSelectedDateTime.set(Calendar.MINUTE, minute)

//                    오후 6:05  형태로 가공. => SimpleDateFormat
                    val sdf = SimpleDateFormat("a h:mm")
                    binding.timeTxt.text = sdf.format(mSelectedDateTime.time)

                }

            }

            TimePickerDialog(mContext, tsl,
                mSelectedDateTime.get(Calendar.HOUR_OF_DAY),
                mSelectedDateTime.get(Calendar.MINUTE),
                false).show()

        }



//        확인버튼이 눌리면?

        binding.okBtn.setOnClickListener {

//            입력한 값들 받아오기
//            1. 일정 제목
            val inputTitle = binding.titleEdt.text.toString()

//            2. 약속 일시? -> "2021-09-13 11:11" String 변환까지.
//              => 날짜 / 시간중 선택 안한게 있다면? 선택하라고 토스트, 함수 강제 종료. (vaildation)

            if ( binding.dateTxt.text == "일자 설정" ) {
                Toast.makeText(mContext, "일자를 설정하지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (binding.timeTxt.text == "시간 설정") {
                Toast.makeText(mContext, "시간을 설정하지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            여기 코드 실행된다 : 일자 / 시간 모두 설정했다.
//            선택된 약속일시를 -> "yyyy-MM-dd HH:mm" 양식으로 가공. => 최종 서버에 파라미터로 첨부
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
            val finalDatetime = sdf.format(mSelectedDateTime.time)

            Log.d("서버에보낼 약속일시", finalDatetime)

//            3. 약속 장소?
//            - 장소 이름
            val inputPlaceName = binding.placeSearchEdt.text.toString()

//            - 장소 위도/경도?  (임시 : 학원 좌표 하드코딩)
//            val lat = 37.57794132143432
//            val lng = 127.03353823833795

//            지도에서 클릭한 좌표로  위경도 첨부.
//            선택 안했다면? 선택해달라고 안내.

            if (mSelectedLat == 0.0 && mSelectedLng == 0.0) {
                Toast.makeText(mContext, "약속 장소를 지도를 클릭해 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            출발지 정보도 같이 첨부.

//            서버에 API 호출
            apiService.postRequestAppointment(
                inputTitle,
                finalDatetime,
                mSelectedStartPlace.name,
                mSelectedStartPlace.latitude,
                mSelectedStartPlace.longitude,
                inputPlaceName,
                mSelectedLat, mSelectedLng).enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {

                    if (response.isSuccessful) {
                        Toast.makeText(mContext, "약속을 등록했습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                }

            })


        }


    }

    override fun setValues() {

        titleTxt.text = "약속 잡기"

        mFriendSpinnerAdapter = MyFriendSpinnerAdapter(mContext, R.layout.friend_list_item, mMyFriendsList)
        binding.myFriendsSpinner.adapter = mFriendSpinnerAdapter

//        내 친구 목록 담아주기
        apiService.getRequestFriendList("my").enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful) {
                    mMyFriendsList.clear()
                    mMyFriendsList.addAll(response.body()!!.data.friends)
                    mFriendSpinnerAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })


        mSpinnerAdapter = StartPlaceSpinnerAdapter(mContext, R.layout.my_place_list_item, mStartPlaceList)
        binding.startPlaceSpinner.adapter = mSpinnerAdapter

//        내 출발장소 목록 담아주기
        apiService.getRequestMyPlaceList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                if (response.isSuccessful) {

                    val basicResponse = response.body()!!

                    mStartPlaceList.clear()
                    mStartPlaceList.addAll(basicResponse.data.places)

                    mSpinnerAdapter.notifyDataSetChanged()

                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })


//        카카오 지도 띄워보기

//        val mapView = MapView(mContext)
//
//        binding.mapView.addView(mapView)


//        네이버 지도 Fragment 다루기

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naverMapView) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naverMapView, it).commit()
            }

        mapFragment.getMapAsync {
            Log.d("지도객체-바로할일", it.toString())

//            멤버변수에서 null 이던 네이버지도 변수를 채워넣기.
            mNaverMap = it

//            학원 좌표를 지도 시작점으로. (예제)

//            it.mapType = NaverMap.MapType.Hybrid


//            좌표를 다루는 변수 - LatLng클래스 활용.
            val neppplusCoord = LatLng(37.57793737795487, 127.03355269913862)

            val cameraUpdate = CameraUpdate.scrollTo(neppplusCoord)
            it.moveCamera(cameraUpdate)


            val uiSettings = it.uiSettings
            uiSettings.isCompassEnabled = true
            uiSettings.isScaleBarEnabled = false


            selectedPointMarker.icon = OverlayImage.fromResource(R.drawable.red_marker)

            it.setOnMapClickListener { pointF, latLng ->
//                Toast.makeText(mContext, "위도 : ${latLng.latitude},  경도 : ${latLng.longitude}", Toast.LENGTH_SHORT).show()

                mSelectedLat = latLng.latitude
                mSelectedLng = latLng.longitude

//                좌표를 받아서 => 미리 만들어둔 마커의 좌표로 연결. => 맵에 띄우자.
                selectedPointMarker.position = LatLng(mSelectedLat, mSelectedLng)

                selectedPointMarker.map = it

                drawStartPlaceToDestination(it)

            }


        }

    }

    fun drawStartPlaceToDestination(naverMap: NaverMap) {

//        시작지점의 위경도
//        mSelectedStartPlace.latitude 등 활용.

//        시작지점에 좌표 마커 찍어주기.

        mStartPlaceMarker.position = LatLng(mSelectedStartPlace.latitude, mSelectedStartPlace.longitude)
        mStartPlaceMarker.map = naverMap


//        도착지점의 위경도
//        mSelectedLat 등 변수 활용.

//        예제. 시작지점 -> 도착지점으로 연결 선 그어주기.

//        좌표 목록을 ArrayList로 담자.
        val points = ArrayList<LatLng>()

//        출발지점의 좌표를 선의 출발점으로 설정.
        points.add(  LatLng(mSelectedStartPlace.latitude,  mSelectedStartPlace.longitude)  )

//        대중교통 길찾기 API => 들리는 좌표들을 제공 => 목록을 담아주자.
        val odsay = ODsayService.init(mContext, "UqivPrD/2a9zX6LAlrVto3HvYEXgv/BCT+0xVMjCVCg")

        odsay.requestSearchPubTransPath(
            mSelectedStartPlace.longitude.toString(),
            mSelectedStartPlace.latitude.toString(),
            mSelectedLng.toString(),
            mSelectedLat.toString(),
            null,
        null,
        null,
            object : OnResultCallbackListener {
                override fun onSuccess(p0: ODsayData?, p1: API?) {

                    val jsonObj = p0!!.json
                    val resultObj = jsonObj.getJSONObject("result")
                    val pathArr = resultObj.getJSONArray("path")
                    val firstPathObj = pathArr.getJSONObject(0)

//                    총 소요시간이 얼마나 걸리나?
                    Log.d("길찾기응답", firstPathObj.toString())
                    val infoObj = firstPathObj.getJSONObject("info")
                    val totalTime = infoObj.getInt("totalTime")

                    Log.d("총소요시간", totalTime.toString())

//                    멤버변수로 만들어둔 정보창의 내용 설정, 열어주기
                    mInfoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext) {
                        override fun getText(p0: InfoWindow): CharSequence {
                            return "${totalTime}분 소요 예정"
                        }

                    }
                    mInfoWindow.open(selectedPointMarker)



//                    경유지들 좌표를 목록에 추가 (결과가 어떻게 되어있는지 분석. Parsing)
//                    지도에 선을 긋는데 필요한 좌표 목록 추출.
                    val subPathArr = firstPathObj.getJSONArray("subPath")

                    for (i  in  0 until subPathArr.length()) {

                        val subPathObj = subPathArr.getJSONObject(i)

                        if (!subPathObj.isNull("passStopList")) {

//                            정거장 목록을 불러내보자.
                            val passStopListObj = subPathObj.getJSONObject("passStopList")
                            val stationsArr = passStopListObj.getJSONArray("stations")
                            for ( j  in  0 until  stationsArr.length() ) {

                                val stationObj = stationsArr.getJSONObject(j)

                                val latLng = LatLng(stationObj.getString("y").toDouble(), stationObj.getString("x").toDouble())

//                                points ArrayList에 경유지로 추가.
                                points.add(latLng)

                            }

                        }



                    }



//                    최종 목적지 좌표도 추가

//        최종 목적지를 추가.
                    points.add(  LatLng(mSelectedLat, mSelectedLng)  )

//        매번 새로 PolyLine을 그리면, 선이 하나씩 추가됨.
//        멤버변수로 선을 하나 지정해두고, 위치값만 변경하면서 사용.
//        val polyline = PolylineOverlay()

                    mPath.coords = points

                    mPath.map = naverMap


                }

                override fun onError(p0: Int, p1: String?, p2: API?) {

                }

            })



    }


}