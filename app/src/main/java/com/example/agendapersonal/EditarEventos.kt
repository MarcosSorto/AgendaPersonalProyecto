package com.example.agendapersonal

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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



        val vista =inflater.inflate(R.layout.fragment_editar_eventos ,null)
        val rvElEventos =vista.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvEventos)

        noteDBRef = store.collection("Eventos")
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        adapter = EventoAdapter(eventoList, object : RecyclerEventoListener {
            override fun onLongClick(evento: Eventos, position: Int) =
                makeText(context, evento.nombreEvento, LENGTH_SHORT).show()

            override fun onClick(evento: Eventos, position: Int) {

                val supportFragmentManager = fragmentManager
                //creamos un alert dialog para presentar las opciones de eliminar y editar el evento.
                val builder = AlertDialog.Builder(context)

                // el mensaje del alert
                builder.setMessage(getString(R.string.tituloAlerta))

                //realizamos la funcionalidad de la opcion editar.
                builder.setPositiveButton(getString(R.string.AlertaEditar)) { dialog, which ->
                    // Realizar el llamado a la activity NoteAddActivity
                    val nuevoFragment = EditarE(eventoList[position])
                    supportFragmentManager?.beginTransaction()!!.replace(R.id.cmPrincipal, nuevoFragment).commit()

                }
                builder.setNegativeButton(getString(R.string.AlertaEliminar)) { dialogInterface: DialogInterface, i: Int ->

                    val documet =store.document("Eventos/${eventoList[position].documeto}")
                    documet.delete()
                        .addOnCompleteListener {
                            Toast.makeText(context, "Evento Eliminado correctamente.", Toast.LENGTH_SHORT).show()
                            supportFragmentManager?.beginTransaction()!!.replace(R.id.cmPrincipal, EditarEventos()).commit()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "ocurrió un error", Toast.LENGTH_SHORT).show()
                        }

                }
                builder.setNeutralButton(getString(R.string.AlertaCancelar)) { dialog, which ->
                    Toast.makeText(context, getString(R.string.AlertaCancelar), Toast.LENGTH_SHORT).show()
                }


                // creamos el dilog
                val dialog: AlertDialog = builder.create()

                // Mostrar el AlertDialog
                dialog.show()

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

                    Log.d("resultado","evento:$elEvento.................")
                    Log.d("llave","${it.documents[0]}")
                    eventoList.addAll(elEvento)
                    adapter.notifyDataSetChanged()

                }
            }
        })


    }

}
