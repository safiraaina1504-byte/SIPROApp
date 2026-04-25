package com.example.siproapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)

        val sharedPref = getSharedPreferences("USER_DATA", MODE_PRIVATE)

        // AUTO LOGIN
        if (sharedPref.getBoolean("isLogin", false)) {
            val role = sharedPref.getString("role", "")

            val intent = if (role == "Anggota") {
                Intent(this, DashboardAnggotaActivity::class.java)
            } else {
                Intent(this, DashboardBPHActivity::class.java)
            }

            startActivity(intent)
            finish()
        }

        tvWelcome.text = "Selamat datang di SIPRO"

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty()) {
                etEmail.error = "Email wajib diisi"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "Password wajib diisi"
                return@setOnClickListener
            }

            val db = EventDatabase.getDB(this)

            lifecycleScope.launch {

                val user = db.userDao().login(email, password)

                if (user != null) {

                    sharedPref.edit()
                        .putBoolean("isLogin", true)
                        .putString("nama_lengkap", user.nama)
                        .putString("role", user.role)
                        .apply()

                    Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()

                    val intent = if (user.role == "Anggota") {
                        Intent(this@LoginActivity, DashboardAnggotaActivity::class.java)
                    } else {
                        Intent(this@LoginActivity, DashboardBPHActivity::class.java)
                    }

                    startActivity(intent)
                    finish()

                } else {
                    Toast.makeText(this@LoginActivity, "Email atau Password salah", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}