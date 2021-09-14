package com.neppplus.finalproject_20210910

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.neppplus.finalproject_20210910.databinding.ActivityMySettingBinding
import com.neppplus.finalproject_20210910.utils.GlobalData

class MySettingActivity : BaseActivity() {

    lateinit var binding: ActivityMySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_setting)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

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

                Toast.makeText(mContext, "${minuteEdt.text.toString()}", Toast.LENGTH_SHORT).show()

            })
            alert.setNegativeButton("취소", null)
            alert.show()


        }

    }

    override fun setValues() {

        titleTxt.text = "내 정보 설정"

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

    }
}