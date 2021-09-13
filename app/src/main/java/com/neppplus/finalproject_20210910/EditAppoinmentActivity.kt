package com.neppplus.finalproject_20210910

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.neppplus.finalproject_20210910.databinding.ActivityEditAppoinmentBinding
import com.neppplus.finalproject_20210910.datas.BasicResponse
import com.neppplus.finalproject_20210910.utils.ContextUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EditAppoinmentActivity : BaseActivity() {

    lateinit var binding: ActivityEditAppoinmentBinding

//    선택한 약속 일시를 저장할 변수.
    val mSelectedDateTime = Calendar.getInstance()  // 기본값 : 현재 시간

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appoinment)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

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
            val lat = 37.57794132143432
            val lng = 127.03353823833795


//            서버에 API 호출
            apiService.postRequestAppointment(
                ContextUtil.getToken(mContext),
                inputTitle,
                finalDatetime,
                inputPlaceName,
                lat, lng).enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {

                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                }

            })


        }


    }

    override fun setValues() {

    }
}