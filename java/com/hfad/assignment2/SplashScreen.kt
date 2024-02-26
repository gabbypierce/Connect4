/*
Author: Gabby Pierce
Date: February 26 2024
Project: Connect 4 app
 */
package com.hfad.assignment2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import android.widget.EditText



/**
 * A simple [Fragment] subclass.
 * Use the [SplashScreen.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashScreen : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_splash_screen, container, false)

        // Find the start button
        val startButton = view.findViewById<Button>(R.id.startButton)
        val messageView = view.findViewById<EditText>(R.id.NameSpace)



        // Set OnClickListener to navigate to the PlayBoard fragment
        startButton.setOnClickListener {
            Log.d("SplashScreen", "Button clicked")
            //findNavController().navigate(R.id.action_splashScreen_to_playBoard)
            val message = messageView.text.toString()
            val action = SplashScreenDirections
                            .actionSplashScreenToPlayBoard(message)
            view.findNavController().navigate(action)

        }
        return view
    }
}