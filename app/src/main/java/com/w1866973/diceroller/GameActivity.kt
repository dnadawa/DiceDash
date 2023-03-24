package com.w1866973.diceroller

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*

//https://www.baeldung.com/kotlin/enum
enum class Difficulty {
    EASY,
    HARD
}

enum class GameResult {
    HUMAN_WIN,
    COMPUTER_WIN,
    NO_ONE_WIN
}

class GameActivity : AppCompatActivity() {
    var humanScore: Int = 0
    var computerScore: Int = 0
    var humanThrowCount: Int = 0
    var computerThrowCount: Int = 0
    var round: Int = 1
    var humanDiceValues = arrayOf(0, 0, 0, 0, 0)
    var computerDiceValues = arrayOf(0, 0, 0, 0, 0)
    var isTie: Boolean = false
    var WINNING_MARK: Int = 101
    var humanWinCount: Int = 0
    var computerWinCount: Int = 0
    var difficulty: Difficulty = Difficulty.EASY
    var gameResult: GameResult = GameResult.NO_ONE_WIN

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
    lateinit var settingsButton: Button

    lateinit var roundLabel: TextView
    lateinit var humanWinsLabel: TextView
    lateinit var computerWinsLabel: TextView
    lateinit var humanScoreLabel: TextView
    lateinit var computerScoreLabel: TextView

    private lateinit var humanDice: Array<ImageView>
    private lateinit var computerDice: Array<ImageView>

    var popUpAlreadyShown: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        WINNING_MARK = intent.getIntExtra("winningScore", WINNING_MARK)
        humanWinCount = intent.getIntExtra("humanWinCount", 0)
        computerWinCount = intent.getIntExtra("computerWinCount", 0)
        difficulty =
            Difficulty.valueOf(intent.getStringExtra("difficulty") ?: Difficulty.EASY.toString())
        popUpAlreadyShown = false

        roundLabel = findViewById<TextView>(R.id.lblRound)
        humanWinsLabel = findViewById<TextView>(R.id.lblHumanWins)
        computerWinsLabel = findViewById<TextView>(R.id.lblComputerWins)

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
        settingsButton = findViewById<Button>(R.id.btnSettings)


        humanScoreLabel = findViewById<TextView>(R.id.lblHumScore)
        computerScoreLabel = findViewById<TextView>(R.id.lblCompScore)

        humanDice = arrayOf(humanDie1, humanDie2, humanDie3, humanDie4, humanDie5)
        computerDice =
            arrayOf(computerDie1, computerDie2, computerDie3, computerDie4, computerDie5)

