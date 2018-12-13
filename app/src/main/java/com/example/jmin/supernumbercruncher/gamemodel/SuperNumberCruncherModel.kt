package com.example.jmin.supernumbercruncher.gamemodel

import java.util.*

object SuperNumberCruncherModel{

    private var currFocus = arrayOf(-1, -1)
    private val randomNum = Random(System.currentTimeMillis())

    public fun placeFocus(column: Int, row: Int){
        currFocus[0] = row
        currFocus[1] = column
    }

    public fun getFocus() = currFocus

    private var gameBoard = Array(6) {Array(5){(0)}}

    fun getTile(row: Int, column: Int): Int{
        return gameBoard[row][column]
    }

    public fun setTile(row: Int, column: Int, newNumber:Int){
        var num = newNumber
        if (num>99){
            num = 99
        }
        gameBoard[row][column] = num
    }
    fun randomNumGenerator(to: Int) : Int {
        return randomNum.nextInt(to)+1
    }

    public fun resetGameboard(){
        currFocus = arrayOf(-1, -1)
        for (i in 0..5){
            for (j in 0..4){
                gameBoard[i][j] = randomNumGenerator(10)
            }
        }
        placeFocus(-1, -1)
    }
}