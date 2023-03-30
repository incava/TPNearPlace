package com.incava.tpnearplace.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.incava.tpnearplace.G
import com.incava.tpnearplace.R
import com.incava.tpnearplace.databinding.ActivityEmailSigninBinding
import com.incava.tpnearplace.databinding.ActivitySignupBinding
import com.incava.tpnearplace.model.UserAccount

class EmailSigninActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEmailSigninBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailSigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back)

        binding.btnSignin.setOnClickListener { clickSignIn() }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun clickSignIn(){
        var email : String = binding.etEmail.text.toString()
        var password : String = binding.etPassword.text.toString()

        //firebase Firestore DB에서 이메일과 패스워드 확인
        val db:FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("emailUsers")
            .whereEqualTo("email",email)
            .whereEqualTo("password",password)
            .get().addOnSuccessListener {
                if(it.documents.size > 0){
                    //로그인 성공!
                    // document 명.
                    var id:String = it.documents[0].id
                    G.userAccount = UserAccount(id,email)
                    val intent : Intent = Intent(this,MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }else{
                    AlertDialog.Builder(this)
                        .setMessage("이메일과 비밀번호를 다시 확인해주시기 바랍니다.")
                        .show()
                    binding.etEmail.requestFocus()
                    binding.etEmail.selectAll()
                }
            }
    }

}