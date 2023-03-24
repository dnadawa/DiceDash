package com.w1866973.diceroller

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
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
    private var humanScore: Int = 0
    private var computerScore: Int = 0
    private var humanThrowCount: Int = 0
    private var computerThrowCount: Int = 0
    private var round: Int = 1
    private var humanDiceValues = arrayOf(0, 0, 0, 0, 0)
    private var computerDiceValues = arrayOf(0, 0, 0, 0, 0)
    private var isTie: Boolean = false
    private var winingMark: Int = 101
    private var humanWinCount: Int = 0
    private var computerWinCount: Int = 0
    private var difficulty: Difficulty = Difficulty.EASY
    private var gameResult: GameResult = GameResult.NO_ONE_WIN

    private lateinit var humanDie1: ImageView
    private lateinit var humanDie2: ImageView
    private lateinit var humanDie3: ImageView
    private lateinit var humanDie4: ImageView
    private lateinit var humanDie5: ImageView

    private lateinit var computerDie1: ImageView
    private lateinit var computerDie2: ImageView
    private lateinit var computerDie3: ImageView
    private lateinit var computerDie4: ImageView
    private lateinit var computerDie5: ImageView

    private lateinit var scoreButton: Button
    private lateinit var throwButton: Button
    private lateinit var settingsButton: Button

    private lateinit var roundLabel: TextView
    private lateinit var humanWinsLabel: TextView
    private lateinit var computerWinsLabel: TextView
    private lateinit var humanScoreLabel: TextView
    private lateinit var computerScoreLabel: TextView

    private lateinit var humanDice: Array<ImageView>
    private lateinit var computerDice: Array<ImageView>

    //constants
    private val AVERAGE_SCORE_FOR_A_ROUND: Int = 18
    private val THRESHOLD_PERCENTAGE: Int = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        //get data from previous activity
        winingMark = intent.getIntExtra("winningScore", winingMark)
        humanWinCount = intent.getIntExtra("humanWinCount", 0)
        computerWinCount = intent.getIntExtra("computerWinCount", 0)
        difficulty =
            Difficulty.valueOf(intent.getStringExtra("difficulty") ?: Difficulty.EASY.toString())

        roundLabel = findViewById(R.id.lblRound)
        humanWinsLabel = findViewById(R.id.lblHumanWins)
        computerWinsLabel = findViewById(R.id.lblComputerWins)

        humanDie1 = findViewById(R.id.humDie1)
        humanDie2 = findViewById(R.id.humDie2)
        humanDie3 = findViewById(R.id.humDie3)
        humanDie4 = findViewById(R.id.humDie4)
        humanDie5 = findViewById(R.id.humDie5)

        computerDie1 = findViewById(R.id.compDie1)
        computerDie2 = findViewById(R.id.compDie2)
        computerDie3 = findViewById(R.id.compDie3)
        computerDie4 = findViewById(R.id.compDie4)
        computerDie5 = findViewById(R.id.compDie5)

        scoreButton = findViewById(R.id.btnScore)
        throwButton = findViewById(R.id.btnThrow)
        settingsButton = findViewById(R.id.btnSettings)

        humanScoreLabel = findViewById(R.id.lblHumScore)
        computerScoreLabel = findViewById(R.id.lblCompScore)

        humanDice = arrayOf(humanDie1, humanDie2, humanDie3, humanDie4, humanDie5)
        computerDice =
            arrayOf(computerDie1, computerDie2, computerDie3, computerDie4, computerDie5)

        //set intent extra when back button pressed and finish this activity
        //https://stackoverflow.com/questions/58256210/how-to-make-custom-back-button-to-go-back-to-certain-destination-using-navigatio
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                intent.putExtra("humanWinCount", humanWinCount)
                intent.putExtra("computerWinCount", computerWinCount)
                intent.putExtra("winningScore", winingMark)
                intent.putExtra("difficulty", difficulty.toString())
                setResult(RESULT_OK, intent)
                finish()
            }
        })

        //make status bar hide in landscape mode
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        //restore values when configuration changes
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
            winingMark = savedInstanceState.getInt("WINNING_MARK")
            humanDiceValues = savedInstanceState.getIntArray("humanDiceValues")!!.toTypedArray()
            computerDiceValues =
                savedInstanceState.getIntArray("computerDiceValues")!!.toTypedArray()
            val fetchedHumanDice = savedInstanceState.getSerializable("humanDice")!! as Array<ImageView>

            //set dice face images and selected dice
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

    //save data when configuration changes
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
        outState.putInt("WINNING_MARK", winingMark)
        outState.putString("difficulty", difficulty.toString())
        outState.putIntArray("humanDiceValues", humanDiceValues.toIntArray())
        outState.putIntArray("computerDiceValues", computerDiceValues.toIntArray())
        outState.putSerializable("humanDice", humanDice)
    }

    //restore you win/lose dialog if screen rotate while the dialog open
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (gameResult == GameResult.HUMAN_WIN) {
            showResultWindow("You win!", ContextCompat.getColor(this, R.color.green))
        } else if (gameResult == GameResult.COMPUTER_WIN) {
            showResultWindow("You lose!", ContextCompat.getColor(this, R.color.red))
        }
    }

    fun onThrowButtonPressed(view: View) {
        settingsButton.isEnabled = false

        if (!isTie) {
            for (die in humanDice) {
                die.isClickable = true
            }
            scoreButton.isEnabled = true
        }

        //throw human dice
        humanThrowCount++
        throwDiceForSinglePlayer(humanDice, humanDiceValues)

        //initial throw of the computer
        if (humanThrowCount == 1) {
            computerThrowCount++
            throwDiceForSinglePlayer(computerDice, computerDiceValues)
        }

        //when round ends, play computer strategy and calculate store
        if (humanThrowCount > 2 || isTie) {
            endRound()
        }
    }

    fun onScoreButtonPressed(view: View) {
        endRound()
    }

    fun toggleSelected(view: View) {
        val imageView = findViewById<ImageView>(view.id)
        view.isSelected = !view.isSelected
        changeDieColor(imageView)
    }

    fun onSettingButtonPressed(view: View){
        showSettingsWindow()
    }

    private fun throwDiceForSinglePlayer(dice: Array<ImageView>, diceValues: Array<Int>) {
        val rnd: Random = Random()
        var count = 0

        //set random die faces for each die
        while (count < dice.size) {
            val dieValue = rnd.nextInt(6) + 1

            if (!dice[count].isSelected) {
                dice[count].setImageURI((Uri.parse("android.resource://$packageName/drawable/die$dieValue")))
                diceValues[count] = dieValue
            }
            count++
        }
    }

    private fun endRound() {
        if (!isTie) {
            if (difficulty == Difficulty.HARD) {
                computerHardStrategy()
            } else {
                computerEasyStrategy()
            }
        }

        calculateScore()
    }

    private fun computerEasyStrategy() {
        val rand = Random()

        val reRolls: Int = rand.nextInt(3) //check how many rolls that the computer roll
        for (i in 0 until reRolls) {
            val numberOfDiceToKeep: Int = rand.nextInt(5)

            var count: Int = 0
            val uniqueKeptDice = mutableSetOf<Int>()
            while (count < numberOfDiceToKeep) {
                var keepingDie = rand.nextInt(5)
                while (uniqueKeptDice.contains(keepingDie)) {
                    keepingDie = rand.nextInt(5)
                }
                uniqueKeptDice.add(keepingDie)
                computerDice[keepingDie].isSelected = true
                count++
            }
            throwDiceForSinglePlayer(computerDice, computerDiceValues)

            //clearing all selected dice
            for (die in computerDice) {
                die.isSelected = false
            }
        }
    }

    private fun computerHardStrategy() {
        //The computer strategy consider the human score so far, the target score, computer score
        //and the current roll of the computer.
        //First, the computer calculate the score of its current roll.
        //Then, it calculate the threshold value using the targetScore.
        //If the computer score(including this round) is close to the target score it will keep the 4,5,6 dice and re-roll the rest.
        //Otherwise, it consider human score.
        //  -   Calculate the human score (including this round) by adding the average score per round to the human score so far
        //  -   average is calculated by (maximumScorePossible + minScorePossible)/2 = (30 + 5)/2 = 17.5 round to 18.
        //  -   Check if current roll and computer score is less than the calculated human score
        //  -   if human is in lead, keep 4,5,6 dice and re-roll rest
        //  -   if computer is in lead, keep 3,4,5,6 dice and re-roll rest
        //The computer is playing both the optional re-rolls but the re-rolling dice are different in this algorithm.

        //ADVANTAGES
        // - The algorithm considers both the computer's and the human's scores to make optimal decisions.
        // - The algorithm uses both of computer re-rolls to maximize the score.

        //DISADVANTAGES
        // - Human player's current roll score is taken as the average, which may be not be optimum.

        var remainingReRolls: Int = 2
        while (remainingReRolls > 0) {

            //calculate current roll of the computer
            var currentRollScore: Int = 0
            for (score in computerDiceValues) {
                currentRollScore += score
            }

            //determine threshold value that is used to check whether the current score
            // is close to the winning score
            val thresholdValue: Int = winingMark * THRESHOLD_PERCENTAGE / 100

            //keep only 4,5,6 if computer is close to the winning mark
            if (computerScore + currentRollScore >= winingMark - thresholdValue) {
                val endRound = keepOrReRoll(arrayOf(4, 5, 6))
                if (endRound) {
                    break
                }
            } else {
                //add the averageScore for the round to the human and compare scores
                if (humanScore + AVERAGE_SCORE_FOR_A_ROUND > computerScore + currentRollScore) {
                    val endRound = keepOrReRoll(arrayOf(4, 5, 6)) //keep only 4,5,6 if human is in lead
                    if (endRound) {
                        break
                    }
                } else {
                    val endRound = keepOrReRoll(arrayOf(3, 4, 5, 6))//keep only 3,4,5,6 if computer is in lead
                    if (endRound) {
                        break
                    }
                }
            }
            remainingReRolls--
        }
    }

    private fun keepOrReRoll(keepingValues: Array<Int>): Boolean {
        var isReRoll: Boolean = false
        //select keeping dice
        for (i in computerDice.indices) {
            if (computerDiceValues[i] in keepingValues) {
                computerDice[i].isSelected = true
            } else {
                isReRoll = true
            }
        }

        //re-roll if all dice are not keeping
        if (isReRoll) {
            throwDiceForSinglePlayer(computerDice, computerDiceValues)
            return false
        } else {
            return true
        }
    }

    private fun calculateTotal(values: Array<Int>): Int {
        var total = 0
        for (n in values) {
            total += n
        }

        return total
    }

    private fun calculateScore() {
        scoreButton.isEnabled = false

        //calculate scores
        humanScore += calculateTotal(humanDiceValues)
        computerScore += calculateTotal(computerDiceValues)

        //setting scores
        humanScoreLabel.text = humanScore.toString()
        computerScoreLabel.text = computerScore.toString()

        //resetting re-roll count
        humanThrowCount = 0
        computerThrowCount = 0

        //reset dice
        for (die in humanDice) {
            die.isSelected = false
            die.isClickable = false
            changeDieColor(die)
        }
        for (die in computerDice) {
            die.isSelected = false
        }

        //determine the winner
        if (humanScore >= winingMark) {
            if (humanScore > computerScore) {
                gameResult = GameResult.HUMAN_WIN
                processWin(gameResult)
            } else if (computerScore > humanScore) {
                gameResult = GameResult.COMPUTER_WIN
                processWin(gameResult)
            } else {
                isTie = true
            }
        } else if (computerScore >= winingMark) {
            gameResult = GameResult.COMPUTER_WIN
            processWin(gameResult)
        } else {
            round++
            roundLabel.text = round.toString()
        }
    }

    private fun processWin(gameResult: GameResult){
        if(gameResult == GameResult.HUMAN_WIN){
            humanWinCount++
            humanWinsLabel.text = humanWinCount.toString()
            showResultWindow("You win!", ContextCompat.getColor(this, R.color.green))
        } else if (gameResult == GameResult.COMPUTER_WIN){
            computerWinCount++
            computerWinsLabel.text = computerWinCount.toString()
            showResultWindow("You lose!", ContextCompat.getColor(this, R.color.red))
        }
    }

    private fun changeDieColor(imageView: ImageView) {
        //set selected color in die
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

    private fun showResultWindow(message: String, textColor: Int) {
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.game_result_dialog, null)

        //set the text and color
        val textView = popupView.findViewById<TextView>(R.id.lblWinningStatus)
        textView.text = message
        textView.setTextColor(textColor)

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

    private fun showSettingsWindow() {
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.settings_dialog, null)

        //set edit text value if it is set
        val textField: EditText = popupView.findViewById(R.id.txtScore);
        if (winingMark != 101) {
            textField.setText(winingMark.toString())
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

        //on save button pressed
        popupView.findViewById<Button>(R.id.btnSave).setOnClickListener {
            difficulty = spinner.selectedItem as Difficulty
            val enteredScore = textField.text.toString()
            if (enteredScore.trim().isNotEmpty()) {
                winingMark = Integer.parseInt(enteredScore)
            }
            popupWindow.dismiss()
        }
    }
}