package com.example.siproapp.berkas

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.siproapp.*
import com.example.siproapp.kas.KasActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BerkasActivity : AppCompatActivity() {

    private lateinit var db: EventDatabase
    private lateinit var adapter: BerkasAdapter

    private val list = mutableListOf<BerkasModel>()
    private val listAll = mutableListOf<BerkasModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_berkas)

        val rv = findViewById<RecyclerView>(R.id.rvBerkas)
        val btnTambah = findViewById<ImageView>(R.id.btnTambah)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        val filterSemua = findViewById<LinearLayout>(R.id.filterSemua)
        val filterSurat = findViewById<LinearLayout>(R.id.filterSurat)
        val filterLaporan = findViewById<LinearLayout>(R.id.filterLaporan)
        val filterNotulen = findViewById<LinearLayout>(R.id.filterNotulen)

        db = EventDatabase.getDB(this)

        adapter = BerkasAdapter(
            list,
            onEdit = { showEdit(it) },
            onDelete = { deleteData(it) }
        )

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // biar ga nutup klik
        rv.isClickable = false
        rv.isFocusable = false

        btnTambah.setOnClickListener { showAdd() }

        // NAVBAR
        bottomNav.selectedItemId = R.id.menu_berkas
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this, DashboardBPHActivity::class.java))
                    true
                }
                R.id.menu_kas -> {
                    startActivity(Intent(this, KasActivity::class.java))
                    true
                }
                R.id.menu_berkas -> true
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

        // LABEL FILTER (BIAR GA SEMUA "SURAT")
        filterSurat.findViewById<TextView>(R.id.tvLabel).text = "Surat"
        filterLaporan.findViewById<TextView>(R.id.tvLabel).text = "Laporan"
        filterNotulen.findViewById<TextView>(R.id.tvLabel).text = "Notulen"

        // FILTER CLICK
        filterSemua.setOnClickListener {
            list.clear()
            list.addAll(listAll)
            adapter.notifyDataSetChanged()
        }

        filterSurat.setOnClickListener { filterData("Surat") }
        filterLaporan.setOnClickListener { filterData("Laporan") }
        filterNotulen.setOnClickListener { filterData("Notulen") }

        loadData()
    }

    // ======================
    // LOAD DATA + HITUNG
    // ======================
    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val data = db.berkasDao().getAll()

            withContext(Dispatchers.Main) {

                listAll.clear()
                listAll.addAll(data)

                list.clear()
                list.addAll(data)

                adapter.notifyDataSetChanged()

                // 🔥 HITUNG SEMUA
                val total = data.size
                val surat = data.count { it.jenis.equals("Surat", true) }
                val laporan = data.count { it.jenis.equals("Laporan", true) }
                val notulen = data.count { it.jenis.equals("Notulen", true) }

                // 🔥 SET KE UI
                findViewById<TextView>(R.id.tvJumlahSemua).text = total.toString()

                findViewById<LinearLayout>(R.id.filterSurat)
                    .findViewById<TextView>(R.id.tvJumlah).text = surat.toString()

                findViewById<LinearLayout>(R.id.filterLaporan)
                    .findViewById<TextView>(R.id.tvJumlah).text = laporan.toString()

                findViewById<LinearLayout>(R.id.filterNotulen)
                    .findViewById<TextView>(R.id.tvJumlah).text = notulen.toString()
            }
        }
    }

    private fun filterData(jenis: String) {
        val filtered = listAll.filter { it.jenis.equals(jenis, true) }

        list.clear()
        list.addAll(filtered)

        adapter.notifyDataSetChanged()
    }

    // ======================
    // TAMBAH DATA
    // ======================
    private fun showAdd() {
        val v = layoutInflater.inflate(R.layout.bottom_sheet_berkas, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(v)

        val et = v.findViewById<EditText>(R.id.etJudul)
        val sp = v.findViewById<Spinner>(R.id.spJenis)
        val btn = v.findViewById<Button>(R.id.btnSimpan)

        val jenis = listOf("Surat", "Laporan", "Notulen")
        sp.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, jenis)

        btn.setOnClickListener {

            val data = BerkasModel(
                judul = et.text.toString(),
                penulis = "Admin",
                tanggal = "Hari ini",
                ukuran = "File",
                jenis = sp.selectedItem.toString(),
                status = "Publish",
                fileUri = ""
            )

            lifecycleScope.launch(Dispatchers.IO) {
                db.berkasDao().insert(data)

                withContext(Dispatchers.Main) {
                    loadData() // 🔥 WAJIB BIAR ANGKA UPDATE
                }
            }

            dialog.dismiss()
        }

        dialog.show()
    }

    // ======================
    // EDIT
    // ======================
    private fun showEdit(old: BerkasModel) {
        val v = layoutInflater.inflate(R.layout.bottom_sheet_berkas, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(v)

        val et = v.findViewById<EditText>(R.id.etJudul)
        val sp = v.findViewById<Spinner>(R.id.spJenis)
        val btn = v.findViewById<Button>(R.id.btnSimpan)

        val jenis = listOf("Surat", "Laporan", "Notulen")
        sp.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, jenis)

        et.setText(old.judul)
        sp.setSelection(jenis.indexOf(old.jenis))

        btn.setOnClickListener {

            val updated = old.copy(
                judul = et.text.toString(),
                jenis = sp.selectedItem.toString()
            )

            lifecycleScope.launch(Dispatchers.IO) {
                db.berkasDao().update(updated)

                withContext(Dispatchers.Main) {
                    loadData()
                }
            }

            dialog.dismiss()
        }

        dialog.show()
    }

    // ======================
    // DELETE
    // ======================
    private fun deleteData(data: BerkasModel) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.berkasDao().delete(data)

            withContext(Dispatchers.Main) {
                loadData()
            }
        }
    }
}