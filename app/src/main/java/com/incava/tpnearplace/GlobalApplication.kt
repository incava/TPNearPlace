package com.incava.tpnearplace

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //개발자 사이트에 등록한 네이티브 앱키
        KakaoSdk.init(this,"1f70165e329b52b3901e37ae845aadc9")
    }
}