package com.example.whyyou

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.skt.Tmap.TMapMarkerItem
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView
import java.text.SimpleDateFormat
import java.util.*

class History : Fragment() {

    private var tMapView: TMapView? = null
    private val TMapAPIKey = "l7xxa9aa43c6f00e4c02b8812a7e4a5df6c9"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.history, null).apply {

            val linearLayoutTmap = findViewById(R.id.linearLayoutTmap) as LinearLayout
            tMapView = TMapView(getActivity()?.getApplicationContext())
            tMapView!!.setSKTMapApiKey(TMapAPIKey)
            linearLayoutTmap.addView(tMapView)
            //tMapView!!.setIconVisibility(true)
            tMapView!!.zoomLevel = 8

            tMapView!!.setCenterPoint(127.521489, 36.323549);

            val bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_baseline_location_on_24)

            val mapPoint = arrayListOf<HistoryMapPoint>()
            val firestore = Firebase.firestore
            val currentUserEmail = Firebase.auth.currentUser!!.email
            val colRef = firestore.collection(currentUserEmail!!).document("App List").collection("App List")

            colRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val doc = task.result

                    for (snapshot in doc!!) {
                        val appTitle = snapshot.data["Title"].toString()
                        val appDate = snapshot.data["Date"].toString()
                        val latitude = snapshot.data["Latitude"].toString().toDouble()
                        val longitude = snapshot.data["Longitude"].toString().toDouble()
                        Log.d(TAG, "$appTitle, $appDate, $latitude, $longitude")

                        val long_now = System.currentTimeMillis()
                        val t_date = Date(long_now)
                        val t_dateFormat = SimpleDateFormat("yyyyMMdd", Locale("ko", "KR"))
                        val str_date = t_dateFormat.format(t_date)

                        val app_date = Integer.parseInt(appDate.substring(0,4) + appDate.substring(5,7) + appDate.substring(8))
                        val cur_date = Integer.parseInt(str_date)

                        if (app_date < cur_date) {
                            mapPoint.apply {
                                add(HistoryMapPoint(appTitle, appDate, longitude, latitude))
                            }
                        }
                    }

                    for (i in mapPoint.indices){
                        val markerItem1 = TMapMarkerItem()
                        val tMapPoint1 = TMapPoint(mapPoint[i].latitude, mapPoint[i].longitude)

                        markerItem1.icon = bitmap

                        markerItem1.setPosition(0.5f, 1.0f) // 마커의 중심점을 중앙, 하단으로 설정
                        markerItem1.tMapPoint = tMapPoint1 // 마커의 좌표 지정
                        markerItem1.name = mapPoint[i].name // 마커의 타이틀 지정

                        markerItem1.setCanShowCallout(true);         // Balloon View 사용
                        markerItem1.setCalloutTitle(mapPoint[i].name);      // Main Message
                        markerItem1.setCalloutSubTitle(mapPoint[i].date);


                        tMapView!!.addMarkerItem("markerItem$i", markerItem1) // 지도에 마커 추가
                    }
                }
            }




        }
    }
}