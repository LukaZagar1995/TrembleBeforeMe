package com.tremblebeforeme.game

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tremblebeforeme.game.Constants.DB_USERS

class Firebase {

    companion object {
        const val USER_EMAIL = "user_email"
    }

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var db: DatabaseReference

    fun createUser(user: User, password: String, context: Context) {

        firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = firebaseAuth.currentUser
                        val userID = firebaseUser!!.uid

                        db = FirebaseDatabase.getInstance().getReference(DB_USERS).child(userID)

                        val hashMap = HashMap<String, Any>()
                        hashMap["id"] = userID
                        hashMap["username"] = user.username
                        hashMap["email"] = user.email

                        db.setValue(hashMap).addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                firebaseAuth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        val intent = Intent(context, LoginActivity::class.java)
                                        intent.putExtra(USER_EMAIL, user.email)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        context.startActivity(intent)
                                    } else {
                                        Toast.makeText(
                                                context,
                                                R.string.verification_email_error,
                                                Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                }

                            }
                        }
                    } else {
                        Toast.makeText(
                                context,
                                R.string.registration_error,
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
    }

    fun loginUser(email: String, password: String, context: Context) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (firebaseAuth.currentUser!!.isEmailVerified) {
                            val intent = Intent(context, AndroidLauncher::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(
                                    context,
                                    R.string.verified_email_error,
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                                context,
                                R.string.login_error,
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }

    }
}