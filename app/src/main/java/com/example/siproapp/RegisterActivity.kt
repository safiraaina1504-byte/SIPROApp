package com.example.siproapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.siproapp.User.UserModel
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etNama = findViewById<EditText>(R.id.etNama)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val spRole = findViewById<Spinner>(R.id.spRole)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        val roles = arrayOf("Pilih Role", "BPH", "Anggota")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            roles
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRole.adapter = adapter

        btnRegister.setOnClickListener {

            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val role = spRole.selectedItem.toString()

            if (nama.isEmpty()) {
                etNama.error = "Nama wajib diisi"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Email tidak valid"
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error = "Password minimal 6 karakter"
                return@setOnClickListener
            }

            if (spRole.selectedItemPosition == 0) {
                Toast.makeText(this, "Pilih role dulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = EventDatabase.getDB(this)

            lifecycleScope.launch {

                val user = UserModel(
                    nama = nama,
                    email = email,
                    password = password,
                    role = role
                )

                db.userDao().insert(user)

                val sharedPref = getSharedPreferences("USER_DATA", MODE_PRIVATE)
                sharedPref.edit()
                    .putString("nama_lengkap", nama)
                    .putString("role", role)
                    .apply()

                Toast.makeText(this@RegisterActivity, "Registrasi berhasil", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}