package com.example.agendapersonal.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.agendapersonal.R
import com.example.agendapersonal.dataBase.Eventos
import com.example.agendapersonal.interfaces.RecyclerEventoListener
import kotlinx.android.synthetic.main.template_evento_items.view.*

class EventoAdapter(private val eventos: List<Eventos>, private val listener: RecyclerEventoListener)
    : RecyclerView.Adapter<EventoAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventoAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.template_evento_items, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = eventos.size

    override fun onBindViewHolder(holder: EventoAdapter.ViewHolder, position: Int){
        holder.bind(eventos[position], listener)

    }

    class ViewHolder(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(note: Eventos, listener: RecyclerEventoListener) = with(itemView) {
            //itemView.txtNombreEvento.text=note.nombreEvento
            txtTitulo.text=note.nombreEvento
            txtDescripcionV.text=note.descripcionEvento
            txtFechaV.text = ("${note.anioEvento} /${note.mesEvento} /${note.diaEvento} ")
            txtHoraV.text=("${note.horaEvento}:${note.minutoEvento}")
            // Implementar los eventos
            setOnClickListener { listener.onClick(note, adapterPosition) }
        }
    }
}