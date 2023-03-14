package com.w1866973.diceroller

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*


class GameActivity : AppCompatActivity() {
    var humanScore: Int = 0
    var computerScore: Int = 0
    var humanThrowCount: Int = 0
    var computerThrowCount: Int = 0
    var round: Int = 1
    val humanDiceValues = arrayOf(0, 0, 0, 0, 0)
    val computerDiceValues = arrayOf(0, 0, 0, 0, 0)
    var isTie: Boolean = false
    var WINNING_MARK: Int = 101
    var humanWinCount: Int = 0
    var computerWinCount: Int = 0

    lateinit var humanDie1: ImageView
    lateinit var humanDie2: ImageView
    lateinit var humanDie3: ImageView
    lateinit var humanDie4: ImageView
    lateinit var humanDie5: ImageView

    lateinit var computerDie1: ImageView
    lateinit var computerDie2: ImageView
    lateinit var computerDie3: ImageView
    lateinit var computerDie4: ImageView
    lateinit var computerDie5: ImageView

    lateinit var scoreButton: Button
    lateinit var throwButton: Button

    lateinit var roundLabel: TextView
    lateinit var humanWinsLabel: TextView
    lateinit var computerWinsLabel: TextView

    private lateinit var humanDice: Array<ImageView>
    private lateinit var computerDice: Array<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        WINNING_MARK = intent.getIntExtra("winningScore", WINNING_MARK)
        humanWinCount = intent.getIntExtra("humanWinCount", 0)
        computerWinCount = intent.getIntExtra("computerWinCount", 0)

        roundLabel = findViewById<TextView>(R.id.lblRound)
        humanWinsLabel = findViewById<TextView>(R.id.lblHumanWins)
        computerWinsLabel = findViewById<TextView>(R.id.lblComputerWins)

        humanWinsLabel.text = humanWinCount.toString()
        computerWinsLabel.text = computerWinCount.toString()

        humanDie1 = findViewById<ImageView>(R.id.humDie1)
        humanDie2 = findViewById<ImageView>(R.id.humDie2)
        humanDie3 = findViewById<ImageView>(R.id.humDie3)
        humanDie4 = findViewById<ImageView>(R.id.humDie4)
        humanDie5 = findViewById<ImageView>(R.id.humDie5)

        computerDie1 = findViewById<ImageView>(R.id.compDie1)
        computerDie2 = findViewById<ImageView>(R.id.compDie2)
        computerDie3 = findViewById<ImageView>(R.id.compDie3)
        computerDie4 = findViewById<ImageView>(R.id.compDie4)
        computerDie5 = findViewById<ImageView>(R.id.compDie5)

        scoreButton = findViewById<Button>(R.id.btnScore)
        throwButton = findViewById<Button>(R.id.btnThrow)

        humanDice = arrayOf(humanDie1, humanDie2, humanDie3, humanDie4, humanDie5)
        computerDice =
            arrayOf(computerDie1, computerDie2, computerDie3, computerDie4, computerDie5)
    }

    fun throwDice(view: View) {
        if(!isTie){
            for (die in humanDice) {
                die.isClickable = true
            }

            scoreButton.isEnabled = true
            throwButton.text = "Rethrow"
        }

        humanThrowCount++

        throwSinglePartyDice(humanDice, humanDiceValues)
        if (humanThrowCount == 1) {
            computerThrowCount++
            throwSinglePartyDice(computerDice, computerDiceValues)
        }



        if (humanThrowCount > 2 || isTie) {
            //sample computer strategy
            val rand = Random()
            //decide re-roll or not
            if (rand.nextInt(10) < 5) {
                computerRethrow()
            }
            if (rand.nextInt(10) < 5) {
                computerRethrow()
            }

            calculateScore()
        }
    }

    private fun throwSinglePartyDice(dice: Array<ImageView>, diceValues: Array<Int>) {
        val rnd: Random = Random()
        var count = 0
        while (count < 5) {
            val dieValue = rnd.nextInt(6) + 1

            if (!dice[count].isSelected) {
                dice[count].setImageURI((Uri.parse("android.resource://$packageName/drawable/die$dieValue")))
                diceValues[count] = dieValue
            }
            count++
        }
    }

    fun onScoreButtonPressed(view: View) {
        //sample computer strategy
        val rand = Random()
        //decide re-roll or not
        if (rand.nextInt(10) < 5) {
            computerRethrow()
        }
        if (rand.nextInt(10) < 5) {
            computerRethrow()
        }

        calculateScore()
    }

    private fun computerRethrow() {
        val rand = Random()

        //decide keep a die or not
        if (rand.nextInt(10) < 5) {
            val keepingDie = rand.nextInt(5)
            computerDice[keepingDie].isSelected = true
        }

        throwSinglePartyDice(computerDice, computerDiceValues)
    }

    private fun calculateScore() {
        val scoreButton = findViewById<Button>(R.id.btnScore)
        val throwButton = findViewById<Button>(R.id.btnThrow)

        val humanScoreLabel = findViewById<TextView>(R.id.lblHumScore)
        val computerScoreLabel = findViewById<TextView>(R.id.lblCompScore)

        throwButton.text = "Throw"
        scoreButton.isEnabled = false

        for (n in humanDiceValues) {
            humanScore += n
        }
        for (n in computerDiceValues) {
            computerScore += n
        }

        humanScoreLabel.text = humanScore.toString()
        computerScoreLabel.text = computerScore.toString()

        humanThrowCount = 0
        computerThrowCount = 0

        for (die in humanDice) {
            die.isSelected = false
            die.isClickable = false
            changeDieColor(die)
        }

        if (humanScore >= WINNING_MARK && computerScore >= WINNING_MARK) {
            if (humanScore > computerScore) {
                humanWinCount++
                showPopupWindow("You win!", ContextCompat.getColor(this, R.color.green))
            } else if (computerScore > humanScore) {
                computerWinCount++
                showPopupWindow("You lose!", ContextCompat.getColor(this, R.color.red))
            } else {
                isTie = true
            }
        } else if (humanScore >= WINNING_MARK) {
            humanWinCount++
            showPopupWindow("You win!", ContextCompat.getColor(this, R.color.green))
        } else if (computerScore >= WINNING_MARK) {
            computerWinCount++
            showPopupWindow("You lose!", ContextCompat.getColor(this, R.color.red))
        } else {
            round++
            roundLabel.text = round.toString()
        }
    }

    fun toggleSelected(view: View) {
        val imageView = findViewById<ImageView>(view.id)
        view.isSelected = !view.isSelected
        changeDieColor(imageView)
    }

    private fun changeDieColor(imageView: ImageView) {
        if (imageView.isSelected) {
            imageView.setColorFilter(
                ContextCompat.getColor(this, R.color.purple_700),
                android.graphics.PorterDuff.Mode.ADD
            )
        } else {
            imageView.setColorFilter(
                ContextCompat.getColor(this, R.color.black),
                android.graphics.PorterDuff.Mode.ADD
            )
        }
    }

    private fun showPopupWindow(message: String, textColor: Int) {
        val dialog = Dialog(this)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setContentView(R.layout.game_result_dialog)
        dialog.setOnCancelListener {
            intent.putExtra("humanWinCount", humanWinCount)
            intent.putExtra("computerWinCount", computerWinCount)
            setResult(RESULT_OK, intent)
            finish()
        }

        val messageTextView = dialog.findViewById<TextView>(R.id.lblWinningStatus)
        messageTextView.text = message
        messageTextView.setTextColor(textColor)

        dialog.show()
    }
}