package com.tremblebeforeme.game

import android.app.Activity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText

class RegisterActivity : Activity() {

    lateinit var btn_register_register: Button
    lateinit var et_register_email: EditText
    lateinit var et_register_username: EditText
    lateinit var et_register_password: EditText
    lateinit var et_register_repeatPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_register_register = findViewById(R.id.btn_register_register)
        et_register_email = findViewById(R.id.et_register_email)
        et_register_username = findViewById(R.id.et_register_username)
        et_register_password = findViewById(R.id.et_register_password)
        et_register_repeatPassword = findViewById(R.id.et_register_repeatPassword)

        btn_register_register.setOnClickListener{
            register()
        }

    }

    private fun register() {

        validate()
        if(!validateEmail() || !validateUsername() || !validatePassword()){
            return
        } else {
            val user = User()
            user.email = et_register_email.text.toString()
            user.username = et_register_username.text.toString()

            val firebase = Firebase()
            firebase.createUser(user, et_register_password.text.toString(), this)

        }
    }

    private fun validate(){
        validateEmail()
        validateUsername()
        validatePassword()
    }

    private fun validateUsername():Boolean {

        if(et_register_username.text.toString().isEmpty()){
            et_register_username.error = getString(R.string.et_username_empty_error)
            return false
        } else {
            et_register_username.error = null
            return true
        }

    }

    private fun validateEmail():Boolean {

        if(et_register_email.text.toString().isEmpty()){
            et_register_email.error = getString(R.string.et_email_empty_error)
            return false
        } else if(!Patterns.EMAIL_ADDRESS.matcher(et_register_email.text.toString()).matches()) {
            et_register_email.error = getString(R.string.et_email_pattern_error)
            return false
        } else {
            et_register_email.error = null
            return true
        }

    }

    private fun validatePassword():Boolean {

        if(et_register_password.text.toString().isEmpty()){
            et_register_password.error = getString(R.string.et_password_empty_error)
            return false
        } else if(et_register_password.text.toString().length < 6) {
            et_register_password.error = getString(R.string.et_password_length_error)
            return false
        } else if (et_register_password.text.toString() != et_register_repeatPassword.text.toString()) {
            et_register_password.error = getString(R.string.et_repeatPassword_match_error)
            return false
        } else {
            et_register_password.error = null
            et_register_repeatPassword.error = null
            return true
        }

    }
}
