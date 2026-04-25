package com.example.siproapp.Anggota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.siproapp.R

class AnggotaAdapter(
    private val list: MutableList<AnggotaModel>,
    private val onDelete: (AnggotaModel) -> Unit
) : RecyclerView.Adapter<AnggotaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvJabatan: TextView = view.findViewById(R.id.tvJabatan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anggota, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.tvNama.text = data.nama
        holder.tvJabatan.text = data.jabatan

        holder.itemView.setOnLongClickListener {
            onDelete(data)
            true
        }
    }
}