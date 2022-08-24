package com.example.whyyou

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.friend.view.*
import kotlinx.android.synthetic.main.friend_request_list.*

class FriendRequestList:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friend_request_list)
        recView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val decoration = DividerItemDecoration(this, 1)
        recView.addItemDecoration(decoration)

        val datas = mutableListOf<FriendRequestData>()

        val friendRequestAdapter: FriendRequestAdapter = FriendRequestAdapter(this)

        val currentUserEmail = Firebase.auth.currentUser?.email

        val db = Firebase.firestore
        val docRef = db.collection(currentUserEmail!!).document("Friend Request List")

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("TAG", "data: ${snapshot.data}")

                val friendList = snapshot.data?.get("friend_name") as ArrayList<*>
                val listSize = friendList.size

                datas.clear()
                for (i in 0 until listSize) {
                    datas.apply {
                        add(FriendRequestData(friendList[i] as String))
                    }
                }

                friendRequestAdapter.replaceList(datas)
                recView.adapter = friendRequestAdapter

            } else {
                Log.d("TAG", "data: null")
            }
        }
    }
}