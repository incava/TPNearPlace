package com.incava.tpnearplace.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.incava.tpnearplace.R
import com.kakao.sdk.user.UserApiClient

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Log.e("tag", "연결 끊기 실패", error)
            }
            else {
                Log.i("tag", "연결 끊기 성공. SDK에서 토큰 삭제 됨")
            }
        }
    }
}