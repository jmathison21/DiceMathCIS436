package com.example.dicemath_cis436

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

//constants
const val addSubRange = 100
const val mulRange = 21
const val winPoints = 20
const val jackpotStart = 5

//global variables
var currentPlayer = 1
var player1Points = 0
var player2Points = 0
var jackpot = jackpotStart

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}