package com.neppplus.finalproject_20210910

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.neppplus.finalproject_20210910.datas.AppointmentData
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener

class ViewMapActivity : BaseActivity() {

    lateinit var mAppointmentData : AppointmentData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_map)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        mAppointmentData = intent.getSerializableExtra("appointment") as AppointmentData

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naverMapFrag) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naverMapFrag, it).commit()
            }

        mapFragment.getMapAsync {

            val appointmentLatLng = LatLng(mAppointmentData.latitude,  mAppointmentData.longitude)

//            지도도 약속장소로 카메라를 옮겨주자.
            val cameraUpdate = CameraUpdate.scrollTo(appointmentLatLng)
            it.moveCamera(cameraUpdate)

//            마커를 찍고 표시

            val marker = Marker()
            marker.position = appointmentLatLng
            marker.map = it

//            기본적인 모양의 정보창 띄워주기 (마커에 연결)

            val infoWindow = InfoWindow()

            infoWindow.adapter = object : InfoWindow.DefaultViewAdapter(mContext) {
                override fun getContentView(p0: InfoWindow): View {

                    val myView = LayoutInflater.from(mContext).inflate(R.layout.my_custom_info_window, null)

                    val placeNameTxt = myView.findViewById<TextView>(R.id.placeNameTxt)
                    val arrivalTimeTxt = myView.findViewById<TextView>(R.id.arrivalTimeTxt)

                    placeNameTxt.text = mAppointmentData.placeName
//                    arrivalTimeTxt.text = "??시간 ?분 소요예상"

                    val myODsayService = ODsayService.init(mContext, "UqivPrD/2a9zX6LAlrVto3HvYEXgv/BCT+0xVMjCVCg")

                    myODsayService.requestSearchPubTransPath(
                        126.92971682319262.toString(),
                        37.613082888996104.toString(),
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

//                                for (i  in   0 until pathArr.length()) {
//                                    val pathObj = pathArr.getJSONObject(i)
//                                    Log.d("API응답", pathObj.toString(4))
//                                }

                                val firstPath = pathArr.getJSONObject(0)
                                val infoObj = firstPath.getJSONObject("info")

                                val totalTime = infoObj.getInt("totalTime")

                                Log.d("총 소요시간", totalTime.toString())

//                                시간 / 분으로 분리.  92 => 1시간 32분
//                                시간 : 전체분 / 60
//                                분 : 전체분 % 60

                                val hour = totalTime / 60
                                val minute = totalTime % 60

                                Log.d("예상시간", hour.toString())
                                Log.d("예상분", minute.toString())

                                arrivalTimeTxt.text = "테스트"

                                runOnUiThread {
                                    arrivalTimeTxt.text = "${hour}시간 ${minute}분 소요 예상"
                                }



                            }

                            override fun onError(p0: Int, p1: String?, p2: API?) {
//                                실패시 예상시간 받아오지 못했다는 안내.

                                Log.d("예상시간실패", p1!!)
                                arrivalTimeTxt.text = "예상시간 받아오기 실패"
                            }

                        })


                    return  myView
                }

            }

            infoWindow.open(marker)

//            지도의 아무데나 찍으면 => 열려있는 정보창 닫아주기.
            it.setOnMapClickListener { pointF, latLng ->

                infoWindow.close()

            }

//            마커를 누를때 : 정보창이 닫혀있으면 => 열어주자.
//             열려있으면 => 닫아주자.

            marker.setOnClickListener {

                val clickedMarker = it as Marker

                if (clickedMarker.infoWindow == null) {
//                    마커에 연결된 정보창 없을때 (닫혀있을때)
                    infoWindow.open(clickedMarker)
                }
                else {
                    infoWindow.close()
                }

                return@setOnClickListener true
            }


        }

    }
}