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
import com.facebook.*
import com.facebook.login.LoginManager

import com.facebook.login.widget.LoginButton
import org.json.JSONObject
import java.util.*


class LoginActivity : BaseActivity() {

    lateinit var binding : ActivityLoginBinding

    lateinit var callbackManager : CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setupEvents()
        setValues()

    }

    override fun setupEvents() {

        callbackManager = CallbackManager.Factory.create();

        binding.loginButton.setReadPermissions("email")

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

}