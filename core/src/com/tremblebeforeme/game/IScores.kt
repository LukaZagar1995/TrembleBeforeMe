package com.tremblebeforeme.game

interface IScores {

    fun getLast10Scores():ArrayList<Score>
    fun getThisYearScores():ArrayList<Score>
    fun getLastYearScores():ArrayList<Score>
    fun getLastMonthScores():ArrayList<Score>
    fun saveScore(score: Int)

}