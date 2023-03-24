package com.w1866973.diceroller

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity


class SplashScreenActivity : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    var progressValue: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        Thread {
            doWork()
            startApp()
            finish()
        }.start()

        //https://stackoverflow.com/questions/11856886/hiding-title-bar-notification-bar-when-device-is-oriented-to-landscape
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        if(savedInstanceState != null){
            progressValue = savedInstanceState.getInt("progress", 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("progress", progressValue)
    }

    private fun doWork() {
        while (progressValue < 100) {
            try {
                Thread.sleep(30)
                progressBar.progress = progressValue
            } catch (e: Exception) {
                e.printStackTrace()
            }
            progressValue += 1
        }
    }

    private fun startApp() {
        val i = Intent(this@SplashScreenActivity, MainActivity::class.java)
        startActivity(i)
    }
}