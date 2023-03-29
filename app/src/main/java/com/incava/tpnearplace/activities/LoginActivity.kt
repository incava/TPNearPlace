package com.incava.tpnearplace.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.incava.tpnearplace.R
import com.incava.tpnearplace.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvPlace.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        binding.signUp.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
        }
        binding.linearLogin.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
        }

        binding.ivKakao.setOnClickListener { clickedLoginKakao() }
        binding.ivGoogle.setOnClickListener {  clickedLoginGoogle() }
        binding.ivNaver.setOnClickListener {  clickedLoginNaver() }


    }

    fun clickedLoginKakao(){

    }

    fun clickedLoginGoogle(){

    }

    fun clickedLoginNaver(){

    }




}