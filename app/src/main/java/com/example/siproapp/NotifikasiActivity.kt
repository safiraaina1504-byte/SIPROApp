package com.example.siproapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class NotifikasiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifikasi)

        // 🔙 TOMBOL X (BALIK KE DASHBOARD)
        val btnClose = findViewById<ImageView>(R.id.btnClose)

        btnClose.setOnClickListener {
            startActivity(Intent(this, DashboardBPHActivity::class.java))
            finish()
        }
    }
}