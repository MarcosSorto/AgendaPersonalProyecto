package com.example.agendapersonal

import android.annotation.SuppressLint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.agendapersonal.dataBase.Perfiles
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_default.*


class DefaultFragment : androidx.fragment.app.Fragment() {
    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_default ,null)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAcceder.setOnClickListener{
            if(txtIdentidadIngreso.text.isNullOrEmpty()|| txtNombreIngreso.text.isNullOrEmpty()){
                Toast.makeText(context,"Debes ingresar las credenciales de acceso",Toast.LENGTH_SHORT).show()
            }else{
                Perfiles.ident = txtIdentidadIngreso.text.toString()
                Perfiles.name = txtNombreIngreso.text.toString()

                //verificamos si se encuentra registrado en firebase.
                 val store: FirebaseFirestore = FirebaseFirestore.getInstance()
                val documet =store.document("Perfiles/${txtIdentidadIngreso.text}")
                val newNote = HashMap<String, Any>()

                newNote["nombre"] = txtNombreIngreso.text.toString()

                //verificamos si se encuetra el usuario con una actualización
                documet.update(newNote)
                    .addOnCompleteListener {
                        Toast.makeText(this.context, "Bienvenido, a tu agenda", Toast.LENGTH_SHORT).show()

                    }
                    .addOnFailureListener {
                        Toast.makeText(this.context, "Ups, tines un perfil?", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }

}
