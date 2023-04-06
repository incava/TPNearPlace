package com.incava.tpnearplace.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.incava.tpnearplace.R
import com.incava.tpnearplace.databinding.ActivityPlaceUrlBinding

class PlaceUrlActivity : AppCompatActivity() {
    val binding : ActivityPlaceUrlBinding by lazy { ActivityPlaceUrlBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.wv.apply {
            webViewClient = WebViewClient() // 현재 웹뷰 안에서 웹문서 열리도록.
            webChromeClient = WebChromeClient() // 웹문서 안에서 다이얼로그 같은 것을 발동하도록.
            settings.javaScriptEnabled = true //기본적으로 보안문제로 JS 동작을 막아놓았기에.. 이를 허용해야함.

            var place_url : String? = intent.getStringExtra("place_url")
            binding.wv.loadUrl(place_url!!)
        }

    }

    override fun onBackPressed() {
        if (binding.wv.canGoBack()) binding.wv.goBack()
        else super.onBackPressed()

    }

}