package com.example.siproapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val btnBPH = findViewById<LinearLayout>(R.id.btnBPH)
        val btnAnggota = findViewById<LinearLayout>(R.id.btnAnggota)

        // Klik BPH
        btnBPH.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("ROLE", "BPH")
            startActivity(intent)
        }

        // Klik Anggota
        btnAnggota.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("ROLE", "Anggota")
            startActivity(intent)
        }
    }
}