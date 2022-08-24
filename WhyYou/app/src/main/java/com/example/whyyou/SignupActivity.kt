package com.example.whyyou

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup.*
import org.jetbrains.anko.AlertBuilder
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast

@Suppress("NAME_SHADOWING", "DEPRECATION")
class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val firestore = Firebase.firestore

        // 아이디 중복확인 버튼 클릭 시
        checkIdBtn.setOnClickListener {
            val id = userId.text.toString()

            firestore.collection("users")
                    .whereEqualTo("id", id)
                    .get()
                    .addOnSuccessListener {
                        if (it.isEmpty) {
                            checkId.text = "사용할 수 있는 아이디입니다."
                            checkId.visibility = VISIBLE
                        } else {
                            checkId.text = "사용할 수 없는 아이디입니다."
                            checkId.visibility = VISIBLE
                        }
                    }

        }

        // 회원가입 버튼 클릭 시
        btnSignUp.setOnClickListener {
            val userEmail = userEmail.text.toString()
            val password = userPassword.text.toString()
            val checkUserPassword = checkUserPassword.text.toString()
            val userId = userId.text.toString()
            val userName = userName.text.toString()

            if (password != checkUserPassword ) {
                checkPassword.visibility = VISIBLE
            } else {
                // 계정 생성
                Firebase.auth.createUserWithEmailAndPassword(userEmail, password)
                        .addOnCompleteListener { task ->
                            // 계정 생성 성공 시 DB에 정보 저장
                            if (task.isSuccessful) {
                                toast("회원가입 성공")

                                val user = hashMapOf(
                                        "email" to userEmail,
                                        "password" to password,
                                        "id" to userId,
                                        "name" to userName
                                )

                                val database = FirebaseFirestore.getInstance()
                                val currentUser = Firebase.auth.uid

                                database.collection("users").document(currentUser!!)
                                        .set(user)
                                        .addOnSuccessListener {
                                            toast("저장 성공")
                                        }
                                        .addOnFailureListener {
                                            toast("저장 실패")
                                        }

                                finish()

                            } else {
                                toast(task.exception.toString())
                            }
                        }
            }
        }
    }
}