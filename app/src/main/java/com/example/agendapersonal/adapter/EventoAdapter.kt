package com.example.agendapersonal.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onBindViewHolder(holder: EventoAdapter.ViewHolder, position: Int) = holder.bind(eventos[position], listener)

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(note: Eventos, listener: RecyclerEventoListener) = with(itemView) {
            txtTitulo.text = note.nombreEvento

            // Implementar los eventos
            setOnClickListener { listener.onClick(note, adapterPosition) }
        }

    }

}