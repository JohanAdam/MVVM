package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import android.util.TimeUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit
import kotlin.math.min

private val CORRECT_BUZZ_PATTERN = longArrayOf(100, 100, 100, 100, 100, 100)
private val PANIC_BUZZ_PATTERN = longArrayOf(0, 200)
private val GAME_OVER_BUZZ_PATTERN = longArrayOf(0, 2000)
private val NO_BUZZ_PATTERN = longArrayOf(0)

class GameViewModel : ViewModel() {

    enum class BuzzType(val pattern: LongArray) {
        CORRECT(CORRECT_BUZZ_PATTERN),
        GAME_OVER(GAME_OVER_BUZZ_PATTERN),
        COUNTDOWN_PANIC(PANIC_BUZZ_PATTERN),
        NO_BUZZ(NO_BUZZ_PATTERN)
    }

    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L
        // This is the total time of the game (30s)
        const val COUNTDOWN_TIME = 30000L

        // This is the time when the phone will start buzzing each second
        const val COUNTDOWN_PANIC_SECONDS = 10L
    }

    // The current word
    var word = MutableLiveData<String>()

    // The current score
    //Internal use.
    private var _score = MutableLiveData<Int>()
    /**General rule of thumb : we should not let view able to edit the data we make publicly.
    This is why we convert the score to LiveData which view can't make changes to the data.
    LiveData means the value cannot be Mutable.*/
    //External use.
    val score: LiveData<Int>
        get() = _score

    //Flag that game has completed.
    private var _eventGamefinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGamefinish

    //Flag that for vibration.
    private var _eventBuzz = MutableLiveData<BuzzType>()
    val eventBuzz: LiveData<BuzzType>
        get() = _eventBuzz

    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    private var timer: CountDownTimer

    private var _countdown = MutableLiveData<Long>()
    val countdown: LiveData<Long>
        get() = _countdown
    //
    //Transform the value before make it publicly.
    val countdownString = Transformations.map(countdown) { time ->
//        DateUtils.formatElapsedTime(time)
        "" + TimeUnit.MILLISECONDS.toMinutes(time) + ":" + TimeUnit.MILLISECONDS.toSeconds(time)
    }

    init {
        Log.e("e","onCreate GameViewModel")
        _eventGamefinish.value = false
        resetList()
        nextWord()
        //Initialized score live data, because by default it will be null.
        _score.value = 0

        //Initialized timer.
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                _countdown.value = millisUntilFinished

                if (millisUntilFinished / ONE_SECOND <= COUNTDOWN_PANIC_SECONDS) {
                    _eventBuzz.value = BuzzType.COUNTDOWN_PANIC
                }
            }
            override fun onFinish() {
                _eventBuzz.value = BuzzType.GAME_OVER
                _eventGamefinish.value = true
            }
        }
        timer.start()
    }

    override fun onCleared() {
        super.onCleared()
        Log.e("e","onCleared GameViewModel")
        timer.cancel()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
//            gameFinished()
//            _eventGamefinish.value = true
            resetList()
        }
//        } else {
        word.value = wordList.removeAt(0)
//        }
//        updateWordText()
//        updateScoreText()
    }

    /** Methods for buttons presses **/
    fun onSkip() {
        //Minus score value if not null.
        //Basically just score.value-- but with null check.
        _score.value = score.value?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        //Plus score value if not null.
        //Basically just score.value++ but with null check.
        _score.value = score.value?.plus(1)
        nextWord()
        _eventBuzz.value = BuzzType.CORRECT
    }

    fun onGameCompleted() {
        _eventGamefinish.value = false
    }

    fun onBuzzCompleted() {
        _eventBuzz.value = BuzzType.NO_BUZZ
    }
}