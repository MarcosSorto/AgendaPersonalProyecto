package com.example.agendapersonal

import android.annotation.SuppressLint


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.agendapersonal.adapter.EventoAdapter
import com.example.agendapersonal.dataBase.Eventos
import com.example.agendapersonal.interfaces.RecyclerEventoListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.util.*


class EditarEventos : Fragment() {
    //variables necesarias para conexion
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var noteDBRef: CollectionReference
    private val eventoList: ArrayList<Eventos> = ArrayList()
    private lateinit var adapter: EventoAdapter
    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val vista =inflater.inflate(R.layout.fragment_editar_eventos ,null)
        val rvElEventos =vista.findViewById<RecyclerView>(R.id.rvEventos)
        noteDBRef = store.collection("Eventos")

        val layoutManager = LinearLayoutManager(context)
        getNotesFromDB()


        rvElEventos.setHasFixedSize(true)
        rvElEventos.layoutManager = layoutManager
        rvElEventos.itemAnimator = DefaultItemAnimator()

        adapter = EventoAdapter(eventoList, object : RecyclerEventoListener {
            override fun onClick(evento: Eventos, position: Int) {
                Toast.makeText(context, evento.nombreEvento, Toast.LENGTH_SHORT).show()
            }

            /*override fun onLongClick(evento: Eventos, position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }*/

        })

        rvElEventos.adapter = adapter

       return vista
    }

    private fun getNotesFromDB() {
        noteDBRef.addSnapshotListener(object: EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot>{
            override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                exception?.let {
                    Toast.makeText(context, "Error al momento de cargar la información", Toast.LENGTH_SHORT).show()
                    return
                }

                snapshot?.let {
                    val evento = it.toObjects(Eventos::class.java)
                    eventoList.addAll(evento)
                    adapter.notifyDataSetChanged()

                }
            }

        })
    }
}
