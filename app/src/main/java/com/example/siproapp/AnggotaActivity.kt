package com.example.siproapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.siproapp.Anggota.*
import com.example.siproapp.berkas.BerkasActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnggotaActivity : AppCompatActivity() {

    private lateinit var db: EventDatabase
    private lateinit var adapter: AnggotaAdapter
    private val list = mutableListOf<AnggotaModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anggota)

        val rv = findViewById<RecyclerView>(R.id.listAnggota)
        val btnTambah = findViewById<ImageView>(R.id.btnTambah)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        db = EventDatabase.getDB(this)

        adapter = AnggotaAdapter(list) { data ->
            deleteData(data)
        }

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        btnTambah.setOnClickListener {
            showDialog()
        }

        bottomNav.selectedItemId = R.id.menu_anggota

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.menu_home -> {
                    startActivity(Intent(this, DashboardBPHActivity::class.java))
                    true
                }

                R.id.menu_kas -> {
                    startActivity(Intent(this, com.example.siproapp.kas.KasActivity::class.java))
                    true
                }

                R.id.menu_berkas -> {
                    startActivity(Intent(this, BerkasActivity::class.java))
                    true
                }

                R.id.menu_anggota -> true

                R.id.menu_profil -> {
                    startActivity(Intent(this, ProfilActivity::class.java))
                    true
                }

                else -> false
            }
        }

        bottomNav.setOnItemReselectedListener { }

        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch {
            val data = withContext(Dispatchers.IO) {
                db.anggotaDao().getAll()
            }

            list.clear()
            list.addAll(data)
            adapter.notifyDataSetChanged()
        }
    }

    private fun deleteData(data: AnggotaModel) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                db.anggotaDao().delete(data)
            }

            loadData()
            Toast.makeText(this@AnggotaActivity, "Data dihapus", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDialog() {

        val view = layoutInflater.inflate(
            R.layout.bottom_sheet_anggota,
            findViewById(android.R.id.content),
            false
        )

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        val etNama = view.findViewById<EditText>(R.id.etNama)
        val etJabatan = view.findViewById<EditText>(R.id.etJabatan)
        val spStatus = view.findViewById<Spinner>(R.id.spStatus)
        val btnSimpan = view.findViewById<Button>(R.id.btnSimpan)

        val statusList = listOf("Aktif", "Pasif")

        val adapterStatus = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            statusList
        )
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spStatus.adapter = adapterStatus

        btnSimpan.setOnClickListener {

            val nama = etNama.text.toString().trim()
            val jabatan = etJabatan.text.toString().trim()
            val status = spStatus.selectedItem.toString()

            if (nama.isEmpty() || jabatan.isEmpty()) {
                Toast.makeText(this, "Isi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = AnggotaModel(
                nama = nama,
                jabatan = jabatan,
                status = status
            )

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    db.anggotaDao().insert(data)
                }

                loadData()
                dialog.dismiss()
                Toast.makeText(this@AnggotaActivity, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}