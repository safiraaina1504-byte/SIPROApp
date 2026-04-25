package com.example.siproapp.berkas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.siproapp.R

class BerkasAdapter(
    private val list: MutableList<BerkasModel>,
    private val onEdit: (BerkasModel) -> Unit,
    private val onDelete: (BerkasModel) -> Unit
) : RecyclerView.Adapter<BerkasAdapter.ViewHolder>() {

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvJudul: TextView = v.findViewById(R.id.tvJudul)
        val tvInfo: TextView = v.findViewById(R.id.tvInfo)
        val tvJenis: TextView = v.findViewById(R.id.tvJenis)
        val btnEdit: ImageView = v.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = v.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_berkas, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        // isi data
        holder.tvJudul.text = data.judul
        holder.tvInfo.text = "${data.penulis} • ${data.tanggal} • ${data.ukuran}"

        // 🔥 FIX UTAMA (BIAR GA SEMUA SURAT)
        holder.tvJenis.text = data.jenis

        // 🔥 CLICK AMAN (ANTI BUG POSITION)
        holder.btnEdit.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onEdit(list[pos])
            }
        }

        holder.btnDelete.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onDelete(list[pos])
            }
        }
    }
}