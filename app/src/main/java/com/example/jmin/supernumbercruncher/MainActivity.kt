package com.example.jmin.supernumbercruncher

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.example.jmin.supernumbercruncher.gamemodel.SuperNumberCruncherModel
import com.example.jmin.supernumbercruncher.ui.SuperNumberCruncherView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val anim = AnimationUtils.loadAnimation(this@MainActivity, R.anim.rotate_anim)
        btnPlay.startAnimation(anim)
        btnPlay.setOnClickListener {
            runGameActivity()
        }

        btnHowToPlay.startAnimation(anim)
        btnHowToPlay.setOnClickListener {
            runHowToActivity()
        }
    }
    private fun runGameActivity(){
        val myIntent = Intent()
        myIntent.setClass(this@MainActivity, GameActivity::class.java)
        startActivity(myIntent)
    }

    private fun runHowToActivity(){
        val myIntent = Intent()
        myIntent.setClass(this@MainActivity, HowToActivity::class.java)
        startActivity(myIntent)
    }
}
