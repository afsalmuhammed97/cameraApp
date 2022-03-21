package com.practies.camera

import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.practies.camera.databinding.ActivityMainBinding
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityMainBinding
    private var imageCapture :ImageCapture? =null
    private lateinit var outPutDirectory:File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        outPutDirectory = getOutPutDirectory()


        if (permissionGranted()){
            startCamera()
//            Toast.makeText(this,"we have permission",Toast.LENGTH_SHORT).show()
        }else{

            ActivityCompat.requestPermissions(
                this,Constants.REQUIRED_PERMISSIONS,
                Constants.REQUEST_CODE_PERMISSIONS
            )
        }
        binding.btnTakePhoto.setOnClickListener{
            takePhoto()
        }


    }
    private fun getOutPutDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let { mFile ->
            File(mFile,resources.getString(R.string.app_name)).apply {
                mkdirs()
            }
        }
        return if (mediaDir != null && mediaDir .exists())
               mediaDir    else  filesDir

    }



           private fun   takePhoto(){
               val imageCapture = imageCapture ?:return
              val photofFile= File(
                  outPutDirectory,SimpleDateFormat(Constants.FILE_NAME_FORMAT,
                      Locale.getDefault()).format(System.currentTimeMillis())+".jpg")
               val outPutOption =ImageCapture
                   .OutputFileOptions
                   .Builder(photofFile)
                   .build()
  

               imageCapture.takePicture(
                   outPutOption,ContextCompat.getMainExecutor(this),
                   object :ImageCapture.OnImageSavedCallback{
                       override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                           val savedUri  = Uri.fromFile(photofFile)
                           val msg ="Photo saved"
                           Toast.makeText(this@MainActivity,"$msg  $savedUri",Toast.LENGTH_LONG).show()
                       }

                       override fun onError(exception: ImageCaptureException) {
                         Log.e(Constants.TAG,"error : ${exception.message}",exception)
                       }

                   }
               )



           }
    private  fun  startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)




        cameraProviderFuture.addListener({

            val cameraProvider : ProcessCameraProvider=cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {  mPreview ->
                    mPreview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
                      imageCapture= ImageCapture.Builder()
                          .build()
            val cameraSelector= CameraSelector.DEFAULT_BACK_CAMERA



            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture)
            }catch (e:Exception){
                Log.d(Constants.TAG,"Start camera fail :",e )

            }


        },ContextCompat.getMainExecutor(this))

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array< String>,
        grantResults: IntArray
    )  {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode== Constants.REQUEST_CODE_PERMISSIONS){
            if (permissionGranted()){

                startCamera()
            }else{
                Toast.makeText(this,"permission  not granted by the user",Toast.LENGTH_SHORT).show()
                finish()
            }

        }
    }

    private fun permissionGranted()=
        Constants.REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                    baseContext,it
            )==PackageManager.PERMISSION_GRANTED
        }

}


//
//            val cameraProvider : ProcessCameraProvider=cameraProviderFuture.get()
//            val preview = Preview.Builder()
//                .build()
//                .also { mPreview ->
//                    mPreview.setSurfaceProvider(   binding.viewFinder.surfaceProvider)
//                }
//                   imageCapture= ImageCapture.Builder()
//                       .build()
//            val cameraSelector= CameraSelector.DEFAULT_BACK_CAMERA
//
//            try {
//                cameraProvider.unbindAll()
//                cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture)
//
//            }catch (e:Exception){
//                Log.d(Constants.TAG,"Start camera fail :",e )
//
//            }
//
//
//        })