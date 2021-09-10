package com.neppplus.finalproject_20210910

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.neppplus.finalproject_20210910.databinding.ActivitySignUpBinding
import com.neppplus.finalproject_20210910.datas.BasicResponse
import com.neppplus.finalproject_20210910.web.ServerAPI
import com.neppplus.finalproject_20210910.web.ServerAPIService
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : BaseActivity() {

    lateinit var binding:  ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.signUpBtn.setOnClickListener {

            val inputEmail = binding.emailEdt.text.toString()
            val inputPw = binding.pwEdt.text.toString()
            val inputNick = binding.nicknameEdt.text.toString()

            apiService.putRequestSignUp(inputEmail, inputPw, inputNick).enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {

//                    response.body -> 응답 코드 200번이어야 들어있다. (성공)
//                    가입 실패 / 로그인 실패 -> 응답 코드 400 -> errorBody에서 따로 찾아야함. (실패)

                    if ( response.isSuccessful ) {
                        val basicResponse = response.body()!!

                        Log.d("서버 메세지", basicResponse.message)
                        Toast.makeText(mContext, basicResponse.message, Toast.LENGTH_SHORT).show()
                    }
                    else {
//                        어떤 이유던 성공이 아닌 상황.
                        val errorBodyStr =  response.errorBody()!!.string()

//                        단순 JSON 형태의 String으로 내려옴. => JSONObject 형태로 가공 필요.
                        Log.d("에러 경우", errorBodyStr)
                        val jsonObj = JSONObject(errorBodyStr)
                        val message = jsonObj.getString("message")

//                        runOnUiThread 를 해주지 않아도 UI 접근 가능.
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
                    }




                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                }

            })


        }

    }

    override fun setValues() {


    }
}









