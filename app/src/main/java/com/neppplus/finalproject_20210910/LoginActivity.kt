package com.neppplus.finalproject_20210910

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.neppplus.finalproject_20210910.databinding.ActivityLoginBinding
import android.content.pm.PackageManager

import android.content.pm.PackageInfo
import android.util.Base64
import android.util.Log
import java.security.MessageDigest

import com.facebook.login.LoginResult

import android.view.View
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginManager

import com.facebook.login.widget.LoginButton
import com.kakao.sdk.user.UserApiClient
import com.neppplus.finalproject_20210910.datas.BasicResponse
import com.neppplus.finalproject_20210910.utils.ContextUtil
import com.neppplus.finalproject_20210910.utils.GlobalData
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class LoginActivity : BaseActivity() {

    lateinit var binding : ActivityLoginBinding

    lateinit var callbackManager : CallbackManager

    lateinit var mNaverLoginModule : OAuthLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setupEvents()
        setValues()

    }

    override fun setupEvents() {

        binding.naverLoginBtn.setOnClickListener {


            mNaverLoginModule.startOauthLoginActivity(this, object : OAuthLoginHandler() {
                override fun run(success: Boolean) {

                    if (success) {
                        
//                        네이버로그인 성공하면 그 계정의 토큰값 추출
                        
                        val accessToken = mNaverLoginModule.getAccessToken(mContext)
                        Log.d("네이버토큰값", accessToken)

//                        코루틴으로 백그라운드 작업.

//                        코루틴 => scope 코드 실행 {  } 정의.
//                        Dispatcher => UI 쓰레드 / 백그라운드(Default) / IO (다운로드/업로드)

                        val scope = CoroutineScope(Dispatchers.Default)

                        scope.launch {

//                            쓰레드 대신, 코루틴 사용 예시

                            //                            이 내부의 코드를 백그라운드 실행

                            val url = "https://openapi.naver.com/v1/nid/me"
                            val jsonObj = JSONObject(mNaverLoginModule.requestApi(mContext, accessToken, url))
                            Log.d("네이버로그인내정보", jsonObj.toString())

                            val responseObj = jsonObj.getJSONObject("response")

//                            정보 추출
                            val uid = responseObj.getString("id")
                            val name = responseObj.getString("name")

//                            우리 서버로 전달.
                            apiService.postRequestSocialLogin(
                                "naver",
                                uid,
                                name
                            ).enqueue(object : retrofit2.Callback<BasicResponse> {
                                override fun onResponse(
                                    call: Call<BasicResponse>,
                                    response: Response<BasicResponse>
                                ) {

//                                    소셜로그인 마무리 -> 토큰,Globaldata 로그인사용자 -> 메인화면으로 이동.

                                    val basicResponse = response.body()!!
                                    ContextUtil.setToken(mContext, basicResponse.data.token)
                                    GlobalData.loginUser = basicResponse.data.user
                                    moveToMain()

                                }

                                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                                }

                            })

                        }


                    }
                    else {
                        Toast.makeText(mContext, "네이버 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }

                }

            })

        }

        binding.loginBtn.setOnClickListener {

            val inputId = binding.emailEdt.text.toString()
            val inputPw = binding.pwEdt.text.toString()

//            POST -> /user 로 로그인 시도.

            apiService.postRequestLogin(inputId, inputPw).enqueue(object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.isSuccessful) {
                        val basicResponse = response.body()!!
                        Toast.makeText(mContext, basicResponse.message, Toast.LENGTH_SHORT).show()

//                        로그인 성공 =? "data" jsonObject -> DataResponse -> token변수.

                        Log.d("토큰", basicResponse.data.token)

                        ContextUtil.setToken(mContext, basicResponse.data.token)


//                        Toast.makeText(mContext, basicResponse.data.user.email, Toast.LENGTH_SHORT).show()

//                        로그인한사람이 누구인지 => GlobalData 클래스에 저장.

                        GlobalData.loginUser = basicResponse.data.user

                        moveToMain()


                    }
                    else {

                        val errorBodyStr = response.errorBody()!!.string()
                        val jsonObj = JSONObject(errorBodyStr)
                        Log.d("응답본문", jsonObj.toString())
                        val message = jsonObj.getString("message")

                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                }

            })


        }

        binding.signUpBtn.setOnClickListener {
            val myIntent = Intent(mContext, SignUpActivity::class.java)
            startActivity(myIntent)
        }


        binding.kakaoLoginBtn.setOnClickListener {
            // 카카오계정으로 로그인
            UserApiClient.instance.loginWithKakaoAccount(mContext) { token, error ->
                if (error != null) {
                    Log.e("카카오로그인", "로그인 실패", error)
                }
                else if (token != null) {
                    Log.i("카카오로그인", "로그인 성공 ${token.accessToken}")

                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            Log.e("카카오로그인", "사용자 정보 요청 실패", error)
                        } else if (user != null) {
                            Log.i(
                                "카카오로그인", "사용자 정보 요청 성공" +
                                        "\n회원번호: ${user.id}" +
                                        "\n이메일: ${user.kakaoAccount?.email}" +
                                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                        "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                            )

//                            소셜로그인 API에 "kakao" 로 id / 닉네임 전송. (도전과제)
                            apiService.postRequestSocialLogin(
                                "kakao",
                                user.id.toString(),
                                user.kakaoAccount?.profile?.nickname!!).enqueue(object : Callback<BasicResponse> {
                                override fun onResponse(
                                    call: Call<BasicResponse>,
                                    response: Response<BasicResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        val basicResponse = response.body()!!
                                        ContextUtil.setToken(mContext, basicResponse.data.token)
                                        GlobalData.loginUser = basicResponse.data.user
                                        moveToMain()
                                    }
                                    else {
                                        val errorBody = response.errorBody()!!.string()
                                        val jsonObj = JSONObject(errorBody)
                                        Log.d("응답내용", jsonObj.toString())
                                    }

                                }

                                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                                }

                            })

                        }
                    }

                }
            }
        }


        binding.facebookLoginBtn.setOnClickListener {

//            우리가 붙인 버튼에 기능 활용

//            커스텀 버튼에, 로그인 하고 돌아온 callback을 따로 설정.

            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {

                    Log.d("로그인성공", "우리가만든버튼으로 성공")

//                    페이스북에 접근할 수 있는 토큰이 생겨 있다. => 활용.
//                    나의 (로그인한사람) 정보 (GraphRequest) 를 받아오는데 활용.

                    val graphRequest = GraphRequest.newMeRequest(result?.accessToken, object : GraphRequest.GraphJSONObjectCallback {
                        override fun onCompleted(jsonObj: JSONObject?, response: GraphResponse?) {

                            Log.d("내정보내용", jsonObj.toString())

                            val name = jsonObj!!.getString("name")
                            val id = jsonObj.getString("id")

//                            가입한 회원 이름 => 우리 서버에 사용자 이름으로 (닉네임으로) 저장
                            Log.d("이름", name)
//                            페북에서 사용자를 구별하는 고유번호. => 우리 서버에 같이 저장.  회원가입 or 로그인 근거자료로 활용
                            Log.d("id값", id)

                            apiService.postRequestSocialLogin("facebook", id, name).enqueue(object : Callback<BasicResponse> {
                                override fun onResponse(
                                    call: Call<BasicResponse>,
                                    response: Response<BasicResponse>
                                ) {
                                    val basicResponse = response.body()!!

                                    Toast.makeText(mContext, basicResponse.message, Toast.LENGTH_SHORT).show()
                                    Log.d("API서버가 준 토큰값", basicResponse.data.token)

//                                    ContextUtil 등으로 SharedPreferences로 토큰값 저장.
                                    ContextUtil.setToken(mContext, basicResponse.data.token)
                                    GlobalData.loginUser = basicResponse.data.user

//                                    메인화면으로 이동.
                                    moveToMain()



                                }

                                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                                }

                            })

                        }

                    })

//                    위에서 정리한 내용을 들고, 내 정보를 실제로 요청.
                    graphRequest.executeAsync()


                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException?) {

                }

            })


            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"))

        }



        // Callback registration
