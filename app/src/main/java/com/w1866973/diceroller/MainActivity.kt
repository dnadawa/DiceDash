package com.w1866973.diceroller

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    private var WINNING_SCORE: Int = 101
    private var humanWinCount: Int = 0
    private var computerWinCount: Int = 0
    lateinit var dialog: Dialog
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dialog = Dialog(this)


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
        AboutDialog(this).show()
    }

    fun startNewGame(view: View){
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("winningScore", WINNING_SCORE)
        intent.putExtra("humanWinCount", humanWinCount)
        intent.putExtra("computerWinCount", computerWinCount)
        resultLauncher.launch(intent)
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