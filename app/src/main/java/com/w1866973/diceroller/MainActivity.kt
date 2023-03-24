//video link
//https://drive.google.com/file/d/1ZtRhimjLi9sBMh4xd95kWOkqT1_F5RCR/view?usp=sharing

package com.w1866973.diceroller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.PopupWindow
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private var winningScore: Int = 101
    private var humanWinCount: Int = 0
    private var computerWinCount: Int = 0
    private var difficulty: Difficulty = Difficulty.EASY
    private var isAboutShowing: Boolean = false
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //get data from GameActivity when it finishes
        //https://stackoverflow.com/questions/62671106/onactivityresult-method-is-deprecated-what-is-the-alternative
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data != null) {
                        humanWinCount = data.getIntExtra("humanWinCount", 0)
                        computerWinCount = data.getIntExtra("computerWinCount", 0)
                        winningScore = data.getIntExtra("winningScore", winningScore)
                        difficulty = Difficulty.valueOf(
                            data.getStringExtra("difficulty") ?: Difficulty.EASY.toString()
                        )
                    }
                }
            }

        //make status bar hide in landscape mode
        //https://stackoverflow.com/questions/11856886/hiding-title-bar-notification-bar-when-device-is-oriented-to-landscape
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        if (savedInstanceState != null) {
            humanWinCount = savedInstanceState.getInt("humanWinCount")
            computerWinCount = savedInstanceState.getInt("computerWinCount")
            winningScore = savedInstanceState.getInt("winningScore")
            isAboutShowing = savedInstanceState.getBoolean("isAboutShowing")
            difficulty = Difficulty.valueOf(savedInstanceState.getString("difficulty")!!)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("winningScore", winningScore)
        outState.putInt("humanWinCount", humanWinCount)
        outState.putInt("computerWinCount", computerWinCount)
        outState.putString("difficulty", difficulty.toString())
        outState.putBoolean("isAboutShowing", isAboutShowing)
    }

    //restore about dialog after screen rotates if it is open when screen rotates
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isAboutShowing) {
            showAboutDialog()
        }
    }

    fun onAboutButtonPressed(view: View) {
        showAboutDialog()
    }

    private fun showAboutDialog() {
        isAboutShowing = true

        //https://developer.android.com/reference/kotlin/android/widget/PopupWindow
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.about_dialog, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        popupWindow.animationStyle = R.style.PopUpAnimation //add pop open/close animation
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        // Create an overlay view that covers the entire screen except for the popup window
        // used to dim the background
        val parentView = window.decorView.rootView as ViewGroup
        val overlayView = View(this)
        overlayView.setBackgroundResource(R.color.dimmed_background)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        parentView.addView(overlayView, params)
        overlayView.setOnClickListener {

            //dismiss the popup-window and remove the overlay
            isAboutShowing = false
            popupWindow.dismiss()
            parentView.removeView(overlayView)
        }
    }

    fun startNewGame(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("winningScore", winningScore)
        intent.putExtra("humanWinCount", humanWinCount)
        intent.putExtra("computerWinCount", computerWinCount)
        intent.putExtra("difficulty", difficulty.toString())
        resultLauncher.launch(intent) //start new activity using resultLauncher to get the results back when finishing the activity
    }

}