package com.w1866973.diceroller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    fun showAboutDialog(view: View) {
        AboutDialog(this).show()
    }

    fun startNewGame(view: View){
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

}