package com.example.agendapersonal.interfaces

import com.example.agendapersonal.dataBase.Eventos

interface RecyclerEventoListener {
    fun onClick(evento: Eventos, position: Int)
    fun onLongClick(evento: Eventos, position: Int)
}