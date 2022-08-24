package com.example.whyyou

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.friend_request_listview.view.*

@Suppress("NAME_SHADOWING")
class FriendRequestAdapter(private val context: Context) : RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>(){

    private var datas = mutableListOf<FriendRequestData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.friend_request_listview, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val friendName = itemView.request_friend_name

        fun bind(item: FriendRequestData) {
            friendName.text = item.name

            itemView.request_ok.setOnClickListener {
                Toast.makeText(context, "수락", Toast.LENGTH_SHORT).show()
                myFriendList(item.name)
                friendFriendList(item.name)

                // 각자 리스트에 이름 추가 후 요청 목록에서 삭제
                removeRequestItem(item.name)
                datas.remove(item)
                notifyDataSetChanged()
            }

            itemView.request_cancel.setOnClickListener {
                removeRequestItem(item.name)
                datas.remove(item)
                notifyDataSetChanged()
                Toast.makeText(context, "삭제 성공", Toast.LENGTH_SHORT).show()
            }

            itemView.setOnClickListener {
                val intent = Intent(context,FriendAppAdd::class.java)
                intent.putExtra("friend_name", item.name)
                context.startActivity(intent)
            }
        }
    }

    fun replaceList(newList: MutableList<FriendRequestData>) {
        datas = newList.toMutableList()
        notifyDataSetChanged()
    }

    // 현재 사용자 친구 목록에 추가
    fun myFriendList(name: String) {
        val firestore = Firebase.firestore
        val currentUserEmail = Firebase.auth.currentUser!!.email

        firestore.collection(currentUserEmail!!).document("Friend List")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document!!.exists()) {
                        firestore.collection(currentUserEmail).document("Friend List")
                            .update("friend_name", FieldValue.arrayUnion(name))
                            .addOnSuccessListener {
                                Toast.makeText(context, "저장 성공", Toast.LENGTH_SHORT).show()
                            }
                    }
                    else {
                        val friendList = arrayListOf<String>()    // 친구 이름 저장할 배열

                        friendList.add(name)
                        val friendName = hashMapOf("friend_name" to friendList)

                        firestore.collection(currentUserEmail).document("Friend List")
                            .set(friendName)
                            .addOnSuccessListener {
                                Toast.makeText(context, "저장 성공", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show()
            }
    }

    // 친구의 친구 목록에 내 이름 추가
    fun friendFriendList(name: String) {
        lateinit var currentUserName : String
        lateinit var friendEmail : String

        val firestore = Firebase.firestore
        val currentUserEmail = Firebase.auth.currentUser!!.email

        // 내 이름 가져오기
        firestore.collection("users")
                .whereEqualTo("email", currentUserEmail)
                .get()
                .addOnSuccessListener {
                    for (email in it!!.documents) {
                        currentUserName = email["name"].toString()
                    }
                }

        // 친구 이메일 가져오기
        firestore.collection("users")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener {
                    for (name in it!!.documents) {
                        friendEmail = name["email"].toString()
                        Toast.makeText(context, friendEmail, Toast.LENGTH_SHORT).show()
                    }

                    // 친구 db에 내 이름 추가
                    firestore.collection(friendEmail).document("Friend List")
                            .get()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val document = task.result
                                    if (document!!.exists()){
                                        firestore.collection(friendEmail).document("Friend List")
                                                .update("friend_name", FieldValue.arrayUnion(currentUserName))
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "저장 성공", Toast.LENGTH_SHORT).show()
                                                }
                                    }
                                    else {
                                        val friendList = arrayListOf<String>()
                                        friendList.add(currentUserName)

                                        val friendName = hashMapOf(
                                                "friend_name" to friendList
                                        )

                                        firestore.collection(friendEmail).document("Friend List")
                                                .set(friendName)
                                                .addOnSuccessListener {
                                                    Toast.makeText(context, "저장 성공", Toast.LENGTH_SHORT).show()
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show()
                                                }
                                    }
                                }
                            }
                }
    }

    fun removeRequestItem(name: String) {
        val firestore = Firebase.firestore
        val currentUserEmail = Firebase.auth.currentUser!!.email

        val ref = firestore.collection(currentUserEmail!!).document("Friend Request List")
        ref.update("friend_name",FieldValue.arrayRemove(name))
                .addOnSuccessListener {
                    Toast.makeText(context, "db 삭제 성공", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "db 삭제 실패", Toast.LENGTH_SHORT).show()
                }
    }
}