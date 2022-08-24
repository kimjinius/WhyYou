package com.example.whyyou

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ClipDrawable.VERTICAL
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.friend.*
import kotlinx.android.synthetic.main.friend.view.*
import org.jetbrains.anko.toast

class Friend : Fragment() {
    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.friend, null).apply {
            recView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val decoration = DividerItemDecoration(context, 1)
            recView.addItemDecoration(decoration)

            // 친구추가 버튼 눌렀을 때 친구 요청 화면으로 이동
            btnFriendAdd.setOnClickListener {
                val newIntent = Intent(context, FriendRequest::class.java)
                startActivity(newIntent)
            }

            val datas = mutableListOf<FriendData>()
            val friendAdapter : FriendAdapter = FriendAdapter(context)
            val currentUserEmail = Firebase.auth.currentUser?.email
            val db = Firebase.firestore
            val docRef = db.collection(currentUserEmail!!).document("Friend List")

            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val doc = task.result

                    if (doc!!.exists()) {
                        val friendList = doc.data?.get("friend_name") as ArrayList<*>
                        val listSize = friendList.size

                        datas.clear()
                        for (i in 0 until listSize) {
                            datas.apply {
                                add(FriendData(friendList[i] as String))
                            }
                        }

                        friendAdapter.replaceList(datas)
                        recView.adapter = friendAdapter
                    }
                    else {
                        datas.clear()
                        friendAdapter.replaceList(datas)
                        recView.adapter = friendAdapter
                    }
                }
            }

            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("TAG", "data: ${snapshot.data}")
                    //toast(snapshot.data?.get("friend_name").toString())

                    val friendList = snapshot.data?.get("friend_name") as ArrayList<*>
                    val listSize = friendList.size

                    datas.clear()
                    for (i in 0 until listSize) {
                        datas.apply {
                            add(FriendData(friendList[i] as String))
                        }
                    }

                    friendAdapter.replaceList(datas)
                    recView.adapter = friendAdapter

                } else {
                    Log.d("TAG", "data: null")
                }
            }
        }
    }
}