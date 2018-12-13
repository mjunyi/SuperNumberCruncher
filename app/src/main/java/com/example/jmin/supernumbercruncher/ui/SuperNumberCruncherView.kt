package com.example.jmin.supernumbercruncher.ui

import android.content.Context
import android.support.design.widget.Snackbar
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.example.jmin.supernumbercruncher.R
import com.example.jmin.supernumbercruncher.R.color.*
import com.example.jmin.supernumbercruncher.gamemodel.SuperNumberCruncherModel
import com.example.jmin.supernumbercruncher.gamemodel.SuperNumberCruncherModel.getFocus
import com.example.jmin.supernumbercruncher.gamemodel.SuperNumberCruncherModel.getTile
import com.example.jmin.supernumbercruncher.gamemodel.SuperNumberCruncherModel.placeFocus
import com.example.jmin.supernumbercruncher.gamemodel.SuperNumberCruncherModel.randomNumGenerator
import com.example.jmin.supernumbercruncher.gamemodel.SuperNumberCruncherModel.setTile
import kotlinx.android.synthetic.main.activity_game.view.*

class SuperNumberCruncherView constructor(context: Context?, attrs: AttributeSet?) : View(context, attrs){
    interface GameHandler{
        fun incrementScore()
        fun decreaseScore()
        fun deductTime()
        fun addTime()
        fun scoreIsNewHighscore(): Boolean
        fun updateHighscore()
    }
    companion object {
        private val SWIPE_MIN_DISTANCE = 100
        private val SWIPE_MAX_OFF_PATH = 100
        private val SWIPE_THRESHOLD_VELOCITY = 100
    }

    private var gameHandler: GameHandler

    private var gameOver = false
    private val numColumns = 5
    private val numRows = 6

    private val paintLine = Paint()
    private val paintFocus = Paint()

    private var paintNumber = Paint()
    private var paintTile = Paint()
    private var tileColors = arrayOf(
            ContextCompat.getColor(getContext(),block1), //0th position in array
            ContextCompat.getColor(getContext(),block2), //1
            ContextCompat.getColor(getContext(),block3),
            ContextCompat.getColor(getContext(),block4),
            ContextCompat.getColor(getContext(),block5),
            ContextCompat.getColor(getContext(),block6),
            ContextCompat.getColor(getContext(),block7),
            ContextCompat.getColor(getContext(),block8),
            ContextCompat.getColor(getContext(),block9), //8
            ContextCompat.getColor(getContext(), block10Plus), //9
            ContextCompat.getColor(getContext(), block21), //10
            ContextCompat.getColor(getContext(), blockOver21)) //11th position in array

    private var gestureDetector = GestureDetector(context, GestureListener())

    init{
        paintLine.color = Color.WHITE
        paintLine.strokeWidth = 10F
        paintLine.style = Paint.Style.STROKE

        paintFocus.color = ContextCompat.getColor(getContext(), singlefocus)
        paintFocus.strokeWidth = 10F
        paintFocus.style = Paint.Style.STROKE

        paintNumber.color = Color.WHITE
        paintNumber.textSize = 100F
        paintNumber.typeface = Typeface.create("Aerial",Typeface.BOLD)

        paintTile.color = tileColors[9]

        if(context is GameHandler){
            gameHandler= context
        }
        else{
            throw RuntimeException("Activity does not implement ScoreHandler interface")
        }
    }

