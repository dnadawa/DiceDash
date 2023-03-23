package com.w1866973.diceroller

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity


class SplashScreenActivity : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        Thread {
            doWork()
            startApp()
            finish()
        }.start()

//        Handler().postDelayed(Runnable {
//            val i = Intent(this@SplashScreenActivity, MainActivity::class.java)
//            startActivity(i)
//            finish()
//        }, 5000)
    }

    private fun doWork() {
        var progress = 0
        while (progress < 100) {
            try {
                Thread.sleep(30)
                progressBar.progress = progress
            } catch (e: Exception) {
                e.printStackTrace()
            }
            progress += 1
        }
    }

    private fun startApp() {
        val i = Intent(this@SplashScreenActivity, MainActivity::class.java)
        startActivity(i)
    }
}