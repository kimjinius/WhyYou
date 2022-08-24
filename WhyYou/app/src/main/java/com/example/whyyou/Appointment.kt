package com.example.whyyou

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.friend.*
import kotlinx.android.synthetic.main.friend.recView
import kotlinx.android.synthetic.main.friend.view.*
import kotlinx.android.synthetic.main.friend_app_add.*
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NAME_SHADOWING")
class Appointment : Fragment() {

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.app, null).apply {
            recView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val decoration = DividerItemDecoration(context, 1)
            recView.addItemDecoration(decoration)

            val datas = mutableListOf<AppData>()
            val appAdapter : AppAdapter = AppAdapter(context)

            val currentUserEmail = Firebase.auth.currentUser?.email
            val db = Firebase.firestore
            val docRef = db.collection(currentUserEmail!!).document("App List").collection("App List")

            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val doc = task.result

                    datas.clear()
                    for (snapshot in doc!!) {
                        val appTitle = snapshot.data["Title"].toString()
                        val friendName = snapshot.data["Friend_name"].toString()
                        val appDate = snapshot.data["Date"].toString()
                        val appTime = snapshot.data["Time"].toString()
                        val appLocation = snapshot.data["Location"].toString()

                        val long_now = System.currentTimeMillis()
                        val t_date = Date(long_now)
                        val t_dateFormat = SimpleDateFormat("yyyyMMdd", Locale("ko", "KR"))
                        val str_date = t_dateFormat.format(t_date)

                        val app_date = Integer.parseInt(appDate.substring(0,4) + appDate.substring(5,7) + appDate.substring(8))
                        val cur_date = Integer.parseInt(str_date)

                        if (app_date >= cur_date) {
                            datas.apply {
                                add(AppData(appTitle, friendName, appDate, appTime, appLocation))
                            }
                        }

                        appAdapter.replaceList(datas)
                        recView.adapter = appAdapter
                    }
                }
            }

            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e)
                    return@addSnapshotListener
                }

                datas.clear()
                for (snapshot in snapshot!!) {
                    val appTitle = snapshot.data["Title"].toString()
                    val friendName = snapshot.data["Friend_name"].toString()
                    val appDate = snapshot.data["Date"].toString()
                    val appTime = snapshot.data["Time"].toString()
                    val appLocation = snapshot.data["Location"].toString()

                    val long_now = System.currentTimeMillis()
                    val t_date = Date(long_now)
                    val t_dateFormat = SimpleDateFormat("yyyyMMdd", Locale("ko", "KR"))
                    val str_date = t_dateFormat.format(t_date)

                    val app_date = Integer.parseInt(appDate.substring(0,4) + appDate.substring(5,7) + appDate.substring(8))
                    val cur_date = Integer.parseInt(str_date)

                    if (app_date >= cur_date) {
                        datas.apply {
                            add(AppData(appTitle, friendName, appDate, appTime, appLocation))
                        }
                    }

                    appAdapter.replaceList(datas)
                    recView.adapter = appAdapter
                }
            }
        }
    }
}