package com.neppplus.finalproject_20210910

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.finalproject_20210910.adapters.AppointmentAdapter
import com.neppplus.finalproject_20210910.adapters.AppointmentRecyclerAdapter
import com.neppplus.finalproject_20210910.databinding.ActivityMainBinding
import com.neppplus.finalproject_20210910.datas.AppointmentData
import com.neppplus.finalproject_20210910.datas.BasicResponse
import com.neppplus.finalproject_20210910.fragments.InvitedAppointmentsListFragment
import com.neppplus.finalproject_20210910.fragments.MyAppointmentsListFragment
import com.neppplus.finalproject_20210910.fragments.SettingsFragment
import com.neppplus.finalproject_20210910.utils.GlobalData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupEvents()
        setValues()
    }


    override fun setupEvents() {

        binding.bottomNavBar.setOnItemSelectedListener {

            val frag = when (it.itemId) {
                R.id.myAppointments -> MyAppointmentsListFragment.getFrag()
                R.id.invitedAppointments -> InvitedAppointmentsListFragment.getFrag()
                else -> SettingsFragment.getFrag()
            }

            changeFragment(frag)

            return@setOnItemSelectedListener true
        }



        profileImg.setOnClickListener {
            val myIntent = Intent(mContext, MySettingActivity::class.java)
            startActivity(myIntent)
        }

    }

    override fun setValues() {

        changeFragment(MyAppointmentsListFragment.getFrag())

        Toast.makeText(mContext, "${GlobalData.loginUser!!.nickName}님 환영합니다!", Toast.LENGTH_SHORT).show()


//        상속받은, 액션바에 있는 프로필버튼 보여주기.
        profileImg.visibility = View.VISIBLE

//        메인화면의 화면 제목 변경
        titleTxt.text = "메인 화면"

    }

    fun changeFragment(frag: Fragment) {

        val fragTransaction = supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.fragFrameLayout, frag)
        fragTransaction.commit()

    }



}