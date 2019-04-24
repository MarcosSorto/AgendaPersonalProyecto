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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.agendapersonal.dataBase.Eventos
import com.example.agendapersonal.dateTimePicker.DatePickerFragment
import com.example.agendapersonal.dateTimePicker.TimePickerFragment
import com.example.matematicasaplicacion.AppConstants
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_editar_e.*
import kotlinx.android.synthetic.main.fragment_editar_e.view.*
import java.io.ByteArrayOutputStream


@SuppressLint("ValidFragment")
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

class EditarE(note:Eventos): Fragment() {
    private  var fileUri: Uri? = null
   private val event = note
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var noteDBRef: CollectionReference
    private var storage  = FirebaseStorage.getInstance()
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val vista =inflater.inflate(R.layout.fragment_editar_e, container, false)

        Picasso.with(vista.context).load(event.UrlEvento).into(vista.ivFotoEventoE)
        vista.txtNombreEventoE.setText(event.nombreEvento)
        vista.txtDescripcionE.setText(event.descripcionEvento)
        vista.txtFechaE.setText("${event.anioEvento} /${event.mesEvento} /${event.diaEvento} ")
        vista.txtHoraE.setText("${event.horaEvento}:${event.minutoEvento}")

        //actualizamos el selector en caso que no se modifique la fecha.
        Selector.anio = event.anioEvento
        Selector.mes = event.mesEvento
        Selector.dia = event.diaEvento
        Selector.hora = event.horaEvento
        Selector.minuto = event.minutoEvento


        return vista
    }

    //hora veificamos si se pulsa sobre el boton guardar

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnFechaE.setOnClickListener{
            val newFragment = DatePickerFragment()
            newFragment.show(this.fragmentManager, "datePicker")
            txtFechaE.setText("${Selector.anio}/${Selector.mes}/${Selector.dia}")
        }

        val laFecha=view.findViewById<EditText>(R.id.txtFechaE)
        laFecha.setOnClickListener{
            txtFechaE.setText("${Selector.anio}/${Selector.mes}/${Selector.dia}")
        }

        /*creamos la variable de tipo edit text para acceder a los eventos y metodos*/
        btnHoraE.setOnClickListener{
            val newFargemt2 = TimePickerFragment()
            newFargemt2.show(this.fragmentManager,"timePicker")
            txtHoraE.setText("${Selector.hora}:${Selector.minuto}")
        }
        val laHora=view.findViewById<EditText>(R.id.txtHoraE)
        laHora.setOnClickListener{
            txtHoraE.setText("${Selector.hora}:${Selector.minuto}")
        }

        btnGuardarEventoE.setOnClickListener{

            //verificamos si la actividad está vacia
            if(view.txtNombreEventoE.text.isNullOrEmpty() || view.txtDescripcionE.text.isNullOrEmpty()){
                Toast.makeText(context,"Debes ingresar e nombre ,la descripción y una fotografia para guardar",Toast.LENGTH_SHORT).show()
            }else{
                // Establecer la colección a utilizar
                noteDBRef = store.collection("Eventos")

                val nombre = view.findViewById<EditText>(R.id.txtNombreEventoE)
                val descripcion =view.findViewById<EditText>(R.id.txtDescripcionE)
                val anio= Selector.anio
                val mes = Selector.mes
                val dia = Selector.dia
                val hora= Selector.hora
                val minuto=Selector.minuto

                // Almacenar la información en Firebase

                val evento = Eventos(nombre.text.toString(), descripcion.text.toString(),anio,mes,dia,hora,minuto)
                saveFoto(event.documeto,evento)
            }


        }
        //definimos si se ha hecho click en la imagen de la fotografía
        val laFoto :ImageView =  view.findViewById(R.id.ivFotoEventoE)

        //verificamos si se hace click
        laFoto.setOnClickListener{
            askCameraPermission()
        }
    }

    private fun saveFoto(nombre: String, elEvento:Eventos) {
        //creando la Referencia
        val storageRef = storage.reference
        val referenciaImagenes =storageRef.child(nombre)
        val laImagen: ImageView? = view!!.findViewById(R.id.ivFotoEventoE)
        @Suppress("DEPRECATED_IDENTITY_EQUALS", "NAME_SHADOWING", "PLUGIN_WARNING", "DEPRECATION")
        laImagen?.isDrawingCacheEnabled = true
        @Suppress("DEPRECATED_IDENTITY_EQUALS", "NAME_SHADOWING", "PLUGIN_WARNING", "DEPRECATION")
        laImagen?.buildDrawingCache()

        val bitmap = (laImagen?.drawable as BitmapDrawable).bitmap
        if(bitmap ==null){

        }
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = referenciaImagenes.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this.context, "ups", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            referenciaImagenes.downloadUrl.addOnSuccessListener {
                saveNote(elEvento,it.toString())

            }

            limpiar()
        }


    }
    private fun saveNote(note: Eventos, url:String) {
        val newNote = HashMap<String, Any>()

        newNote["nombreEvento"] = note.nombreEvento
        newNote["descripcionEvento"] = note.descripcionEvento
        newNote["anioEvento"] = note.anioEvento
        newNote["mesEvento"] = note.mesEvento
        newNote["diaEvento"] = note.diaEvento
        newNote["horaEvento"] = note.horaEvento
        newNote["minutoEvento"] = note.minutoEvento
        newNote["registradoPor"] = note.registradoPor
        newNote["UrlEvento"]=url
        val documet =store.document("Eventos/${event.documeto}")
        documet.update(newNote)
            .addOnCompleteListener {
                Toast.makeText(this.context, "Evento Actualizado correctamente", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                Toast.makeText(this.context, "Ocurrió un error!!", Toast.LENGTH_SHORT).show()
            }
    }

    //creamos una función para limpiar todos los datos una vez guardados.
    private fun limpiar(){
        txtNombreEventoE.setText("")
       txtDescripcionE.setText(" ")
        txtFechaE.setText(" ")
        txtHoraE.setText(" ")
        ivFotoEventoE.setImageResource(R.drawable.ic_menu_camera)
        Selector.anio=0
        Selector.dia=0
        Selector.mes=0
        Selector.hora=0
        Selector.minuto=0
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
    private fun askCameraPermission() = Dexter.withActivity(this@EditarE.activity)
        .withPermissions(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {/* ... */
                if(report.areAllPermissionsGranted()){
                    //once permissions are granted, launch the camera
                    launchCamera()
                }else{
                    Toast.makeText(this@EditarE.context, "Requiere de todos los permisos para utilizar la camara.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {/* ... */
                //show alert dialog with permission options
                AlertDialog.Builder(this@EditarE.context)
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
            ivFotoEventoE.setImageURI(fileUri)
        }else if(resultCode == Activity.RESULT_OK
            && requestCode == AppConstants.PICK_PHOTO_REQUEST){
            //photo from gallery
            fileUri = data?.data
            ivFotoEventoE.setImageURI(fileUri)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }
}
