package com.example.agendapersonal

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.matematicasaplicacion.AppConstants
import kotlinx.android.synthetic.main.fragment_crear_perfil.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener



class CrearPerfil : Fragment() {
    private  var fileUri: Uri? = null

       @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_crear_perfil ,null)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnFoto.setOnClickListener{
            askCameraPermission()
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

}
