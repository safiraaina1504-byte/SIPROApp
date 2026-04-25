package com.example.siproapp

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val tvNama: TextView = itemView.findViewById(R.id.tvNama)
    val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
    val tvJam: TextView = itemView.findViewById(R.id.tvJam)
    val tvKebutuhan: TextView = itemView.findViewById(R.id.tvKebutuhan)
    val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

    val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
    val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
}