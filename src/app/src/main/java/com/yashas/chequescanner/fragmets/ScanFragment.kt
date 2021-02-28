package com.yashas.chequescanner.fragmets

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.yashas.chequescanner.R
import java.io.File
import java.util.*


class ScanFragment : Fragment() {

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private lateinit var torch: AppCompatImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewFinder: PreviewView
    private lateinit var camera: Camera
    private lateinit var imageCapture: ImageCapture
    private lateinit var captureBtn: AppCompatButton
    private lateinit var gallery: AppCompatImageView
    private lateinit var provider: ProcessCameraProvider
    private var rotationDegrees: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_scan, container, false)
        setup(view)
        return view
    }

    private fun setup(view: View){
        initUI(view)
        listener()
        startCamera()
    }

    private fun initUI(view: View){
        viewFinder = view.findViewById(R.id.previewView)
        cameraManager = context!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]
        torch = view.findViewById(R.id.torch)
        captureBtn = view.findViewById(R.id.capture)
        gallery = view.findViewById(R.id.gallery)
        sharedPreferences = context!!.getSharedPreferences("flash", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("on", false).apply()
    }

    private fun checkTorch(){
        if(sharedPreferences.getBoolean("on", false)){
            Glide.with(context!!)
                .load(ResourcesCompat.getDrawable(context!!.resources, R.drawable.ic_on, null))
                .into(torch)
        }else{
            Glide.with(context!!)
                .load(ResourcesCompat.getDrawable(context!!.resources, R.drawable.ic_off, null))
                .into(torch)
        }
    }

    private fun listener(){
        torch.setOnClickListener {
            turnOnFlash(sharedPreferences.getBoolean("on", false))
        }

        captureBtn.setOnClickListener {
            takePhoto()
        }

        gallery.setOnClickListener {
            ImagePicker.with(this)
                .galleryOnly()
                .start()
        }
    }

    private fun turnOnFlash(on: Boolean){
        try {
            if(camera.cameraInfo.hasFlashUnit()){
                camera.cameraControl.enableTorch(!on)
                sharedPreferences.edit().putBoolean("on", !on).apply()
                checkTorch()
            }
        }catch (e: Exception){
            Toast.makeText(context, "Some error occurred", Toast.LENGTH_LONG).show()
        }
    }



    private fun takePhoto() {
        val imageCapture = imageCapture?: return
        val photoFile = File.createTempFile(Calendar.getInstance().timeInMillis.toString(), ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context!!),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {}

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val fragment = ScanDetailsFragment()
                    val bundle = Bundle()
                    bundle.putBoolean("gallery", false)
                    bundle.putString("imageUri", savedUri.toString())
                    fragment.arguments = bundle
                    (context as FragmentActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, fragment)
                        .commit()
                }
            })

    }

    private fun startCamera() {
        val cameraProvider = ProcessCameraProvider.getInstance(context!!)
        cameraProvider.addListener({
            provider = cameraProvider.get()
            val preview = Preview.Builder().build()

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()


            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setTargetRotation(Surface.ROTATION_90)
                .build()

            preview.setSurfaceProvider(viewFinder.surfaceProvider)
            provider.unbindAll()
            camera = provider.bindToLifecycle(
                context as LifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        }, ContextCompat.getMainExecutor(context!!))

    }

    override fun onPause() {
        super.onPause()
        provider.unbindAll()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK){
            if(data!=null){
                val savedUri = data.data
                val fragment = ScanDetailsFragment()
                val bundle = Bundle()
                bundle.putBoolean("gallery", true)
                bundle.putString("imageUri", savedUri.toString())
                fragment.arguments = bundle
                (context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.frame, fragment)
                    .commit()
            }
        }
    }
}