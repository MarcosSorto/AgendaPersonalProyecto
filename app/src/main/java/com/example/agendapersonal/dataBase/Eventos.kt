package com.example.agendapersonal.dataBase


data class Eventos(
    val nombreEvento:String="",
    val descripcionEvento:String="",
    val anioEvento:Int = 0,
    val mesEvento:Int = 0,
    val diaEvento:Int = 0,
    val horaEvento:Int = 0,
    val minutoEvento:Int = 0,
    val UrlEvento:String="",
    val registradoPor:String="Marcos"
)