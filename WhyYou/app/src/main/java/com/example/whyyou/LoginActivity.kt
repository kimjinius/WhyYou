package com.example.whyyou

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btn_login.setOnClickListener {
            val userEmail = email.text.toString()
            val password = password.text.toString()
            doLogin(userEmail, password)
        }

        btn_sign_up.setOnClickListener {
            startActivity<SignupActivity>()
        }
    }

    // 로그인 함수
    private fun doLogin(userEmail: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) { // 로그인 성공 시 MainActivity로 이동
                    val newIntent = Intent(this, MainActivity::class.java)
                    startActivity(newIntent)
                    finish()
                } else {
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    toast("Authentication failed.")
                }
            }
    }
}