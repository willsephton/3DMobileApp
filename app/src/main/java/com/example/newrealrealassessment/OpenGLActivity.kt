package com.example.newrealrealassessment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutInfoCompat
import freemap.openglwrapper.GLMatrix


class OpenGLActivity: AppCompatActivity(), SensorEventListener {

    private var permissions = arrayOf(Manifest.permission.CAMERA)
    private var surfaceTexture: SurfaceTexture? = null

    var accel: Sensor? = null
    var magField: Sensor? = null

    var accelValues = FloatArray(3)
    var magFieldValues = FloatArray(3)

    val orientationMatrix = FloatArray(16)

    val orientations = FloatArray(3)

    lateinit var glView: OpenGLView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val openglview = OpenGLView(this) {
            Log.d("CAMERAXGL", "Starting camera")
            surfaceTexture = it
            if (!startCamera()) {
                ActivityCompat.requestPermissions(this, permissions, 0)
            }
        }
        setContentView(openglview)


        val sMgr = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accel = sMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magField = sMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sMgr.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI)
        sMgr.registerListener(this, magField, SensorManager.SENSOR_DELAY_UI)


       /* findViewById<Button>(R.id.plusZ).setOnClickListener{
            glView.camera.translate(0f, 0f, 1f)
        }
        findViewById<Button>(R.id.minusZ).setOnClickListener{
            glView.camera.translate(0f, 0f, -1f)
        }
        findViewById<Button>(R.id.plusX).setOnClickListener{
            glView.camera.translate(1f, 0f, 0f)
        }
        findViewById<Button>(R.id.minusX).setOnClickListener{
            glView.camera.translate(-1f, 0f, 0f)
        }
        findViewById<Button>(R.id.plusY).setOnClickListener{
            glView.camera.translate(0f, 1f, 0f)
        }
        findViewById<Button>(R.id.minusY).setOnClickListener{
            glView.camera.translate(0f, -1f, 0f)
        } */
    }

    override fun onSensorChanged(ev: SensorEvent) {
        if(ev.sensor == accel){
            accelValues = ev.values.copyOf()
        } else {
            magFieldValues = ev.values.copyOf()
        }


        /*
        SensorManager.getRotationMatrix(orientationMatrix, null, accelValues, magFieldValues)
        val remapped = FloatArray(16)
        SensorManager.remapCoordinateSystem(orientationMatrix, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, remapped)
        glView.orientationMatrix = GLMatrix(remapped)

        SensorManager.getOrientation(orientationMatrix, orientations)
        val deg = orientations.map {it * 180.0/Math.PI}
        Log.d("gltestlog", "Orientations ${deg[0]} ${deg[1]} ${deg[2]}")*/
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
    private fun checkPermissions(): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startCamera()
        } else {
            AlertDialog.Builder(this).setPositiveButton("OK", null)
                .setMessage("Will not work as camera permission not granted").show()
        }
    }


    private fun startCamera(): Boolean {
        Log.d("CAMERAXGL", "startCamera()")
        if (checkPermissions()) {
            Log.d("CAMERAXGL", "startCamera() ready to go")
            val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    val provider: (SurfaceRequest) -> Unit = { request ->
                        val resolution = request.resolution
                        surfaceTexture?.apply {

                            setDefaultBufferSize(resolution.width, resolution.height)
                            val surface = Surface(this)
                            request.provideSurface(
                                surface,
                                ContextCompat.getMainExecutor(this@OpenGLActivity.baseContext))
                            { }

                        }
                    }
                    it.setSurfaceProvider(provider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview)

                } catch (e: Exception) {
                    Log.e("CAMERAXGL", e.stackTraceToString())
                }
            }, ContextCompat.getMainExecutor(this))
            return true
        } else {
            return false
        }
    }


}