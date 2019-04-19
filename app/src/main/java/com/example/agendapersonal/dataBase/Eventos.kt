package com.example.agendapersonal.dataBase

import com.google.type.Date
import java.sql.Time
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.concurrent.timer

data class Eventos(
    val nombreEvento:String="",
    val descripcionEvento:String="",
    val fechaEvento:Date = Date.getDefaultInstance(),
    val horaEvento:Int=0
)