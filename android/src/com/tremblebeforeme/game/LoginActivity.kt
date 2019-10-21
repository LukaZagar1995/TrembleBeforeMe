package com.tremblebeforeme.game

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.tremblebeforeme.game.Firebase.Companion.USER_EMAIL


class LoginActivity : Activity() {

    lateinit var et_login_email: EditText
    lateinit var et_login_password: EditText
    lateinit var btn_login_login: Button
    lateinit var btn_login_register: Button

    override fun onStart() {

        if (FirebaseAuth.getInstance().currentUser != null && FirebaseAuth.getInstance().currentUser!!.isEmailVerified ) {
            startActivity(Intent (this, AndroidLauncher::class.java))
            finish()
        }
        super.onStart()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        et_login_email = findViewById(R.id.et_login_email)
        btn_login_login = findViewById(R.id.btn_login_login)
        btn_login_register = findViewById(R.id.btn_login_register)
        et_login_password = findViewById(R.id.et_login_password)

        if (intent.getStringExtra(USER_EMAIL) != null) {
            et_login_email.setText(intent.getStringExtra(USER_EMAIL))
        }

        btn_login_login.setOnClickListener {
                login()
        }

        btn_login_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

    }

    private fun login() {

        validate()
        if (!validatePassword() || !validateUsername()) {
            return
        }

        val firebase = Firebase()
        firebase.loginUser(et_login_email.text.toString(), et_login_password.text.toString(), this)

    }

    private fun validate() {

        validatePassword()
        validateUsername()

    }

    private fun validateUsername(): Boolean {

        if (et_login_email.text.toString().isEmpty()) {
            et_login_email.error = getString(R.string.et_email_empty_error)
            return false
        } else {
            et_login_email.error = null
            return true
        }

    }

    private fun validatePassword(): Boolean {

        if (et_login_password.text.toString().isEmpty()) {
            et_login_password.error = getString(R.string.et_password_empty_error)
            return false
        } else {
            et_login_password.error = null
            return true
        }

    }

}