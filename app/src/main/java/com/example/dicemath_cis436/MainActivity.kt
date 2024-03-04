package com.example.dicemath_cis436

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import kotlin.random.Random

//constants
const val addSubRange = 100
const val mulRange = 21
const val winPoints = 20
const val jackpotStart = 5

val diceImages = listOf(
    0,
    R.drawable.dice1,
    R.drawable.dice2,
    R.drawable.dice3,
    R.drawable.dice4,
    R.drawable.dice5,
    R.drawable.dice6
)


//global variables
var currentPlayer = 1
var player1Points = 0
var player2Points = 0
var jackpot = jackpotStart
var correctAnswer = 0
var correctPoints = 0
var isRolled = false


//text updates
val textScope = MainScope()
var textJob: Job? = null

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //initialize text fields
        setPoints()
        setMessage(R.string.currentPlayer, currentPlayer)
        setGuessDisplay()

        //setup button handlers
        findViewById<Button>(R.id.rollButton).setOnClickListener { onRoll() }
        findViewById<Button>(R.id.guessButton).setOnClickListener { onGuess() }
    }

    //set/update point displays
    private fun setPoints() {
        findViewById<TextView>(R.id.player1Text).text = getString(R.string.P1Text, player1Points)
        findViewById<TextView>(R.id.player2Text).text = getString(R.string.P2Text, player2Points)
        findViewById<TextView>(R.id.jackpotText).text = getString(R.string.JackpotText, jackpot)
    }

    //set guess display
    private fun setGuessDisplay(num1: Int = 0, operation: String = "+", num2: Int = 0) {
        findViewById<TextView>(R.id.guessText).text = getString(R.string.GuessText, num1, operation, num2)
    }

    //set message function
    private fun setMessage(messageString: Int, param: Any = false) {
        if(param != false) findViewById<TextView>(R.id.messageText).text = getString(messageString, param)
        else findViewById<TextView>(R.id.messageText).text = getString(messageString)
    }

    //set text after time ms
    private fun textTimeout(time: Long, messageString: Int, param: Any = false) {
        textJob?.cancel()
        textJob = textScope.launch {
            delay(time)
            setMessage(messageString, param)
            setPoints()
        }
    }

    //set winner function
    private fun setWin() {
        setMessage(R.string.winText, currentPlayer)
        setPoints()

        currentPlayer = 1
        player1Points = 0
        player2Points = 0
        jackpot = jackpotStart
        correctAnswer = 0
        correctPoints = 0
        isRolled = false


        textTimeout(10000, R.string.currentPlayer, currentPlayer)

    }

    //action function
    private fun action(action: Int): Boolean {
        when(action) {
            1 -> {
                val num1 = Random.nextInt(addSubRange)
                val num2 = Random.nextInt(addSubRange)

                correctAnswer = num1 + num2
                correctPoints = 1

                setGuessDisplay(num1, "+", num2)
            }
            2 -> {
                val num1 = Random.nextInt(addSubRange)
                val num2 = Random.nextInt(addSubRange)

                correctAnswer = num1 - num2
                correctPoints = 2

                setGuessDisplay(num1, "-", num2)
            }
            3 -> {
                val num1 = Random.nextInt(mulRange)
                val num2 = Random.nextInt(mulRange)

                correctAnswer = num1 * num2
                correctPoints = 3

                setGuessDisplay(num1, "*", num2)
            }
            4 -> {
                setMessage(R.string.doublePoints)

                //re-roll question type for double points
                val actionType = Random.nextInt(1, 4)
                val result = action(actionType)

                if(result) correctPoints *= 2
            }
            5 -> {
                setMessage(R.string.loseTurn)

                currentPlayer = if(currentPlayer == 1) 2 else 1

                textTimeout(2000, R.string.currentPlayer, currentPlayer)

                return false
            }
            6 -> {
                setMessage(R.string.jackpot)

                //multiplication on jackpot
                val result = action(3)

                if(result) correctPoints = jackpot
            }
            else -> {
                print("No case for $action")
            }
        }
        return true
    }

    //onRoll function
    private fun onRoll() {
        //return if roll was not completed this turn
        if (isRolled) return

        //cancel pending text changes
        textJob?.cancel()

        setMessage(R.string.currentPlayer, currentPlayer)

        val roll = Random.nextInt(1, 7)

        //set dice image
        findViewById<ImageView>(R.id.diceImage).setImageResource(diceImages[roll])

        //perform action
        val result = action(roll)

        isRolled = result
    }

    //onGuess function
    private fun onGuess() {
        //return if roll has not been completed for this turn
        if(!isRolled) return

        //cancel pending text changes
        textJob?.cancel()

        try {
            val answer = findViewById<EditText>(R.id.guessBox).text.toString().toInt()

            if(answer == correctAnswer) {
                setMessage(R.string.correctAnswer)
                if(currentPlayer == 1) player1Points += correctPoints else player2Points += correctPoints
                if(correctPoints == jackpot) jackpot = jackpotStart

                //check for winner
                if(player1Points >= winPoints || player2Points >= winPoints) {setWin(); return }

            } else {
                setMessage(R.string.wrongAnswer)
                jackpot += correctPoints
            }
            setPoints()

            currentPlayer = if(currentPlayer == 1) 2 else 1
            isRolled = false


            textTimeout(2000, R.string.currentPlayer, currentPlayer)


        } catch (e: NumberFormatException) {
            print(e)
            setMessage(R.string.notInteger)
        }
    }
}