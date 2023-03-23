package com.w1866973.diceroller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private var WINNING_SCORE: Int = 101
    private var humanWinCount: Int = 0
    private var computerWinCount: Int = 0
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if(data != null){
                    humanWinCount = data.getIntExtra("humanWinCount", 0)
                    computerWinCount = data.getIntExtra("computerWinCount", 0)
                }
            }
        }
    }


    fun showAboutDialog(view: View) {
        val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView : View = inflater.inflate(R.layout.about_dialog, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        popupWindow.animationStyle = R.style.PopUpAnimation
        popupWindow.isOutsideTouchable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        // Create an overlay view that covers the entire screen except for the popup window
        val parentView = window.decorView.rootView as ViewGroup
        val overlayView = View(this)
        overlayView.setBackgroundColor(Color.parseColor("#88000000"))
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        parentView.addView(overlayView, params)
        overlayView.setOnClickListener {
            popupWindow.dismiss()
            parentView.removeView(overlayView)
        }
    }

    fun startNewGame(view: View){
        val intent = Intent(this, GameActivity::class.java)
//        intent.putExtra("winningScore", WINNING_SCORE)
        intent.putExtra("humanWinCount", humanWinCount)
        intent.putExtra("computerWinCount", computerWinCount)
        resultLauncher.launch(intent)
    }

}