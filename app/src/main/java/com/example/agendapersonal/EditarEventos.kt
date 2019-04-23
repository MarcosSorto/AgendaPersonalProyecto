package com.example.agendapersonal

import android.annotation.SuppressLint


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast.*
import com.example.agendapersonal.adapter.EventoAdapter
import com.example.agendapersonal.dataBase.Eventos
import com.example.agendapersonal.interfaces.RecyclerEventoListener
import com.google.firebase.firestore.*



import java.util.EventListener
import kotlin.collections.ArrayList



class EditarEventos : androidx.fragment.app.Fragment() {

    //variables necesarias para conexion
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var noteDBRef: CollectionReference
    private val eventoList: ArrayList<Eventos> = ArrayList()
    private lateinit var adapter: EventoAdapter


    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        store.firestoreSettings = settings

        val vista =inflater.inflate(R.layout.fragment_editar_eventos ,null)
        val rvElEventos =vista.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvEventos)

        noteDBRef = store.collection("Eventos")
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        adapter = EventoAdapter(eventoList, object : RecyclerEventoListener {
            override fun onClick(evento: Eventos, position: Int) =
                makeText(context, evento.nombreEvento, LENGTH_SHORT).show()

            override fun onLongClick(evento: Eventos, position: Int) {
                makeText(context, evento.registradoPor, LENGTH_SHORT).show()
            }

        })
        cargarDatos()
        rvElEventos.setHasFixedSize(true)
        rvElEventos.layoutManager = layoutManager
        rvElEventos.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        rvElEventos.adapter = adapter

       return vista
    }

    private fun cargarDatos() {


        noteDBRef.addSnapshotListener(object:EventListener,com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                p1?.let {
                    makeText(context, "Error al momento de cargar la información", LENGTH_SHORT).show()
                    return
                }
                p0?.let {
                    val elEvento=it.toObjects(Eventos::class.java)
                    eventoList.addAll(elEvento)
                    Log.d("resultado","evento:$elEvento.................")
                    adapter.notifyDataSetChanged()

                }
            }
        })


    }

}
