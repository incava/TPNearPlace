package com.incava.tpnearplace.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.incava.tpnearplace.R
import com.incava.tpnearplace.databinding.ActivityMainBinding
import com.kakao.sdk.user.UserApiClient

class MainActivity : AppCompatActivity() {
    val binding : ActivityMainBinding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_aa -> Toast.makeText(this,"aa",Toast.LENGTH_SHORT).show()
            R.id.menu_bb -> Toast.makeText(this, "bb",Toast.LENGTH_SHORT).show()
        }

        return super.onOptionsItemSelected(item)
    }

}