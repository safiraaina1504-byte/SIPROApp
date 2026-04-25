package com.example.siproapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.siproapp.berkas.BerkasActivity
import com.example.siproapp.kas.KasActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class DashboardBPHActivity : AppCompatActivity() {

    private lateinit var db: EventDatabase
    private lateinit var adapter: EventAdapter
    private val list = mutableListOf<EventModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_bph)

        val rv = findViewById<RecyclerView>(R.id.rvEvent)
        val btnTambah = findViewById<ImageView>(R.id.btnTambahEvent)
        val btnNotif = findViewById<ImageView>(R.id.btnNotif)
        val btnLogout = findViewById<ImageView>(R.id.btnLogout)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        val tvNama = findViewById<TextView>(R.id.tvNama)
        val tvRole = findViewById<TextView>(R.id.tvRole)

        db = EventDatabase.getDB(this)

        // ambil data user
        val pref = getSharedPreferences("USER_DATA", MODE_PRIVATE)
        val nama = pref.getString("nama_lengkap", "")
        val role = pref.getString("role", "")

        tvNama.text = if (!nama.isNullOrEmpty()) "Halo, $nama" else "Halo, User"
        tvRole.text = if (!role.isNullOrEmpty()) role else "-"

        adapter = EventAdapter(
            list,
            onEdit = { showEditEvent(it) },
            onDelete = { deleteData(it) }
        )

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        btnTambah.setOnClickListener { showTambahEvent() }

        btnNotif.setOnClickListener {
            startActivity(Intent(this, NotifikasiActivity::class.java))
        }

        btnLogout.setOnClickListener {
            pref.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        bottomNav.selectedItemId = R.id.menu_home

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {

                R.id.menu_home -> true

                R.id.menu_kas -> {
                    startActivity(Intent(this, KasActivity::class.java))
                    true
                }

                R.id.menu_berkas -> {
                    startActivity(Intent(this, BerkasActivity::class.java))
                    true
                }

                R.id.menu_anggota -> {
                    startActivity(Intent(this, AnggotaActivity::class.java))
                    true
                }

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

    // =========================
    // 🔥 TAMBAH EVENT
    // =========================
    private fun showTambahEvent() {

        val view = layoutInflater.inflate(R.layout.bottom_sheet_event, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        val etNama = view.findViewById<EditText>(R.id.etNama)
        val etTanggal = view.findViewById<EditText>(R.id.etTanggal)
        val etJam = view.findViewById<EditText>(R.id.etJam)
        val etKebutuhan = view.findViewById<EditText>(R.id.etKebutuhan)
        val spStatus = view.findViewById<Spinner>(R.id.spStatus)
        val btnSimpan = view.findViewById<Button>(R.id.btnSimpan)
        val btnBatal = view.findViewById<Button>(R.id.btnBatal)

        val statusList = listOf("Pending", "Selesai")
        spStatus.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, statusList)

        // DATE PICKER
        etTanggal.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                etTanggal.setText(String.format("%02d/%02d/%d", d, m + 1, y))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // TIME PICKER
        etJam.setOnClickListener {
            val cal = Calendar.getInstance()
            TimePickerDialog(this, { _, h, min ->
                etJam.setText(String.format("%02d:%02d", h, min))
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }

        btnBatal.setOnClickListener { dialog.dismiss() }

        btnSimpan.setOnClickListener {

            val nama = etNama.text.toString()
            val tanggal = etTanggal.text.toString()
            val jam = etJam.text.toString()
            val kebutuhan = etKebutuhan.text.toString()

            if (nama.isEmpty() || tanggal.isEmpty()) {
                Toast.makeText(this, "Lengkapi data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = EventModel(
                nama = nama,
                tanggal = tanggal,
                jam = jam,
                kebutuhan = kebutuhan,
                status = spStatus.selectedItem.toString()
            )

            lifecycleScope.launch(Dispatchers.IO) {
                db.eventDao().insert(data)
                withContext(Dispatchers.Main) {
                    loadData()
                    Toast.makeText(this@DashboardBPHActivity, "Event ditambahkan", Toast.LENGTH_SHORT).show()
                }
            }

            dialog.dismiss()
        }

        dialog.show()
    }

    // =========================
    // 🔥 EDIT EVENT
    // =========================
    private fun showEditEvent(old: EventModel) {

        val view = layoutInflater.inflate(R.layout.bottom_sheet_event, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        val etNama = view.findViewById<EditText>(R.id.etNama)
        val etTanggal = view.findViewById<EditText>(R.id.etTanggal)
        val etJam = view.findViewById<EditText>(R.id.etJam)
        val etKebutuhan = view.findViewById<EditText>(R.id.etKebutuhan)
        val spStatus = view.findViewById<Spinner>(R.id.spStatus)
        val btnSimpan = view.findViewById<Button>(R.id.btnSimpan)
        val btnBatal = view.findViewById<Button>(R.id.btnBatal)

        val statusList = listOf("Pending", "Selesai")
        spStatus.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, statusList)

        etNama.setText(old.nama)
        etTanggal.setText(old.tanggal)
        etJam.setText(old.jam)
        etKebutuhan.setText(old.kebutuhan)
        spStatus.setSelection(statusList.indexOf(old.status))

        btnBatal.setOnClickListener { dialog.dismiss() }

        btnSimpan.setOnClickListener {

            val updated = old.copy(
                nama = etNama.text.toString(),
                tanggal = etTanggal.text.toString(),
                jam = etJam.text.toString(),
                kebutuhan = etKebutuhan.text.toString(),
                status = spStatus.selectedItem.toString()
            )

            lifecycleScope.launch(Dispatchers.IO) {
                db.eventDao().update(updated)
                withContext(Dispatchers.Main) {
                    loadData()
                    Toast.makeText(this@DashboardBPHActivity, "Event diupdate", Toast.LENGTH_SHORT).show()
                }
            }

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {

            val dataEvent = db.eventDao().getAll()
            val dataAnggota = db.anggotaDao().getAll()

            val selesai = dataEvent.count { it.status == "Selesai" }
            val pending = dataEvent.count { it.status == "Pending" }

            withContext(Dispatchers.Main) {

                list.clear()
                list.addAll(dataEvent)
                adapter.notifyDataSetChanged()

                findViewById<TextView>(R.id.tvJumlahEvent).text = dataEvent.size.toString()
                findViewById<TextView>(R.id.tvSelesai).text = selesai.toString()
                findViewById<TextView>(R.id.tvPending).text = pending.toString()
                findViewById<TextView>(R.id.tvTotalAnggota).text = dataAnggota.size.toString()
            }
        }
    }

    private fun deleteData(data: EventModel) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.eventDao().delete(data)
            withContext(Dispatchers.Main) {
                loadData()
                Toast.makeText(this@DashboardBPHActivity, "Data dihapus", Toast.LENGTH_SHORT).show()
            }
        }
    }
}