    public fun setGameStatus(status: Boolean){
        gameOver = status
    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)
        if(!gameOver){
            drawTiles(canvas)
            drawGameArea(canvas)
            drawFocus(canvas)
        }
    }

    fun drawGameArea(canvas: Canvas) {
        for(i in 0..numRows){
            canvas.drawLine(0f, (i * height / numRows).toFloat(), width.toFloat(),
                    (i * height / numRows).toFloat(),
                    paintLine)}

        for(i in 0..numColumns){
            canvas.drawLine((i * width / numColumns).toFloat(), 0f,
                    (i * width / numColumns).toFloat(), height.toFloat(),
                    paintLine)
        }
    }

    fun drawTiles(canvas: Canvas){
        for (i in 0..5){
            for (j in 0..4){
                drawSingleTile(i, j, canvas)
            }
        }
    }

    fun drawSingleTile(row: Int, column: Int, canvas: Canvas){
        val number = SuperNumberCruncherModel.getTile(row, column)
        when {
            number==0 -> throw RuntimeException(context.getString(R.string.gameboard_not_initialized))
            number==21 -> paintTile.color = tileColors[10]
            number>21 -> paintTile.color = tileColors[11]
            number>=10 -> paintTile.color = tileColors[9]
            else -> paintTile.color = tileColors[number-1]
        }
        canvas.drawRect((column * width / numColumns).toFloat(),
                (row * height / numRows).toFloat(),
                ((column+1) * width / numColumns).toFloat(),
                ((row+1) * height / numRows).toFloat(),
                paintTile)

        when{
            number>9 ->canvas.drawText(number.toString(),
                    ((column+0.15) * width / numColumns).toFloat(),
                    ((row+0.75) * height / numRows).toFloat(),
                    paintNumber)
            number>99 -> canvas.drawText(context.getString(R.string.ninetynine),
                    ((column+0.15) * width / numColumns).toFloat(),
                    ((row+0.75) * height / numRows).toFloat(),
                    paintNumber)
            else -> canvas.drawText(number.toString(),
                    ((column+0.3) * width / numColumns).toFloat(),
                    ((row+0.75) * height / numRows).toFloat(),
                    paintNumber)
        }
    }

    private fun drawFocus(canvas: Canvas){
        val focus = SuperNumberCruncherModel.getFocus()
        if(focus[0]!=-1 && focus[1]!=-1){
            canvas.drawRect((focus[0]*(width/numColumns).toFloat()),
                    (focus[1]*(height/numRows).toFloat()),
                    ((focus[0]+1)*(width/numColumns).toFloat()),
                    ((focus[1]+1)*(height/numRows).toFloat()), paintFocus)
        }
    }

    fun showMessage(msg: String) {
        Snackbar.make(superNumberCruncherView, msg, Snackbar.LENGTH_LONG).show()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event);
    }

    fun tilesOnSideTouched(lastTouch: Array<Int>, newX:Int, newY:Int): Boolean{
        val oldX = lastTouch[0]
        val oldY = lastTouch[1]
        return if(oldX==(newX+1) && oldY==newY){
            true
        }
        else if(oldX==(newX-1) && oldY==newY){
            true
        }
        else if(oldX==newX && oldY==(newY+1)){
            true
        }
        else oldX==newX && oldY==(newY-1)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if(!gameOver){
                val columnInitial = e1.x.toInt() / (width / numColumns)
                val rowInitial = e1.y.toInt() / (height / numRows)
                val columnFinal = e2.x.toInt() / (width / numColumns)
                val rowFinal = e2.y.toInt() / (height / numRows)
                if (columnInitial < numColumns && rowInitial < numRows &&
                        columnFinal < numColumns && rowFinal < numRows &&
                        columnInitial>= 0 && rowInitial>= 0 &&
                        columnFinal>= 0 && rowFinal>=0) {
                    if (Math.abs(e1.y - e2.y) > SWIPE_MAX_OFF_PATH) {
                        if (Math.abs(e1.x - e2.x) > SWIPE_MAX_OFF_PATH
                                || Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY) {
                            return false
                        }
                        if (e1.y - e2.y > SWIPE_MIN_DISTANCE) {
                            swipeUp(columnInitial,rowInitial,columnFinal,rowFinal)
                        } else if (e2.y - e1.y > SWIPE_MIN_DISTANCE) {
                            swipeDown(columnInitial,rowInitial,columnFinal,rowFinal)
                        }
                    } else {
                        if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
                            return false
                        }
                        if (e1.x - e2.x > SWIPE_MIN_DISTANCE) {
                            swipeLeft(columnInitial,rowInitial,columnFinal,rowFinal)
                        } else if (e2.x - e1.x > SWIPE_MIN_DISTANCE) {
                            swipeRight(columnInitial,rowInitial,columnFinal,rowFinal)
                        }
                    }
                }
            }
            return true
        }

        fun swipeScoreIncreaseHelper(){
            gameHandler.incrementScore()
            if(gameHandler.scoreIsNewHighscore()) {
                gameHandler.updateHighscore()
            }
            gameHandler.addTime()
        }

        fun swipeScoreDecreaseHelper(){
            gameHandler.deductTime()
            gameHandler.decreaseScore()
        }

        fun swipeUp(columnInitial: Int, rowInitial:Int, columnFinal:Int, rowFinal:Int){
            val numOfTiles = rowInitial - rowFinal
            var newNumber = 0
            if (!gameOver) {
                for (i in 0..numOfTiles) {
                    newNumber += getTile(rowInitial - i, columnInitial)
                }

                if (newNumber == 21) {
                    swipeScoreIncreaseHelper()
                    for (i in 0..numOfTiles) {
                        setTile(rowInitial - i, columnInitial, randomNumGenerator(10))
                    }
                } else {
                    if(newNumber>21){
                        swipeScoreDecreaseHelper()
                    }
                    for (i in 0 until numOfTiles) {
                        setTile(rowInitial -
                                i, columnInitial, randomNumGenerator(10))
                    }
                    setTile(rowFinal, columnFinal, newNumber)
                }

                placeFocus(rowFinal, columnFinal)
                invalidate()
            }
        }

        fun swipeDown(columnInitial: Int, rowInitial:Int, columnFinal:Int, rowFinal:Int){
            val numOfTiles = rowFinal - rowInitial
            var newNumber = 0
            if (!gameOver) {
                for (i in 0..numOfTiles) {
                    newNumber += getTile(rowInitial + i, columnInitial)
                }

                if (newNumber == 21) {
                    swipeScoreIncreaseHelper()
                    for (i in 0..numOfTiles) {
                        setTile(rowInitial + i, columnInitial, randomNumGenerator(10))
                    }
                } else {
                    if(newNumber>21){
                        swipeScoreDecreaseHelper()
                    }
                    for (i in 0 until numOfTiles) {
                        setTile(rowInitial +
                                i, columnInitial, randomNumGenerator(10))
                    }
                    setTile(rowFinal, columnFinal, newNumber)
                }
                placeFocus(rowFinal, columnFinal)
                invalidate()

            }
        }
        fun swipeLeft(columnInitial: Int, rowInitial:Int, columnFinal:Int, rowFinal:Int){
            val numOfTiles = columnInitial - columnFinal
            var newNumber = 0
            if (!gameOver) {
                for (i in 0..numOfTiles) {
                    newNumber += getTile(rowInitial, columnInitial-i)
                }

                if (newNumber == 21) {
                    swipeScoreIncreaseHelper()
                    for (i in 0..numOfTiles) {
                        setTile(rowInitial, columnInitial-i, randomNumGenerator(10))
                    }
                } else {
                    if(newNumber>21){
                        swipeScoreDecreaseHelper()
                    }
                    for (i in 0 until numOfTiles) {
                        setTile(rowInitial, columnInitial-i, randomNumGenerator(10))
                    }
                    setTile(rowFinal, columnFinal, newNumber)
                }
                placeFocus(rowFinal, columnFinal)
                invalidate()
            }
        }
        fun swipeRight(columnInitial: Int, rowInitial:Int, columnFinal:Int, rowFinal:Int){
            val numOfTiles = columnFinal - columnInitial
            var newNumber = 0
            if (!gameOver) {
                for (i in 0..numOfTiles) {
                    newNumber += getTile(rowInitial, columnInitial+i)
                }

                if (newNumber == 21) {
                    swipeScoreIncreaseHelper()
                    for (i in 0..numOfTiles) {
                        setTile(rowInitial, columnInitial+i, randomNumGenerator(10))
                    }
                } else {
                    if(newNumber>21){
                        swipeScoreDecreaseHelper()
                    }
                    for (i in 0 until numOfTiles) {
                        setTile(rowInitial, columnInitial+i, randomNumGenerator(10))
                    }
                    setTile(rowFinal, columnFinal, newNumber)
                }

                placeFocus(rowFinal, columnFinal)
                invalidate()
            }
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (!gameOver){
                val lastTouch = getFocus() //Row, Column
                var newNumber = 0

                val column = e.x.toInt() / (width / numColumns)
                val row = e.y.toInt() / (height / numRows)

                if (column < numColumns && row < numRows) {
                    if(tilesOnSideTouched(lastTouch, column, row)){
                        newNumber = getTile(lastTouch[1], lastTouch[0]) *
                                getTile(row, column)
                        if(newNumber == 21){
                            swipeScoreIncreaseHelper()
                            setTile(row, column, randomNumGenerator(10))
                        }
                        else{
                            if(newNumber>21){
                                swipeScoreDecreaseHelper()
                            }
                            setTile(row, column, newNumber)
                        }
                        setTile(lastTouch[1], lastTouch[0], randomNumGenerator(10))
                    }
                    placeFocus(row, column)
                    invalidate()
                }
            }
            return super.onSingleTapUp(e)
        }
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }
    }

    fun restart(){
        gameOver = false
        SuperNumberCruncherModel.resetGameboard()
        invalidate()
    }
}
