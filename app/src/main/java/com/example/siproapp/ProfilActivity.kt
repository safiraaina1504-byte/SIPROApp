package com.example.siproapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ProfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val etNama = findViewById<EditText>(R.id.etNama)
        val etJabatan = findViewById<EditText>(R.id.etJabatan)
        val etTanggal = findViewById<EditText>(R.id.etTanggal)
        val etAlamat = findViewById<EditText>(R.id.etAlamat)

        val btnSimpan = findViewById<Button>(R.id.btnSimpan)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val sharedPref = getSharedPreferences("USER_DATA", MODE_PRIVATE)

        etNama.setText(sharedPref.getString("nama_lengkap", ""))
        etJabatan.setText(sharedPref.getString("jabatan", ""))
        etTanggal.setText(sharedPref.getString("tanggal", ""))
        etAlamat.setText(sharedPref.getString("alamat", ""))

        etTanggal.setOnClickListener {
            val calendar = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, year, month, day ->
                    etTanggal.setText("$day/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnSimpan.setOnClickListener {

            val nama = etNama.text.toString()
            val jabatan = etJabatan.text.toString()
            val tanggal = etTanggal.text.toString()
            val alamat = etAlamat.text.toString()

            if (nama.isEmpty() || jabatan.isEmpty() || tanggal.isEmpty() || alamat.isEmpty()) {
                Toast.makeText(this, "Isi semua data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sharedPref.edit()
                .putString("nama_lengkap", nama)
                .putString("jabatan", jabatan)
                .putString("tanggal", tanggal)
                .putString("alamat", alamat)
                .apply()

            Toast.makeText(this, "Profil tersimpan", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}