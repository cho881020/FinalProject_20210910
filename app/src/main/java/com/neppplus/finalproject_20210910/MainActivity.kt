package com.neppplus.finalproject_20210910

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.neppplus.finalproject_20210910.adapters.AppointmentAdapter
import com.neppplus.finalproject_20210910.databinding.ActivityMainBinding
import com.neppplus.finalproject_20210910.datas.AppointmentData
import com.neppplus.finalproject_20210910.datas.BasicResponse
import com.neppplus.finalproject_20210910.utils.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding

    val mAppointmentList = ArrayList<AppointmentData>()
    lateinit var mAdapter : AppointmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupEvents()
        setValues()
    }

    override fun onResume() {
        super.onResume()
        getAppointmentListFromServer()
    }

    override fun setupEvents() {

        binding.addAppoinmentBtn.setOnClickListener {
            val myIntent = Intent(mContext, EditAppoinmentActivity::class.java)
            startActivity(myIntent)
        }

    }

    override fun setValues() {

        Toast.makeText(mContext, "${GlobalData.loginUser!!.nickName}님 환영합니다!", Toast.LENGTH_SHORT).show()

//        getAppointmentListFromServer()

        mAdapter = AppointmentAdapter(mContext, R.layout.appointment_list_item, mAppointmentList)
        binding.appointmentListView.adapter = mAdapter
    }

    fun getAppointmentListFromServer() {

        apiService.getRequestAppointmentList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                val basicResponse = response.body()!!

                mAppointmentList.clear()

//                약속목록변수에 => 서버가 알려준 약속목록을 전부 추가.
                mAppointmentList.addAll( basicResponse.data.appointments )

//                어댑터 새로고침
                mAdapter.notifyDataSetChanged()

            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })

    }

}