package com.w1866973.diceroller

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private var WINNING_SCORE: Int = 101
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dialog = Dialog(this)
    }


    fun showAboutDialog(view: View) {
        AboutDialog(this).show()
    }

    fun startNewGame(view: View){
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("winningScore", WINNING_SCORE)
        startActivity(intent)
    }



    fun showSetScoreDialog(view: View){
        dialog.setCanceledOnTouchOutside(false)
        dialog.setTitle("Set Score")
        dialog.setContentView(R.layout.set_score_dialog)
        dialog.show()
    }

    fun setScore(view: View){
        val enteredScore = dialog.findViewById<EditText>(R.id.txtScore).text.toString()
        if(enteredScore.trim().isNotEmpty()){
            WINNING_SCORE = Integer.parseInt(enteredScore)
        }
        dialog.dismiss()
    }

}