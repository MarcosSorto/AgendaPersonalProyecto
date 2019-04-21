package com.example.agendapersonal

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_crear_eventos.*
import android.widget.Toast
import com.example.agendapersonal.dataBase.Eventos
import com.example.agendapersonal.dateTimePicker.DatePickerFragment
import com.example.agendapersonal.dateTimePicker.TimePickerFragment
import com.example.matematicasaplicacion.AppConstants

import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import java.io.ByteArrayOutputStream
@Suppress("DEPRECATED_IDENTITY_EQUALS", "NAME_SHADOWING", "PLUGIN_WARNING", "DEPRECATION")

class CrearEventos : Fragment() {
    /*variables globales*/
    private  var fileUri: Uri? = null
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var noteDBRef: CollectionReference
    private var storage  = FirebaseStorage.getInstance()

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_crear_eventos ,null)
    }

    @SuppressLint("SetTextI18n")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*Definimos si se ha hecho click en eñ texto de la fecha para ingresarla*/
        btnFecha.setOnClickListener{
            val newFragment = DatePickerFragment()
            newFragment.show(this.fragmentManager, "datePicker")
            txtHora.setText("${Selector.hora}:${Selector.minuto}")
        }

        val laFecha=view.findViewById<EditText>(R.id.txtFecha)
        laFecha.setOnClickListener{
            txtFecha.setText("${Selector.anio}/${Selector.mes}/${Selector.dia}")
        }

        /*creamos la variable de tipo edit text para acceder a los eventos y metodos*/
        btnHora.setOnClickListener{
            val newFargemt2 = TimePickerFragment()
            newFargemt2.show(this.fragmentManager,"timePicker")
            txtHora.setText("${Selector.hora}:${Selector.minuto}")
        }
        val laHora=view.findViewById<EditText>(R.id.txtHora)
        laHora.setOnClickListener{
            txtHora.setText("${Selector.hora}:${Selector.minuto}")
        }

        btnGuardarEvento.setOnClickListener{
            if(txtNombreEvento.text.isNullOrEmpty() || txtDescripcion.text.isNullOrEmpty()){
                Toast.makeText(this.context, "¡Debe llenar el nombre y la descripción!", Toast.LENGTH_SHORT).show()
            }else{
                // Establecer la colección a utilizar
                noteDBRef = store.collection("Eventos")
                val nombre = view.findViewById<EditText>(R.id.txtNombreEvento)
                val descripcion =view.findViewById<EditText>(R.id.txtDescripcion)
                val anio= Selector.anio
                val mes = Selector.mes
                val dia = Selector.dia
                val hora= Selector.hora
                val minuto=Selector.minuto

                // Almacenar la información en Firebase
                val evento = Eventos(nombre.text.toString(), descripcion.text.toString(),anio,mes,dia,hora,minuto)
                saveNote(evento)
                saveFoto()
            }
        }

        //definimos si se ha hecho click en la imagen de la fotografía
        val laFoto :ImageView =  view.findViewById(R.id.ivFotoEvento)

        //verificamos si se hace click
        laFoto.setOnClickListener{
            askCameraPermission()
        }

    }

    private fun saveFoto() {
       //creando la Referencia
        val storageRef = storage.reference
        val referenciaImagenes =storageRef.child("Evento1")
        val laImagen = view!!.findViewById<ImageView>(R.id.ivFotoEvento)
        laImagen.isDrawingCacheEnabled = true
        laImagen.buildDrawingCache()
        val bitmap = (laImagen.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = referenciaImagenes.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this.context, "ups", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            Toast.makeText(this.context, "Se guardó la foto", Toast.LENGTH_SHORT).show()
        }



    }

    //llamamos a la aplicación camara mediante un Intent
    private fun launchCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        fileUri = this.activity!!.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity( this.activity!!.packageManager) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri)
            intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, AppConstants.TAKE_PHOTO_REQUEST)
        }
    }

    //ask for permission to take photo
    private fun askCameraPermission() = Dexter.withActivity(this@CrearEventos.activity)
        .withPermissions(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {/* ... */
                if(report.areAllPermissionsGranted()){
                    //once permissions are granted, launch the camera
                    launchCamera()
                }else{
                    Toast.makeText(this@CrearEventos.context, "Requiere de todos los permisos para utilizar la camara.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {/* ... */
                //show alert dialog with permission options
                AlertDialog.Builder(this@CrearEventos.context)
                    .setTitle(
                        "Permissions Error!")
                    .setMessage(
                        "Please allow permissions to take photo with camera")
                    .setNegativeButton(
                        android.R.string.cancel
                    ) { dialog, _ ->
                        dialog.dismiss()
                        token.cancelPermissionRequest()
                    }
                    .setPositiveButton(android.R.string.ok
                    ) { dialog, _ ->
                        dialog.dismiss()
                        token.continuePermissionRequest()
                    }
                    /*.setOnDismissListener({
                        token?.cancelPermissionRequest() })*/
                    .show()
            }

        }).check()

    //override function that is called once the photo has been taken
    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        if (resultCode == Activity.RESULT_OK
            && requestCode == AppConstants.TAKE_PHOTO_REQUEST) {
            //photo from camera
            //display the photo on the imageview
            ivFotoEvento.setImageURI(fileUri)
        }else if(resultCode == Activity.RESULT_OK
            && requestCode == AppConstants.PICK_PHOTO_REQUEST){
            //photo from gallery
            fileUri = data?.data
            ivFotoEvento.setImageURI(fileUri)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
    private fun saveNote(note: Eventos) {
        val newNote = HashMap<String, Any>()
        newNote["Nombre"] = note.nombreEvento
        newNote["Descripcion"] = note.descripcionEvento
        newNote["Anio"] = note.anioEvento
        newNote["Mes"] = note.mesEvento
        newNote["Dia"] = note.diaEvento
        newNote["Hora"] = note.horaEvento
        newNote["Minuto"] = note.minutoEvento
        newNote["RegistradoPor"] = note.registradoPor

        noteDBRef.add(newNote)
            .addOnCompleteListener {
                Toast.makeText(this.context, "Evento registrado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this.context, "Ocurrió un error!!", Toast.LENGTH_SHORT).show()
            }
    }
}