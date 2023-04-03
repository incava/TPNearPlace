package com.incava.tpnearplace.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.incava.tpnearplace.R
import com.incava.tpnearplace.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //툴바를 액션바로 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        //버튼 클릭 리스너
        binding.btnSignup.setOnClickListener { clickSignUp() }
    }// onCreate

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun clickSignUp() {
        //Firebase Firestore DB에 사용자 정보 저장하기

        var email: String = binding.etEmail.text.toString()
        var password: String = binding.etPassword.text.toString()
        var passwordConfirm: String = binding.etPasswordConfirm.text.toString()

        if (password != passwordConfirm) {
            AlertDialog.Builder(this@SignupActivity)
                .setMessage("패스워드확인에 문제가 있습니다. 다시 확인해 주시기 바랍니다.")
                .setPositiveButton("확인") { _, _ ->
                    binding.etPasswordConfirm.requestFocus()
                    binding.etPasswordConfirm.selectAll()
                }
                .show()
            //커서가 모두를 잡는 메서드
            return
        }

        // fireStore 얻어 오기
        val db = FirebaseFirestore.getInstance()

        //저장할 값을 HashMap으로 저장
        val user: MutableMap<String, String> = mutableMapOf()
        user["email"] = email
        user["password"] = password

        //Collection 명은 "emailUsers"로 지정 [RDBMS의 테이블명 같은 역할]
        db.collection("emailUsers")
            .whereEqualTo("email", email)
            .get().addOnSuccessListener {
                if (it.documents.size > 0) {
                    AlertDialog.Builder(this)
                        .setMessage("중복된 이메일이 있습니다.\n 다시 확인하시기 바랍니다.")
                        .setPositiveButton("확인") { _, _ ->
                        }
                        .show()
                    binding.etEmail.requestFocus()
                    binding.etEmail.selectAll()
                } else {
                    db.collection("emailUsers").add(user).addOnSuccessListener {
                        AlertDialog.Builder(this)
                            .setMessage("축하합니다.\n 회원가입이 완료되었습니다.")
                            .setPositiveButton("확인"){ _, _ -> finish() }
                            .create().show()
                    }
                }
            }


    }


}