        //custom back behaviour
        //https://stackoverflow.com/questions/58256210/how-to-make-custom-back-button-to-go-back-to-certain-destination-using-navigatio
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                intent.putExtra("humanWinCount", humanWinCount)
                intent.putExtra("computerWinCount", computerWinCount)
                intent.putExtra("winningScore", WINNING_MARK)
                intent.putExtra("difficulty", difficulty.toString())
                setResult(RESULT_OK, intent)
                finish()
            }
        })


        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        //set values
        if (savedInstanceState != null) {
            humanScore = savedInstanceState.getInt("humanScore")
            computerScore = savedInstanceState.getInt("computerScore")
            humanThrowCount = savedInstanceState.getInt("humanThrowCount")
            computerThrowCount = savedInstanceState.getInt("computerThrowCount")
            settingsButton.isEnabled = savedInstanceState.getBoolean("isSettingsButtonEnabled")
            scoreButton.isEnabled = savedInstanceState.getBoolean("isScoreButtonEnabled")
            isTie = savedInstanceState.getBoolean("isTie")
            round = savedInstanceState.getInt("round")
            humanWinCount = savedInstanceState.getInt("humanWinCount")
            computerWinCount = savedInstanceState.getInt("computerWinCount")
            gameResult = GameResult.valueOf(savedInstanceState.getString("gameResult")!!)
            difficulty = Difficulty.valueOf(savedInstanceState.getString("difficulty")!!)
            WINNING_MARK = savedInstanceState.getInt("WINNING_MARK")
            humanDiceValues = savedInstanceState.getIntArray("humanDiceValues")!!.toTypedArray()
            computerDiceValues =
                savedInstanceState.getIntArray("computerDiceValues")!!.toTypedArray()
            val fetchedHumanDice = savedInstanceState.getSerializable("humanDice")!! as Array<ImageView>

            for (i in fetchedHumanDice.indices) {
                humanDice[i].setImageURI((Uri.parse("android.resource://$packageName/drawable/die${humanDiceValues[i]}")))
                computerDice[i].setImageURI((Uri.parse("android.resource://$packageName/drawable/die${computerDiceValues[i]}")))

                humanDice[i].isSelected = fetchedHumanDice[i].isSelected
                changeDieColor(humanDice[i])
            }


        }
        humanScoreLabel.text = humanScore.toString()
        computerScoreLabel.text = computerScore.toString()
        roundLabel.text = round.toString()
        humanWinsLabel.text = humanWinCount.toString()
        computerWinsLabel.text = computerWinCount.toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("humanScore", humanScore)
        outState.putInt("computerScore", computerScore)
        outState.putInt("humanThrowCount", humanThrowCount)
        outState.putInt("computerThrowCount", computerThrowCount)
        outState.putBoolean("isSettingsButtonEnabled", settingsButton.isEnabled)
        outState.putBoolean("isScoreButtonEnabled", scoreButton.isEnabled)
        outState.putBoolean("isTie", isTie)
        outState.putInt("round", round)
        outState.putInt("humanWinCount", humanWinCount)
        outState.putInt("computerWinCount", computerWinCount)
        outState.putString("gameResult", gameResult.toString())
        outState.putInt("WINNING_MARK", WINNING_MARK)
        outState.putString("difficulty", difficulty.toString())
        outState.putIntArray("humanDiceValues", humanDiceValues.toIntArray())
        outState.putIntArray("computerDiceValues", computerDiceValues.toIntArray())
        outState.putSerializable("humanDice", humanDice)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !popUpAlreadyShown) {
            if (gameResult == GameResult.HUMAN_WIN) {
                showPopupWindow("You win!", ContextCompat.getColor(this, R.color.green))
            } else if (gameResult == GameResult.COMPUTER_WIN) {
                showPopupWindow("You lose!", ContextCompat.getColor(this, R.color.red))
            }
            popUpAlreadyShown = true
        }
    }

    fun throwDice(view: View) {
        settingsButton.isEnabled = false

        if (!isTie) {
            for (die in humanDice) {
                die.isClickable = true
            }

            scoreButton.isEnabled = true
        }

        humanThrowCount++

        throwSinglePartyDice(humanDice, humanDiceValues)
        if (humanThrowCount == 1) {
            println("Computer throwing")
            computerThrowCount++
            throwSinglePartyDice(computerDice, computerDiceValues)
        }



        if (humanThrowCount > 2 || isTie) {
            if (!isTie) {

                if (difficulty == Difficulty.HARD) {
                    computerHardStrategy()
                } else {
                    computerEasyStrategy()
                }
            }

            println("outside hard strategy")
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
        if (difficulty == Difficulty.HARD) {
            computerHardStrategy()
        } else {
            computerEasyStrategy()
        }

        calculateScore()
    }

    private fun computerEasyStrategy() {
        println("Entered into easy strategy")
        val rand = Random()

        val reRolls: Int = rand.nextInt(3)
        for (i in 0 until reRolls) {
            println("Rerolling")
            val numberOfDiceToKeep: Int = rand.nextInt(5)
            println("no of dice keeping: $numberOfDiceToKeep")
            var count: Int = 0
            val uniqueKeptDice = mutableSetOf<Int>()
            while (count < numberOfDiceToKeep) {
                var keepingDie = rand.nextInt(5)
                while (uniqueKeptDice.contains(keepingDie)) {
                    keepingDie = rand.nextInt(5)
                }
                uniqueKeptDice.add(keepingDie)
                println("keeping die: $keepingDie")
                computerDice[keepingDie].isSelected = true
                count++
            }
            throwSinglePartyDice(computerDice, computerDiceValues)

            //clearing all selected dice
            for (die in computerDice) {
                die.isSelected = false
            }
        }
    }

    private fun computerHardStrategy() {
        println("human : " + humanScore)
        println("computer : " + computerScore)

        var remainingReRolls: Int = 2
        while (remainingReRolls > 0) {
            println("Inside while remaining rerolls: $remainingReRolls")

            //calculate current roll of the computer
            var currentRollScore: Int = 0
            for (score in computerDiceValues) {
                println("Computer dices: $score")
                currentRollScore += score
            }
            println("Current round computer score: $currentRollScore")

            //determine threshold value
            val thresholdPercentage: Int = 20
            val thresholdValue: Int = WINNING_MARK * thresholdPercentage / 100
            println("thresholdValue $thresholdValue")

            if (computerScore + currentRollScore >= WINNING_MARK - thresholdValue) {
                println("Computer is closer to the target score")
                val endRound = keepOrReRoll(arrayOf(4, 5, 6))
                if (endRound) {
                    println("end of round")
                    break
                }
            } else {
                println("Computer is NOT closer to the target score")
                if (humanScore + 18 > computerScore + currentRollScore) {
                    println("human wining")
                    val endRound = keepOrReRoll(arrayOf(4, 5, 6))
                    if (endRound) {
                        println("end of round")
                        break
                    }
                } else {
                    println("Computer wining")
                    val endRound = keepOrReRoll(arrayOf(3, 4, 5, 6))
                    if (endRound) {
                        println("end of round")
                        break
                    }
                }
            }
            remainingReRolls--
        }
        println("While end")
    }

    private fun keepOrReRoll(keepingValues: Array<Int>): Boolean {
        var isReRoll: Boolean = false
        for (i in 0..4) {
            if (computerDiceValues[i] in keepingValues) {
                println("die face ${computerDiceValues[i]} is keeping")
                computerDice[i].isSelected = true
            } else {
                println("die face ${computerDiceValues[i]} is NOT keeping")
                isReRoll = true
            }
        }

        if (isReRoll) {
            println("Rerolling")
            throwSinglePartyDice(computerDice, computerDiceValues)
            return false
        } else {
            println("not rerolling")
            return true
        }
    }

    private fun calculateScore() {
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
        for (die in computerDice) {
            die.isSelected = false
        }


        if (humanScore >= WINNING_MARK) {
            if (humanScore > computerScore) {
                humanWinCount++
                humanWinsLabel.text = humanWinCount.toString()
                gameResult = GameResult.HUMAN_WIN
                showPopupWindow("You win!", ContextCompat.getColor(this, R.color.green))
            } else if (computerScore > humanScore) {
                computerWinCount++
                computerWinsLabel.text = computerWinCount.toString()
                gameResult = GameResult.COMPUTER_WIN
                showPopupWindow("You lose!", ContextCompat.getColor(this, R.color.red))
            } else {
                isTie = true
            }
        } else if (computerScore >= WINNING_MARK) {
            computerWinCount++
            computerWinsLabel.text = computerWinCount.toString()
            gameResult = GameResult.COMPUTER_WIN
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
                ContextCompat.getColor(this, R.color.primary),
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
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.game_result_dialog, null)

        val messageTextView = popupView.findViewById<TextView>(R.id.lblWinningStatus)
        messageTextView.text = message
        messageTextView.setTextColor(textColor)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        popupWindow.animationStyle = R.style.PopUpAnimation
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        // Create an overlay view that covers the entire screen except for the popup window
        val parentView = window.decorView.rootView as ViewGroup
        val overlayView = View(this)
        overlayView.setBackgroundResource(R.color.dimmed_background)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        parentView.addView(overlayView, params)
        overlayView.setOnClickListener {}
    }

    fun showSettingsDialog(view: View) {
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.settings_dialog, null)
        val textField: EditText = popupView.findViewById<EditText>(R.id.txtScore);

        if (WINNING_MARK != 101) {
            textField.setText(WINNING_MARK.toString())
        }

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        popupWindow.animationStyle = R.style.PopUpAnimation
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        //spinner
        //https://www.geeksforgeeks.org/spinner-in-android-using-java-with-example/
        val spinner: Spinner = popupView.findViewById(R.id.difficulty)

        val adapter: ArrayAdapter<Difficulty> =
            ArrayAdapter<Difficulty>(
                this,
                R.layout.spinner_item,
                Difficulty.values()
            )
        adapter.setDropDownViewResource(R.layout.spinner_item)
        spinner.adapter = adapter
        spinner.setSelection(adapter.getPosition(difficulty))

        popupView.findViewById<Button>(R.id.btnSave).setOnClickListener {
            difficulty = spinner.selectedItem as Difficulty
            val enteredScore = textField.text.toString()
            if (enteredScore.trim().isNotEmpty()) {
                WINNING_MARK = Integer.parseInt(enteredScore)
            }
            popupWindow.dismiss()
        }
    }
}