//        binding.loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
//            override fun onSuccess(loginResult: LoginResult?) {
//                // App code
//
//                Log.d("확인용", loginResult.toString())
//
//                val accessToken = AccessToken.getCurrentAccessToken()
//                Log.d("페북토큰", accessToken.toString())
//
//            }
//
//            override fun onCancel() {
//                // App code
//            }
//
//            override fun onError(exception: FacebookException) {
//                // App code
//            }
//        })

    }

    override fun setValues() {


//        페북로그인 - 콜백 관련 변수
        callbackManager = CallbackManager.Factory.create();

//        네이버로그인 모듈 세팅
        mNaverLoginModule = OAuthLogin.getInstance()
        mNaverLoginModule.init(
            mContext,
            getString(R.string.naver_client_id),
            getString(R.string.naver_secret_key),
            getString(R.string.naver_client_name)
        )

//        제목 문구 숨김, 회사 로고 보여주기
        titleTxt.visibility = View.GONE
        companyLogoImg.visibility = View.VISIBLE


//        카톡으로 받은 코드 복붙 => 키 해시값 추출
        val info = packageManager.getPackageInfo(
            "com.neppplus.finalproject_20210910",
            PackageManager.GET_SIGNATURES
        )
        for (signature in info.signatures) {
            val md: MessageDigest = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }


    fun moveToMain() {
        val myIntent = Intent(mContext, MainActivity::class.java)
        startActivity(myIntent)
        finish()
    }


}