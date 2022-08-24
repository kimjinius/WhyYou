package com.example.whyyou

import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.friend_listview.*
import kotlinx.android.synthetic.main.friend_request.*
import org.jetbrains.anko.toast

@Suppress("NAME_SHADOWING", "UNCHECKED_CAST")
class FriendRequest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.friend_request)

        lateinit var currentUserName : String

        friend_addclose.setOnClickListener {
            finish()
        }

        // 요청 버튼 눌렀을 때
        btn_request.setOnClickListener {

            val firestore = Firebase.firestore
            val currentUserEmail = Firebase.auth.currentUser!!.email   // 현재 사용자 uid (계정 구별하는 키)
            val friendId = search_id.text.toString()   // 검색할 친구 아이디

            // DB에서 아이디 검색해서 친구 이름 가져오는거
            firestore.collection("users")
                .whereEqualTo("id", friendId)  // 아이디 검색을 통해 친구 정보 가져옴
                .get()

                    //성공하면
                .addOnSuccessListener {
                    for(name in it!!.documents) {
                        Log.d("success", name["name"].toString())

                        firestore.collection("users")
                                .whereEqualTo("email", currentUserEmail)
                                .get()
                                .addOnSuccessListener {
                                    for (email in it!!.documents) {
                                        currentUserName = email["name"].toString()
                                    }
                                }

                        firestore.collection(name["email"] as String).document("Friend Request List")
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document = task.result
                                    if (document!!.exists()) {
                                        firestore.collection(name["email"] as String)
                                            .document("Friend Request List")
                                            .update(
                                                "friend_name",
                                                FieldValue.arrayUnion(currentUserName)
                                            )
                                            .addOnSuccessListener {
                                                toast("저장 성공")
                                                finish()
                                            }
                                    }
                                    else {
                                        val friendRequestList = arrayListOf<String>()    // 친구 이름 저장할 배열

                                        friendRequestList.add(currentUserName)
                                        toast(friendRequestList.toString())

                                        val friendName = hashMapOf(
                                            "friend_name" to friendRequestList)

                                        firestore.collection(name["email"] as String)
                                            .document("Friend Request List")
                                            .set(friendName)
                                            .addOnSuccessListener {
                                                toast("저장 성공")
                                            }
                                            .addOnFailureListener {
                                                toast("저장 실패")
                                            }
                                    }
                                }
                            }
                    }
                }
                    //실패하면
                .addOnFailureListener { exception ->
                    Log.d("fail", exception.toString())
                    toast("사용자 정보 없음")
                }
        }
    }
}