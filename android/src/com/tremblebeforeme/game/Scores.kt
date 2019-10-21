package com.tremblebeforeme.game

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.tremblebeforeme.game.Constants.DB_SCORES
import kotlin.collections.ArrayList


class Scores : IScores {


    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var db: DatabaseReference
    val last10ScoresList = ArrayList<Score>()
    val lastMonthScoresList = ArrayList<Score>()
    val thisYearScoresList = ArrayList<Score>()
    val lastYearScoresList = ArrayList<Score>()
    val day = SimpleDateFormat("dd")
    val month = SimpleDateFormat("MM")
    val year = SimpleDateFormat("yyyy")
    val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)

    override fun getThisYearScores(): ArrayList<Score> {
        thisYearScoresList.clear()
        val date = Date()
        var monthScores: Long
        var numberOfPlayedDays = 0
        val dataYear = year.format(date)

        val ref = FirebaseDatabase.getInstance().reference.child(DB_SCORES).child(firebaseAuth.uid!!).child(dataYear)
        ref.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshotMonth in dataSnapshot.children) {
                            monthScores = 0
                            for (snapshotDay in snapshotMonth.children) {
                                val score = snapshotDay.child("score").getValue(Long::class.java)!!
                                monthScores += score
                                numberOfPlayedDays++
                            }

                            val firstDate = snapshotMonth.key
                            val formatter = SimpleDateFormat("MM", Locale.ENGLISH)
                            val date = formatter.parse(firstDate)
                            val averageScore = monthScores / numberOfPlayedDays
                            thisYearScoresList.add(Score(averageScore, SimpleDateFormat("MMMM", Locale.ENGLISH).format(date)))
                            numberOfPlayedDays = 0
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
        return thisYearScoresList
    }

    override fun getLastYearScores(): ArrayList<Score> {
        lastYearScoresList.clear()
        val date = Date()
        var monthScores: Long
        var numberOfPlayedDays = 0
        val dataYear = (year.format(date).toInt() - 1).toString()

        val ref = FirebaseDatabase.getInstance().reference.child(DB_SCORES).child(firebaseAuth.uid!!).child(dataYear)
        ref.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshotMonth in dataSnapshot.children) {
                            monthScores = 0
                            for (snapshotDay in snapshotMonth.children) {
                                val score = snapshotDay.child("score").getValue(Long::class.java)!!
                                monthScores += monthScores + score
                                numberOfPlayedDays++
                            }

                            val firstDate = snapshotMonth.key
                            val formatter = SimpleDateFormat("MM", Locale.ENGLISH)
                            val date = formatter.parse(firstDate)
                            lastYearScoresList.add(Score(monthScores / numberOfPlayedDays,SimpleDateFormat("MMMM", Locale.ENGLISH).format(date)))
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
        return lastYearScoresList
    }

    override fun getLastMonthScores(): ArrayList<Score> {
        lastMonthScoresList.clear()
        val date = Date()
        var dataYear = year.format(date)
        var dataMonth = month.format(date)

        if (dataMonth == "01") {
            dataMonth = "12"
            dataYear = (dataYear.toInt() - 1).toString()
        } else {
            dataMonth = if (dataMonth.toInt() >= 10) {
                (dataMonth.toInt() - 1).toString()
            } else {
                "0" + (dataMonth.toInt() - 1).toString()
            }

        }

        val ref = FirebaseDatabase.getInstance().reference.child(DB_SCORES).child(firebaseAuth.uid!!).child(dataYear).child(dataMonth)
        ref.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshotDay in dataSnapshot.children) {
                            val score = snapshotDay.child("score").getValue(Long::class.java)!!
                            val firstDate = snapshotDay.child("date").getValue(String::class.java)!!
                            val formatter = SimpleDateFormat("dd", Locale.ENGLISH)
                            val date = formatter.parse(firstDate)

                            lastMonthScoresList.add(Score(score, "Day " + SimpleDateFormat("dd", Locale.ENGLISH).format(date)) )
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
        return lastMonthScoresList
    }

    override fun getLast10Scores(): ArrayList<Score> {
        last10ScoresList.clear()
        val ref = FirebaseDatabase.getInstance().reference.child(DB_SCORES).child(firebaseAuth.uid!!)
        ref.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshotYear in dataSnapshot.children) {
                            for (snapshotMonth in snapshotYear.children) {
                                for (snapshotDay in snapshotMonth.children) {
                                    val score = snapshotDay.child("score").getValue(Long::class.java)!!
                                    val date = snapshotDay.child("date").getValue(String::class.java)!!
                                    last10ScoresList.add(Score(score, date))
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
        return last10ScoresList

    }

    override fun saveScore(score: Int) {
        val firebaseUser = firebaseAuth.currentUser
        var dailyScore = 0
        val userID = firebaseUser!!.uid
        val hashMap = HashMap<String, Any>()
        val date = Date()
        db = FirebaseDatabase.getInstance().getReference(Constants.DB_SCORES).child(userID).child(year.format(date)).child(month.format(date)).child(day.format(date))

        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val score1 = dataSnapshot.getValue(Score::class.java)
                if (score1 != null) {
                    dailyScore = score1.score.toInt()
                }

                if (score1 != null) {
                    dailyScore = if (dailyScore > 0) {
                        (dailyScore + score) / 2
                    } else {
                        score
                    }
                }
                hashMap["score"] = dailyScore
                hashMap["date"] = dateFormat.format(date)

                db.setValue(hashMap)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })

    }


}