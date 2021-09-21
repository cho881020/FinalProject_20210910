package com.neppplus.finalproject_20210910

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.finalproject_20210910.adapters.AppointmentAdapter
import com.neppplus.finalproject_20210910.adapters.AppointmentRecyclerAdapter
import com.neppplus.finalproject_20210910.databinding.ActivityMainBinding
import com.neppplus.finalproject_20210910.datas.AppointmentData
import com.neppplus.finalproject_20210910.datas.BasicResponse
import com.neppplus.finalproject_20210910.service.MyJobService
import com.neppplus.finalproject_20210910.service.MyJobService.Companion.JOB_A
import com.neppplus.finalproject_20210910.utils.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding

    val mAppointmentList = ArrayList<AppointmentData>()
//    lateinit var mAdapter : AppointmentAdapter

    lateinit var mRecyclerAdapter : AppointmentRecyclerAdapter

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

            val js = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val serviceComponent = ComponentName(this, MyJobService::class.java)
            val jobInfo = JobInfo.Builder(JOB_A, serviceComponent)
                .setMinimumLatency(TimeUnit.MINUTES.toMillis(1))
                .setOverrideDeadline(TimeUnit.MINUTES.toMillis(3))
                .build()
            js.schedule(jobInfo)
            
            Log.d("잡스케쥴러 설정", "확인용")

//            val myIntent = Intent(mContext, EditAppoinmentActivity::class.java)
//            startActivity(myIntent)
        }

        profileImg.setOnClickListener {
            val myIntent = Intent(mContext, MySettingActivity::class.java)
            startActivity(myIntent)
        }

    }

    override fun setValues() {

        Toast.makeText(mContext, "${GlobalData.loginUser!!.nickName}님 환영합니다!", Toast.LENGTH_SHORT).show()

//        getAppointmentListFromServer()

//        mAdapter = AppointmentAdapter(mContext, R.layout.appointment_list_item, mAppointmentList)
//        binding.appointmentListView.adapter = mAdapter

        mRecyclerAdapter = AppointmentRecyclerAdapter(mContext, mAppointmentList)
        binding.appointmentRecyclerView.adapter = mRecyclerAdapter

        binding.appointmentRecyclerView.layoutManager = LinearLayoutManager(mContext)


//        상속받은, 액션바에 있는 프로필버튼 보여주기.
        profileImg.visibility = View.VISIBLE

//        메인화면의 화면 제목 변경
        titleTxt.text = "메인 화면"

    }

    fun getAppointmentListFromServer() {

        apiService.getRequestAppointmentList().enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                val basicResponse = response.body()!!

                mAppointmentList.clear()

//                약속목록변수에 => 서버가 알려준 약속목록을 전부 추가.
                mAppointmentList.addAll( basicResponse.data.appointments )

//                어댑터 새로고침
                mRecyclerAdapter.notifyDataSetChanged()

            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })

    }

}