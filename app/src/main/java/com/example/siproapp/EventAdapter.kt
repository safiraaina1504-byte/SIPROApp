package com.example.siproapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(
    private val list: MutableList<EventModel>,
    private val onEdit: (EventModel) -> Unit,
    private val onDelete: (EventModel) -> Unit
) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val tvJam: TextView = itemView.findViewById(R.id.tvJam)
        val tvKebutuhan: TextView = itemView.findViewById(R.id.tvKebutuhan)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        holder.tvNama.text = data.nama
        holder.tvTanggal.text = data.tanggal
        holder.tvJam.text = data.jam
        holder.tvKebutuhan.text = "${data.kebutuhan} peserta"
        holder.tvStatus.text = data.status

        // warna status
        when (data.status) {
            "Selesai" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_hijau)
                holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"))
            }
            "Pending" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_orange)
                holder.tvStatus.setTextColor(Color.parseColor("#FF6F00"))
            }
        }

        // 🔥 FIX CLICK (PASTI KEKLIK)
        holder.btnEdit.setOnClickListener {
            onEdit(data)
        }

        holder.btnDelete.setOnClickListener {
            onDelete(data)
        }
    }
}