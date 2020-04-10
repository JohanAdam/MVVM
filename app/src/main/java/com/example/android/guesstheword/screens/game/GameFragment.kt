/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.guesstheword.screens.game

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

//    // The current word
//    private var word = ""
//
//    // The current score
//    private var score = 0
//
//    // The list of words - the front of the list is the next word to guess
//    private lateinit var wordList: MutableList<String>

    private lateinit var binding: GameFragmentBinding

    private lateinit var viewModel: GameViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.game_fragment,
                container,
                false
        )

        //Initialized View model.
        Log.e("e","Initialized View Model.")
        viewModel = ViewModelProviders.of(this).get(GameViewModel::class.java)

//        binding.correctButton.setOnClickListener {
//            viewModel.onCorrect()
//            updateWordText()
//            updateScoreText()
//        }
//        binding.skipButton.setOnClickListener {
//            viewModel.onSkip()
//            updateWordText()
//            updateScoreText()
//        }
//        updateScoreText()
//        updateWordText()
        binding.gameViewModel = viewModel
        binding.lifecycleOwner = this

        //Register a live data listener.
        //Observe score value changes.
//        viewModel.score.observe(this, Observer {newScoreValue ->
//            When there is changes to score value, update the textview score.
//            binding.scoreText.text = newScoreValue.toString()
//        })

        //We don't need this anymore, if we bind the xml to this viewmodel directly.
        //Observe word value changes.
//        viewModel.word.observe(this, Observer {newWordValue ->
            //When there is changes to word value, update the textview word.
//            binding.wordText.text = newWordValue
//        })
        //Observer when the game is ended.
        viewModel.eventGameFinish.observe(this, Observer { isFinish ->
            if (isFinish) {
                gameFinished()
                viewModel.onGameCompleted()
            }
        })
//        viewModel.countdown.observe(this, Observer {
//            binding.timerText.text = it
//        })

        viewModel.eventBuzz.observe(this, Observer { buzzType ->
            if (buzzType != GameViewModel.BuzzType.NO_BUZZ) {
                buzz(buzzType.pattern)
                viewModel.onBuzzCompleted()
            }
        })

        return binding.root
    }

    /**
     * Called when the game is finished
     */
    private fun gameFinished() {
        //If score is null, use 0.
        val currentScore = viewModel.score.value ?: 0
        val action = GameFragmentDirections.actionGameToScore(currentScore)
        findNavController(this).navigate(action)
//        Toast.makeText(this.activity, "Game BABI", Toast.LENGTH_LONG).show()
    }

    private fun buzz(pattern: LongArray) {
        val buzzer = activity?.getSystemService<Vibrator>()

        buzzer?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                buzzer.vibrate(pattern, -1)
            }
        }
    }

    /** Methods for updating the UI **/

//    private fun updateWordText() {
//        binding.wordText.text = viewModel.word
//
//    }

//    private fun updateScoreText() {
//        binding.scoreText.text = viewModel.score.toString()
//    }
}
