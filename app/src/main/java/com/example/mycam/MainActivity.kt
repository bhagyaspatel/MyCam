package com.example.mycam

import android.Manifest
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    var camera : Camera ?= null
    var preview : Preview ?= null
    var imageCapture : ImageCapture ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PERMISSION_GRANTED) {
            startCamera()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        }

        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PERMISSION_GRANTED) {
                startCamera()
            }else{
                Toast.makeText(this, "Please accept the permission", Toast.LENGTH_SHORT).show()
            }
        }

        captureBtn.setOnClickListener {
            takePhoto()
        }
    }

    private fun takePhoto() {
        //code to save photo

        //photofile is the path jaha par humari image save hogi
        //externalMediaDirs gives us that path acc to ur android phones settings
        val photofile = File (externalMediaDirs.firstOrNull(), "MyCam - ${System.currentTimeMillis()}.jpg")

//        "MyCam - ${System.currentTimeMillis()}.jpg" is the file name jisse humari file save hogi
//        and agar hum sirf 1 hi nam rakhege to woh baar baar replace hoti jayegi so we want unique
//        name every time so we chose "System.currentTimeMillis()" i.e. current system ka time

        val output = ImageCapture.OutputFileOptions.Builder(photofile).build()

//        ContextCompat.getMainExecutor(this) ..main thread pe kaam karne ke lie
        imageCapture?.takePicture(output,ContextCompat.getMainExecutor(this), object:ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Toast.makeText(applicationContext, "Image Saved", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: ImageCaptureException) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun startCamera() {
        //we have to bind our camera with our application lifecycle
        //camera is a hardware and our lifecycle is a software
        //and we do that with the use of camera provider

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder().build()

            //setting our preview with our camera's view (cameraView is the id of our PreviewView in xml)
            preview?.setSurfaceProvider(cameraView.createSurfaceProvider(camera?.cameraInfo))

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build() /////*****
            //camera selector selects the camera (Selfie camera or rear camera)

            cameraProvider.unbind()
            //important to unbind the cameraProvider before binding it to our lifecycle

            camera = cameraProvider.bindToLifecycle(this,cameraSelector, preview, imageCapture)

        }, ContextCompat.getMainExecutor(this))
    }
}