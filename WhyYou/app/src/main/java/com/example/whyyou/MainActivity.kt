package com.example.whyyou

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.api.ResourceDescriptor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.header.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Firebase.auth.currentUser == null) {
            startActivity<LoginActivity>()
            finish()
        }

        val after_login_viewpager = findViewById<ViewPager>(R.id.dlg_date_viewpager)
        val after_login_tablayout = findViewById<TabLayout>(R.id.dlg_date_tablayout)

        val adapter = ViewpagerAdapter(supportFragmentManager)
        adapter.addFragment(Friend(), "Friend")
        adapter.addFragment(Appointment(), "Appointment")
        adapter.addFragment(History(), "History")
        after_login_viewpager.adapter = adapter
        after_login_tablayout.setupWithViewPager(after_login_viewpager)

        after_login_tablayout.getTabAt(0)?.setIcon(R.drawable.friends_1)
        after_login_tablayout.getTabAt(1)?.setIcon(R.drawable.appointment)
        after_login_tablayout.getTabAt(2)?.setIcon(R.drawable.history_map)

        val toolbar: Toolbar = findViewById(R.id.toolbar) // toolBar를 통해 App Bar 생성
        setSupportActionBar(toolbar) // 툴바 적용

        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24) // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게

        // 네비게이션 드로어 생
        drawerLayout = findViewById(R.id.drawer_layout)

        // 네비게이션 드로어 내에있는 화면의 이벤트를 처리하기 위해 생성
        navigationView = findViewById(R.id.nav_view)

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.logout -> {
                    Firebase.auth.signOut()
                    startActivity<LoginActivity>()
                    true
                }R.id.friendRequest -> {
                    startActivity<FriendRequestList>()
                    true
                }else -> false
            }
        }
    }

    // 툴바 메뉴 버튼이 클릭 됐을 때 실행하는 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // 클릭한 툴바 메뉴 아이템 id 마다 다르게 실행하도록 설정
        when(item.itemId){
            android.R.id.home->{
                // 햄버거 버튼 클릭시 네비게이션 드로어 열기
                drawerLayout.openDrawer(GravityCompat.START)

                val firestore = Firebase.firestore
                val currentUserEmail = Firebase.auth.currentUser!!.email
                firestore.collection("users")
                        .whereEqualTo("email", currentUserEmail)
                        .get()
                        .addOnSuccessListener {
                            for (email in it!!.documents) {
                                profile_id.text = email["name"].toString()
                            }
                        }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}