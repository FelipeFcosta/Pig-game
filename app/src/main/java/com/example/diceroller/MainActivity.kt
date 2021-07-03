package com.example.diceroller

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 * This activity allows the user to roll a dice and view the result
 * on the screen
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val PLAYER_A: Int = 0
        private const val PLAYER_B: Int = 1
    }

    private var playerWon: Boolean = false
    // my way of removing duplicate code
    private var sum: Array<Int> = arrayOf(0, 0)
    private var score: Array<Int> = arrayOf(0, 0)
    private lateinit var resBtnBanks: Array<Int>
    private lateinit var resBtnRolls: Array<Int>
    private lateinit var resTxtSums: Array<Int>
    private lateinit var resTxtScores: Array<Int>

    override fun onCreate(savedInstanceState: Bundle?) {    // entry point
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resBtnBanks = arrayOf(R.id.btnBankA, R.id.btnBankB)
        resBtnRolls = arrayOf(R.id.btnRollA, R.id.btnRollB)
        resTxtSums = arrayOf(R.id.txtSomaA, R.id.txtSomaB)
        resTxtScores = arrayOf(R.id.txtScoreA, R.id.txtScoreB)

        val rollButtonA: Button = findViewById(resBtnRolls[0])
        val rollButtonB: Button = findViewById(resBtnRolls[1])
        val btnBankA: Button = findViewById(resBtnBanks[0])
        val btnBankB: Button = findViewById(resBtnBanks[1])

        rollButtonA.setOnClickListener { onClick(rollButtonA) }
        rollButtonB.setOnClickListener { onClick(rollButtonB) }
        btnBankA.setOnClickListener { onClick(btnBankA) }
        btnBankB.setOnClickListener { onClick(btnBankB) }

        // start with player A
        invertBtnState(PLAYER_B)

        // start with a random roll
        rollDice()
    }

    /**
     * Implement listener for all the buttons
     */
    override fun onClick(v: View) {
        when(v.id) {
            R.id.btnRollA -> rollPlay(PLAYER_A)
            R.id.btnRollB -> rollPlay(PLAYER_B)
            R.id.btnBankA -> bankPlay(PLAYER_A)
            R.id.btnBankB -> bankPlay(PLAYER_B)
        }
    }

    /**
     * Roll the dice and update the screen with the result.
     */
    private fun rollDice(): Int {
        // Create new Dice object with 6 sides and roll the dice
        val dice = Dice(6)
        val diceRoll = dice.roll()

        // Find the ImageView in the layout
        val diceImage: ImageView = findViewById(R.id.imageView)

        // Determine which drawable resource ID to use based on the dice roll
        val drawableResource = when (diceRoll) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }

        // Update the ImageView with the correct drawable resource ID
        diceImage.setImageResource(drawableResource)

        // Update the content description
        diceImage.contentDescription = "dice rolled a $diceRoll"
        return diceRoll
    }

    /**
     * updates the score of a player in the current round
     */
    private fun updateSum(value: Int, resource: Int) {
        val textSum: TextView = findViewById(resource)
        textSum.text = value.toString()
    }

    /**
     * Real score of a player
     */
    private fun updateScore(value: Int, resource: Int) {
        val txtScore: TextView = findViewById(resource)
        txtScore.text = value.toString()
    }

    /**
     * What happens when a player clicks the 'Roll' button
     */
    private fun rollPlay(PLAYER: Int) {
        val diceRoll = rollDice()

        // restart scores if last game ended
        if (playerWon) {
            restartScores()
            playerWon = false
        }

        // Add dice result or pass the turn
        if (diceRoll != 1) { sum[PLAYER] += diceRoll }
        else {
            sum[PLAYER] = 0
            invertBtnState(PLAYER)
        }

        updateSum(sum[PLAYER], resTxtSums[PLAYER])
    }

    /**
     * What happens when a player clicks the 'Bank' button
     */
    private fun bankPlay(PLAYER: Int) {
        // Add sum value to the score and update the screen
        score[PLAYER] += sum[PLAYER]
        updateScore(score[PLAYER], resTxtScores[PLAYER])

        // restart scores if last game ended
        if (playerWon) {
            restartScores()
            playerWon = false
        }

        // restart sum
        sum[PLAYER] = 0
        updateSum(sum[PLAYER], resTxtSums[PLAYER])

        // check if a player has won
        if (score[PLAYER] >= 100) {
            playerWon = true
            val txtScore: TextView = findViewById(resTxtScores[PLAYER])
            txtScore.setTextColor(ContextCompat.getColor(this, R.color.green_win))
        }

        // pass the turn
        invertBtnState(PLAYER)
    }

    /**
     * toggles the specified button
     */
    private fun toggleButton(resId: Int, enabled: Boolean) {
        val button: Button = findViewById(resId)
        button.isEnabled = enabled
        button.isClickable = enabled
    }

    /**
     * disable current player buttons and enable next player buttons
     */
    private fun invertBtnState(PLAYER: Int) {
        toggleButton(resBtnRolls[PLAYER], false)
        toggleButton(resBtnBanks[PLAYER], false)
        toggleButton(resBtnRolls[1-PLAYER], true)
        toggleButton(resBtnBanks[1-PLAYER], true)
    }

    /**
     * restart game after a player has won
     */
    private fun restartScores() {
        for (player in 0..1) {
            score[player] = 0
            updateScore(score[player], resTxtScores[player])
            
            // return any green colored points to the original color
            val txtScore: TextView = findViewById(resTxtScores[player])
            txtScore.setTextColor(getColorFromAttr(R.attr.colorPrimaryText))
        }
    }

    /**
     * Retrieve color of attribute used in the current theme
     */
    @ColorInt
    private fun getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue()
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, true)
        return typedValue.data
    }
}

/**
 * Dice with a fixed number of sides
 */
class Dice(private val numSides: Int) {
    fun roll(): Int {
        return (1..numSides).random()
    }
}