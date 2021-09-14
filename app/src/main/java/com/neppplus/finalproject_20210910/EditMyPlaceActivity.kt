package com.neppplus.finalproject_20210910

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.neppplus.finalproject_20210910.databinding.ActivityEditMyPlaceBinding

class EditMyPlaceActivity : BaseActivity() {

    lateinit var binding: ActivityEditMyPlaceBinding

    var mSelectedLat = 0.0
    var mSelectedLng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_my_place)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.saveBtn.setOnClickListener {

            val inputName = binding.nameEdt.text.toString()

//            멤버변수에 있는 Lat / Lng 사용하자.



        }

    }

    override fun setValues() {

        titleTxt.text = "내 출발장소 추가"

        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment.getMapAsync {

            val marker = Marker()

            it.setOnMapClickListener { pointF, latLng ->

                mSelectedLat = latLng.latitude
                mSelectedLng = latLng.longitude

                marker.position = latLng

                marker.map = it

            }

        }

    }
}