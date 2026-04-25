package com.example.siproapp.kas

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.*
import androidx.core.graphics.toColorInt
import com.example.siproapp.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.*
import java.text.NumberFormat
import java.util.*
import com.example.siproapp.berkas.BerkasActivity
class KasActivity : AppCompatActivity() {

    private lateinit var db: KasDatabase
    private lateinit var container: LinearLayout
    private lateinit var tvSaldo: TextView
    private lateinit var tvMasuk: TextView
    private lateinit var tvKeluar: TextView

    private lateinit var btnSemua: TextView
    private lateinit var btnMasuk: TextView
    private lateinit var btnKeluar: TextView

    private var currentFilter = "semua"

    private val data = mutableListOf<KasModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kas)

        db = KasDatabase.getDB(this)

        container = findViewById(R.id.containerKas)
        tvSaldo = findViewById(R.id.tvSaldo)
        tvMasuk = findViewById(R.id.tvMasuk)
        tvKeluar = findViewById(R.id.tvKeluar)

        btnSemua = findViewById(R.id.btnSemua)
        btnMasuk = findViewById(R.id.btnMasuk)
        btnKeluar = findViewById(R.id.btnKeluar)

        findViewById<ImageView>(R.id.btnTambah).setOnClickListener {
            showDialog(null)
        }

        setupFilter()
        setupNavbar()

        loadData()
    }

    private fun setupFilter() {

        fun reset() {
            btnSemua.setBackgroundColor("#EEEEEE".toColorInt())
            btnMasuk.setBackgroundColor("#EEEEEE".toColorInt())
            btnKeluar.setBackgroundColor("#EEEEEE".toColorInt())
        }

        btnSemua.setOnClickListener {
            currentFilter = "semua"
            reset()
            btnSemua.setBackgroundColor("#2196F3".toColorInt())
            render()
        }

        btnMasuk.setOnClickListener {
            currentFilter = "masuk"
            reset()
            btnMasuk.setBackgroundColor("#00A86B".toColorInt())
            render()
        }

        btnKeluar.setOnClickListener {
            currentFilter = "keluar"
            reset()
            btnKeluar.setBackgroundColor("#FF5722".toColorInt())
            render()
        }
    }

    private fun setupNavbar() {

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.selectedItemId = R.id.menu_kas

        bottomNav.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.menu_home -> {
                    startActivity(Intent(this, DashboardBPHActivity::class.java))
                    true
                }

                R.id.menu_kas -> true

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
    }

    private fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = db.kasDao().getAll()
            withContext(Dispatchers.Main) {
                data.clear()
                data.addAll(result)
                render()
            }
        }
    }

    private fun render() {
        container.removeAllViews()

        var masuk = 0
        var keluar = 0

        val inflater = LayoutInflater.from(this)

        for (item in data) {

            if (currentFilter == "masuk" && item.tipe != "masuk") continue
            if (currentFilter == "keluar" && item.tipe != "keluar") continue

            val view = inflater.inflate(R.layout.item_kas, container, false)

            val tvJudul = view.findViewById<TextView>(R.id.tvJudul)
            val tvJumlah = view.findViewById<TextView>(R.id.tvJumlah)
            val tvKategori = view.findViewById<TextView>(R.id.tvKategori)
            val btnEdit = view.findViewById<ImageView>(R.id.btnEdit)
            val btnHapus = view.findViewById<ImageView>(R.id.btnHapus)

            tvJudul.text = item.judul
            tvKategori.text = item.kategori

            if (item.tipe == "masuk") {
                tvJumlah.text = "+${format(item.jumlah)}"
                tvJumlah.setTextColor("#00A86B".toColorInt())
                masuk += item.jumlah
            } else {
                tvJumlah.text = "-${format(item.jumlah)}"
                tvJumlah.setTextColor("#FF0000".toColorInt())
                keluar += item.jumlah
            }

            btnEdit.setOnClickListener { showDialog(item) }

            btnHapus.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Hapus")
                    .setMessage("Yakin hapus?")
                    .setPositiveButton("Ya") { _, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            db.kasDao().delete(item)
                            loadData()
                        }
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }

            container.addView(view)
        }

        tvSaldo.text = format(masuk - keluar)
        tvMasuk.text = format(masuk)
        tvKeluar.text = format(keluar)
    }

    private fun showDialog(editData: KasModel?) {

        val view = layoutInflater.inflate(R.layout.bottom_sheet_tambah, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        val etJudul = view.findViewById<EditText>(R.id.etJudul)
        val etJumlah = view.findViewById<EditText>(R.id.etJumlah)
        val spType = view.findViewById<Spinner>(R.id.spType)
        val spKategori = view.findViewById<Spinner>(R.id.spKategori)

        val listType = listOf("Pemasukan", "Pengeluaran")
        val listKategori = listOf("Iuran", "Sponsor", "Perlengkapan", "Konsumsi")

        spType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listType)
        spKategori.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listKategori)

        if (editData != null) {
            etJudul.setText(editData.judul)
            etJumlah.setText(editData.jumlah.toString())
            spType.setSelection(if (editData.tipe == "masuk") 0 else 1)
        }

        view.findViewById<Button>(R.id.btnSimpan).setOnClickListener {

            val judul = etJudul.text.toString()
            val jumlah = etJumlah.text.toString().toIntOrNull() ?: 0
            val tipe = if (spType.selectedItem == "Pemasukan") "masuk" else "keluar"
            val kategori = spKategori.selectedItem.toString()

            if (judul.isEmpty() || jumlah == 0) return@setOnClickListener

            CoroutineScope(Dispatchers.IO).launch {
                if (editData == null) {
                    db.kasDao().insert(KasModel(judul = judul, jumlah = jumlah, tipe = tipe, kategori = kategori))
                } else {
                    db.kasDao().update(editData.copy(judul = judul, jumlah = jumlah, tipe = tipe, kategori = kategori))
                }
                loadData()
            }

            dialog.dismiss()
        }

        view.findViewById<Button>(R.id.btnBatal).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun format(num: Int): String {
        return NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(num)
    }
}