/*
Author: Gabby Pierce
Date: February 26 2024
Project: Connect 4 app
 */
package com.hfad.assignment2

import android.os.Bundle
import android.graphics.Color
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import kotlin.random.Random
import android.widget.TextView
import android.widget.Button


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var param1: String? = null
private var param2: String? = null
private const val COLOR_BLUE = Color.BLUE
private const val COLOR_RED = Color.RED


/**
 * A simple [Fragment] subclass.
 * Use the [PlayBoard.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayBoard : Fragment() {
    private val board: Array<Array<ImageButton?>> = Array(6) { arrayOfNulls<ImageButton>(6) }
    private val boardState: Array<Array<Int>> = Array(6) { Array(6) { 0 } }
    private var currentPlayer: Int = 0 // 0 for player, 1 for AI
    private var winnerTextView: TextView? = null
    private val userSelectedSquares = mutableSetOf<Int>()
    private val computerSelectedSquares = mutableSetOf<Int>()
    private val selectedSquares = mutableSetOf<Int>()
    private var gameActive = true // Flag to track if the game is active
    private var playerTurn = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val view = inflater.inflate(R.layout.fragment_play_board, container, false)
        initializeBoard(view)

        //new
        val resetButton = view.findViewById<Button>(R.id.resetButton)
        resetButton?.setOnClickListener {
            resetGame()
        }


        winnerTextView = view.findViewById<TextView>(R.id.winnerText)
        val message = PlayBoardArgs.fromBundle(requireArguments()).message
        val playBoardView = view.findViewById<TextView>(R.id.textTransfer)
        playBoardView.text = (message + "'s game")

        return view

    }

//players turn
    private fun initializeBoard(view: View) {
        for (i in 0 until 6) {
            for (j in 0 until 6) {
                val buttonId = resources.getIdentifier("imageButton${i * 6 + j + 1}", "id", requireActivity().packageName)
                val button = view.findViewById<ImageButton>(buttonId)
                board[i][j] = button
                button.setOnClickListener {
                    if (currentPlayer == 0) {
                        it.setBackgroundColor(Color.BLUE)
                        it.isEnabled = false
                        boardState[i][j] = 1 // Assuming i and j represent the row and column
                        val hasWinner = checkWinner()
                        if (!hasWinner && checkForDraw()) {
                            updateTurnStatus("draw")
                        }
                        currentPlayer = 1
                        aiTurn()
                    }
                }
            }
        }
    }


//ai turn
    private fun aiTurn() {
        val randomColumn = Random.nextInt(6)
        for (i in 5 downTo 0) {
            if (board[i][randomColumn]?.isEnabled == true) {
                board[i][randomColumn]?.setBackgroundColor(Color.RED)
                board[i][randomColumn]?.isEnabled = false
                boardState[i][randomColumn] = 2
                val hasWinner = checkWinner()
                if (!hasWinner && checkForDraw()) {
                    updateTurnStatus("draw")
                }
                currentPlayer = 0
                return
            }
        }
    }



    // Updates message for whos turn it is
    private fun updateTurnStatus(winner: String? = null) {
        val turnStatusTextView = view?.findViewById<TextView>(R.id.winnerText)
        turnStatusTextView?.text = when (currentPlayer) {
            0 -> "Player's Turn"
            1 -> "AI's Turn..."
            else -> ""
        }
        when (winner) {
            "player" -> turnStatusTextView?.text = "Player Wins"
            "computer" -> turnStatusTextView?.text = "Computer Wins"
            "draw" -> turnStatusTextView?.text = "Game Draw"
            null -> turnStatusTextView?.text = if (playerTurn) "Player's Turn" else "Computer's Turn"
        }
        if (winner != null) {
            gameActive = false
            view?.findViewById<Button>(R.id.resetButton)?.isEnabled = true
        }
    }

    //checks the winner
    private fun checkWinner(): Boolean {
        // Horizontal and vertical checks
        for (i in 0 until 6) {
            for (j in 0 until 3) {
                // Horizontal check
                if (boardState[i][j] != 0 && boardState[i][j] == boardState[i][j+1] && boardState[i][j] == boardState[i][j+2] && boardState[i][j] == boardState[i][j+3]) {
                    updateWinner(boardState[i][j])
                    return true
                }
                // Vertical check (make sure not to go out of bounds)
                if (i <= 2 && boardState[i][j] != 0 && boardState[i][j] == boardState[i+1][j] && boardState[i][j] == boardState[i+2][j] && boardState[i][j] == boardState[i+3][j]) {
                    updateWinner(boardState[i][j])
                    return true
                }
                //down-right diagonal check
                if (i <= 2 && j <= 2 && boardState[i][j] != 0 &&
                    boardState[i][j] == boardState[i + 1][j + 1] &&
                    boardState[i][j] == boardState[i + 2][j + 2] &&
                    boardState[i][j] == boardState[i + 3][j + 3]) {
                    updateWinner(boardState[i][j])
                    return true
                }

                // Down-left diagonal check
                if (i <= 2 && j >= 3 && boardState[i][j] != 0 &&
                    boardState[i][j] == boardState[i + 1][j - 1] &&
                    boardState[i][j] == boardState[i + 2][j - 2] &&
                    boardState[i][j] == boardState[i + 3][j - 3]) {
                    updateWinner(boardState[i][j])
                    return true
                }
                // Upward diagonal check - ensure i is at least 3 to avoid negative index
                if (i >= 3 && j <= 2 &&
                    boardState[i][j] != 0 &&
                    boardState[i][j] == boardState[i - 1][j + 1] &&
                    boardState[i][j] == boardState[i - 2][j + 2] &&
                    boardState[i][j] == boardState[i - 3][j + 3]) {
                    updateWinner(boardState[i][j])
                    return true
                }
            }
        }
        return false
    }

    //updates the winner
    private fun updateWinner(player: Int) {
        val winnerText = if (player == 1) "player" else "computer"
        updateTurnStatus(winnerText)
    }



    private fun checkForDraw(): Boolean {
        for (row in boardState) {
            for (cell in row) {
                if (cell == 0) { // If any cell is empty, it's not a draw
                    return false
                }
            }
        }
        // If no cells are empty and no winner has been declared, it's a draw
        return true
    }

    //resets the game with the reset button
    private fun resetGame() {
        // Reset board state to 0 (empty)
        for (i in 0 until 6) {
            for (j in 0 until 6) {
                boardState[i][j] = 0
                board[i][j]?.apply {
                    setBackgroundColor(Color.GRAY) // Assuming GRAY is your default button color
                    isEnabled = true
                }
            }
        }

        // Reset game state variables
        currentPlayer = 0 // Or whoever is supposed to start the next game
        gameActive = true
        updateTurnStatus(null) // Clear any win/draw message

        // Optionally, if you're tracking selected squares or other states, reset them too
        userSelectedSquares.clear()
        computerSelectedSquares.clear()
        selectedSquares.clear()

        // Re-enable the reset button or disable it until next win/draw
        view?.findViewById<Button>(R.id.resetButton)?.isEnabled = false
    }



}