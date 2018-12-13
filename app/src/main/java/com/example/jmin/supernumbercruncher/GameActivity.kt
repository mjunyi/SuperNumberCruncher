package com.example.jmin.supernumbercruncher

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.example.jmin.supernumbercruncher.gamemodel.SuperNumberCruncherModel
import com.example.jmin.supernumbercruncher.ui.SuperNumberCruncherView
import kotlinx.android.synthetic.main.activity_game.*


class GameActivity : AppCompatActivity(), SuperNumberCruncherView.GameHandler {
    var seconds = 101
    var alpha = 1.0

    companion object {
        private val KEY_RECORD = "KEY_RECORD"
    }

    private inner class MyTimerThread: Thread (){
        override fun run(){
            while(seconds!=0){
                runOnUiThread {
                    seconds--
                    tvTime.text = seconds.toString() + getString(R.string.singleS)
                }
                sleep(1000)
            }
            runOnUiThread{
                gameOver()
            }
        }
    }

    private inner class fadeTimerThread: Thread(){
        override fun run(){
            if(alpha!=1.0) alpha = 1.0
            while(alpha!=0.0){
                runOnUiThread {
                    alpha -= 0.05
                    tvTimeAdjustment.alpha = alpha.toFloat()
                }
                sleep(50)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        SuperNumberCruncherModel.resetGameboard()

        ivPlayAgain.setOnClickListener{
            setListener()
        }

        ivGoBack.setOnClickListener{
            finish()
        }

        MyTimerThread().start()
        displayHighscore()
    }

    private fun displayHighscore(){
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val record = sp.getString(KEY_RECORD, getString(R.string.zero))
        tvRecord.text = record
    }

    override fun updateHighscore(){
        val newHighscore = tvScore.text.toString()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sp.edit()
        editor.putString(KEY_RECORD, newHighscore)
        editor.apply()
        tvRecord.text = tvScore.text.toString()
    }
    override fun scoreIsNewHighscore(): Boolean{
        val score = tvScore.text.toString().toInt()
        val record = tvRecord.text.toString().toInt()
        return score>record
    }
    private fun setListener() {
        superNumberCruncherView.restart()
        tvScore.text = getString(R.string.zero)
        tvTime.text = getString(R.string.onehundredseconds)
        ivGoBack.visibility = View.GONE
        ivPlayAgain.visibility = View.GONE
        ivGameOverBox.visibility = View.GONE
        seconds = 101
        alpha = 1.0
        MyTimerThread().start()
    }

    public fun gameOver(){
        val message = getString(R.string.game_over_your_score_is) + " " + tvScore.text.toString()
        superNumberCruncherView.setGameStatus(true)

        superNumberCruncherView.showMessage(message)
        ivGameOverBox.visibility = View.VISIBLE
        ivGoBack.visibility = View.VISIBLE
        ivPlayAgain.visibility = View.VISIBLE
    }

    public override fun addTime(){
        seconds += 5
        if(seconds>100){
            seconds = 101
        }
        alpha = 1.0
        tvTimeAdjustment.text = getString(R.string.plus5s)
        fadeTimerThread().start()
    }

    public override fun deductTime(){
        seconds -= 10
        if(seconds<10){
            seconds = 0
            tvTime.text = getString(R.string.zeroseconds)
        }
        alpha = 1.0
        tvTimeAdjustment.text = getString(R.string.minus10seconds)
        fadeTimerThread().start()
    }

    public override fun incrementScore(){
        var score = tvScore.text.toString().toInt()
        score += 100
        tvScore.text = score.toString()
    }

    public override fun decreaseScore(){
        var score = tvScore.text.toString().toInt()
        score -= 50
        tvScore.text = score.toString()
    }
}