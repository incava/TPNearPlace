package com.incava.tpnearplace.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.firestore.auth.User
import com.incava.tpnearplace.G
import com.incava.tpnearplace.R
import com.incava.tpnearplace.databinding.ActivityLoginBinding
import com.incava.tpnearplace.model.NidUserInfoResponse
import com.incava.tpnearplace.model.UserAccount
import com.incava.tpnearplace.network.RetrofitApiService
import com.incava.tpnearplace.network.RetrofitHelper
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvPlace.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.signUp.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
        binding.linearLogin.setOnClickListener {
            startActivity(Intent(this, EmailSigninActivity::class.java))
        }

        binding.ivKakao.setOnClickListener { clickedLoginKakao() }
        binding.ivGoogle.setOnClickListener { clickedLoginGoogle() }
        binding.ivNaver.setOnClickListener { clickedLoginNaver() }


        var keyHash = Utility.getKeyHash(this)
        Log.i("keyHash", keyHash)
    }

    fun clickedLoginKakao() {

        //카카오 공통 callback 함수
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (token != null) {
                Toast.makeText(this, "카카오 로그인 성공", Toast.LENGTH_SHORT).show()
                UserApiClient.instance.me { user, error ->
                    if (user != null) {
                        var id: String = user.id.toString()
                        var email: String = user.kakaoAccount?.email ?: ""
                        Toast.makeText(this, "$email", Toast.LENGTH_SHORT).show()
                        G.userAccount = UserAccount(id, email)
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }


        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this))
            UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
        else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }

    fun clickedLoginGoogle() {

        //Google에서 검색 [ 안드로이드 구글 로그인]
        //구글 로그인 화면을 실행하는 Intent를 통해 로그인 구현
        val signInOptions: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        val intent: Intent = GoogleSignIn.getClient(this, signInOptions).signInIntent
        resultLauncher.launch(intent)
    }

    // 구글 결과를 받을 계약체결 대행사
    val resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        object : ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult?) {
                //로그인 결과를 가져온 인텐트 소환.
                val intent: Intent? = result?.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                val account: GoogleSignInAccount = task.result
                var id: String = account.id.toString()
                var email: String = account.email ?: ""
                Toast.makeText(this@LoginActivity, email, Toast.LENGTH_SHORT).show()
                G.userAccount = UserAccount(id, email)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
        })

    fun clickedLoginNaver() {
        //네이버 아이디 로그인 초기화
        NaverIdLoginSDK.initialize(
            this,
            "tZf9SG3FO3OMJoEd8UWr",
            "XG0YzXnSL2",
            "${R.string.app_name}"
        )

        NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback {
            override fun onError(errorCode: Int, message: String) {
                Toast.makeText(this@LoginActivity, "error : $message", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Toast.makeText(this@LoginActivity, "로그인 실패 : $message", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess() {
                Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()

                val accessToken: String? = NaverIdLoginSDK.getAccessToken()
                Log.i("token", accessToken.toString())

                //레트로핏으로 사용자정보 API 가져오기
                val retrofit = RetrofitHelper
                    .getRetrofitInstance("https://openapi.naver.com")
                retrofit.create(RetrofitApiService::class.java)
                    .getNaveridUserInfo("Bearer $accessToken")
                    .enqueue(object : Callback<NidUserInfoResponse> {
                        override fun onResponse(
                            call: Call<NidUserInfoResponse>,
                            response: Response<NidUserInfoResponse>
                        ) {
                            val userInfoResponse: NidUserInfoResponse? = response.body()
                            val id:String= userInfoResponse?.response?.id ?: ""
                            val email:String= userInfoResponse?.response?.email ?: ""
                            G.userAccount = UserAccount(id,email)

                            Toast.makeText(this@LoginActivity,id,Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }

                        override fun onFailure(call: Call<NidUserInfoResponse>, t: Throwable) {
                            Toast.makeText(this@LoginActivity, "회원정보 검색 실패", Toast.LENGTH_SHORT)
                                .show()
                        }

                    })

            }
        })
    }


}