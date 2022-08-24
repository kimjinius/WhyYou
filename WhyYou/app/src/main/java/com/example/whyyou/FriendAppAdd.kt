package com.example.whyyou

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.friend_app_add.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NAME_SHADOWING", "DEPRECATION")
class FriendAppAdd : AppCompatActivity() {

    private var myCalendar = Calendar.getInstance()

    private var myDatePicker =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateLabel()
            }

    lateinit var latitude : String
    lateinit var longitude : String
    lateinit var location : String
    lateinit var currentUserName: String
    lateinit var friendEmail: String
    lateinit var friendAppList: HashMap<String, String>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.friend_app_add)

        friendname.text = intent.getStringExtra("friend_name")

        app_addclose.setOnClickListener {
            finish()
        }

        val et_Date = findViewById<View>(R.id.app_datepick) as EditText
        et_Date.setOnClickListener {
            DatePickerDialog(
                    this,
                    myDatePicker,
                    myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        val et_time = findViewById<View>(R.id.app_timepick) as EditText
        et_time.setOnClickListener {
            val mcurrentTime = Calendar.getInstance()
            val hour = mcurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mcurrentTime[Calendar.MINUTE]

            val mTimePicker: TimePickerDialog = TimePickerDialog(this,
                    { timePicker, selectedHour, selectedMinute ->
                        var selectedHour = selectedHour
                        et_time.setText(String.format("%02d", selectedHour) + "시 " + String.format("%02d", selectedMinute) + "분")
                    }, hour, minute, false
            ) // true의 경우 24시간 형식의 TimePicker 출현
            mTimePicker.setTitle("Select Time")
            mTimePicker.show()
        }

        search_location.setOnClickListener {
            val intent = Intent(this, AppSearchLocation::class.java)
            startActivityForResult(intent, CONTEXT_INCLUDE_CODE)
        }

        val firestore = Firebase.firestore
        val currentUserEmail = Firebase.auth.currentUser!!.email   // 현재 사용자 email (계정 구별하는 키)

        app_addok.setOnClickListener {

            val currentUserAppList = hashMapOf(
                    "Title" to app_title.text.toString(),
                    "Friend_name" to friendname.text.toString(),
                    "Date" to et_Date.text.toString(),
                    "Time" to et_time.text.toString(),
                    "Latitude" to latitude,
                    "Longitude" to longitude,
                    "Location" to location
            )

            // 현재 사용자 DB에 약속 저장
            firestore.collection(currentUserEmail!!).document("App List")
                    .collection("App List").add(currentUserAppList)
                    .addOnSuccessListener {
                        toast("내 db 저장 성공")
                    }
                    .addOnFailureListener {
                        toast("내 db 저장 실패")
                    }

            // 친구 DB에 약속 저장
            // 내 이름 가져오기
            firestore.collection("users")
                    .whereEqualTo("email", currentUserEmail)
                    .get()
                    .addOnSuccessListener {
                        for (email in it!!.documents) {
                            currentUserName = email["name"].toString()

                            friendAppList = hashMapOf(
                                    "Title" to app_title.text.toString(),
                                    "Friend_name" to currentUserName,
                                    "Date" to et_Date.text.toString(),
                                    "Time" to et_time.text.toString(),
                                    "Latitude" to latitude,
                                    "Longitude" to longitude,
                                    "Location" to location
                            )
                        }
                    }

            val friendName = friendname.text.toString()
            firestore.collection("users")
                    .whereEqualTo("name", friendName)
                    .get()
                    .addOnSuccessListener {
                        for (name in it!!.documents) {
                            friendEmail = name["email"].toString()
                        }

                        firestore.collection(friendEmail).document("App List")
                                .collection("App List").add(friendAppList)
                                .addOnSuccessListener {
                                    toast("친구 db 저장 성공")
                                }.addOnFailureListener {
                                    toast("친구 db 저장 실패")
                                }
                    }
        }
    }

    private fun updateLabel() {
        val myFormat = "yyyy/MM/dd" // 출력형식   2018/11/28
        val sdf = SimpleDateFormat(myFormat, Locale.KOREA)
        val et_date = findViewById<View>(R.id.app_datepick) as EditText
        et_date.setText(sdf.format(myCalendar.time))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CONTEXT_INCLUDE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                latitude = data!!.getStringExtra("latitude")!!
                longitude = data.getStringExtra("longitude")!!
                location = data.getStringExtra("location")!!
                toast("$latitude, $longitude")

                search_location.text = location
            }
        }
    }

}