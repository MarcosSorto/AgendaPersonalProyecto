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
import com.example.agendapersonal.dataBase.Perfiles
import com.example.matematicasaplicacion.AppConstants
import kotlinx.android.synthetic.main.fragment_crear_perfil.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import java.io.ByteArrayOutputStream

@Suppress("DEPRECATION")
class CrearPerfil : androidx.fragment.app.Fragment() {
    private  var fileUri: Uri? = null
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var noteDBRef: CollectionReference
    private var storage  = FirebaseStorage.getInstance()

       @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_crear_perfil ,null)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //verificamos si se hace click sobre tomar fotografía
        val tomarFoto = view.findViewById<ImageView>(R.id.ivImagen)
        tomarFoto.setOnClickListener{
            askCameraPermission()
        }
        //verificamos si se hace click en el boton crear.
        btnAceptar.setOnClickListener{
            if(txtIdentidad.text.isNullOrEmpty()||txtNombres.text.isNullOrEmpty()){
                Toast.makeText(this.context,"¡Debes ingresar la identidad el nombre  y una foto del usuario!",Toast.LENGTH_SHORT).show()
            }else{
                //preparamos lo necesario para guardar los datos ingresados

                // Establecer la colección a utilizar
                noteDBRef = store.collection("Perfiles")
                val identidad = view.findViewById<EditText>(R.id.txtIdentidad)
                val nombre =view.findViewById<EditText>(R.id.txtNombres)
                val apellido =view.findViewById<EditText>(R.id.txtApellidos)
                val correo =view.findViewById<EditText>(R.id.txtCorreo)

                // Almacenar la información en Firebase
                val perfil = Perfiles(identidad.text.toString(),nombre.text.toString(),apellido.text.toString(),correo.text.toString())
                saveFoto(nombre.text.toString())
                savePerfil(perfil)
            }
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
    private fun askCameraPermission() = Dexter.withActivity(this@CrearPerfil.activity)
        .withPermissions(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {/* ... */
                if(report.areAllPermissionsGranted()){
                    //once permissions are granted, launch the camera
                    launchCamera()
                }else{
                    Toast.makeText(this@CrearPerfil.context, "All permissions need to be granted to take photo", Toast.LENGTH_LONG).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {/* ... */
                //show alert dialog with permission options
                AlertDialog.Builder(this@CrearPerfil.context)
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
            ivImagen.setImageURI(fileUri)
        }else if(resultCode == Activity.RESULT_OK
            && requestCode == AppConstants.PICK_PHOTO_REQUEST){
            //photo from gallery
            fileUri = data?.data
            ivImagen.setImageURI(fileUri)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun saveFoto(nombre: String) {
        //creando la Referencia
        val storageRef = storage.reference
        val referenciaImagenes =storageRef.child(nombre)
        val laImagen:ImageView? = view!!.findViewById(R.id.ivImagen)

        laImagen?.isDrawingCacheEnabled = true
        laImagen?.buildDrawingCache()

        val bitmap = (laImagen?.drawable as BitmapDrawable).bitmap

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

    private fun savePerfil(note: Perfiles) {
        val newNote = HashMap<String, Any>()
        newNote["Identidad"] = note.identidad
        newNote["Nombres"] = note.nombre
        newNote["Apellidos"] = note.apellido
        newNote["Correo"] = note.correo

        noteDBRef.add(newNote)
            .addOnCompleteListener {
                Toast.makeText(this.context, "Evento registrado correctamente", Toast.LENGTH_SHORT).show()
                limpiar()
            }
            .addOnFailureListener {
                Toast.makeText(this.context, "Ocurrió un error!!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun limpiar(){
        txtIdentidad.setText("")
        txtNombres.setText("")
        txtApellidos.setText("")
        txtCorreo.setText("")
        ivImagen.setImageResource(R.drawable.ic_menu_camera)


    }

}
