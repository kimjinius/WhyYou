package com.example.whyyou

import android.annotation.SuppressLint
import android.app.PendingIntent.getActivity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.skt.Tmap.TMapMarkerItem
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView
import kotlinx.android.synthetic.main.app_search_location.*
import org.jetbrains.anko.toast
import java.io.IOException
import java.util.*
import kotlin.properties.Delegates

class AppSearchLocation : AppCompatActivity() {

    lateinit var location : String
    var latitude2 by Delegates.notNull<Double>()
    var longitude2 by Delegates.notNull<Double>()

    private var tMapView: TMapView? = null
    private val TMapAPIKey = "l7xxa9aa43c6f00e4c02b8812a7e4a5df6c9"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_search_location)

        val linearLayoutTmap = findViewById(R.id.linearLayoutTmap1) as LinearLayout
        tMapView = TMapView(this)
        tMapView!!.setSKTMapApiKey(TMapAPIKey)
        linearLayoutTmap.addView(tMapView)

        // 마커 아이콘
        val bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_baseline_location_on_24)

        searchLocationBtn.setOnClickListener {
            //지오 코딩 작업 수행하는 객체 생성
            val geocoder = Geocoder(this, Locale.KOREA)

            location = locationEditText.text.toString()

            //지오코더에게 지오코딩작업 요청
            try {
                val addresses = geocoder.getFromLocationName(location, 3) //최대 3개까지 받음.
                val buffer = StringBuffer()
                for (t in addresses) {
                    buffer.append(
                            """
                    ${t.latitude}, ${t.longitude}
                    
                    """.trimIndent()

                    )

                    latitude2 = t.latitude     // 입력한 목적지의 위도
                    longitude2 = t.longitude   // 입력한 목적지의 경도
                    toast("$latitude2, $longitude2")

                }
            }
            // 검색 실패하였을 때 출력하는 토스트 메시지
            catch (e: IOException) {
                Toast.makeText(this, "검색 실패", Toast.LENGTH_LONG).show()
            }

            val markerItem1 = TMapMarkerItem()
            val tMapPoint1 = TMapPoint(latitude2, longitude2)

            markerItem1.icon = bitmap
            markerItem1.setPosition(0.5f, 1.0f) // 마커의 중심점을 중앙, 하단으로 설정
            markerItem1.tMapPoint = tMapPoint1 // 마커의 좌표 지정
            markerItem1.name = "마커" // 마커의 타이틀 지정
            tMapView!!.addMarkerItem("markerItem1", markerItem1) // 지도에 마커 추가
            tMapView!!.setCenterPoint(longitude2, latitude2)

        }

        btn_searchok.setOnClickListener {
            val intent = Intent()
            intent.putExtra("location", location)
            intent.putExtra("latitude", latitude2.toString())
            intent.putExtra("longitude", longitude2.toString())
            setResult(RESULT_OK, intent)
            Log.d(TAG,"데이터 보냄")
            finish()
        }

    }
}