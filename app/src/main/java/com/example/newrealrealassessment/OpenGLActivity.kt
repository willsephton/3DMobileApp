package com.example.newrealrealassessment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class OpenGLActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl)

        val glview = findViewById<OpenGLView>(R.id.glview)

        findViewById<Button>(R.id.plusZ).setOnClickListener{
            glview.camera.translate(0f, 0f, 1f)
        }
        findViewById<Button>(R.id.minusZ).setOnClickListener{
            glview.camera.translate(0f, 0f, -1f)
        }
        findViewById<Button>(R.id.plusX).setOnClickListener{
            glview.camera.translate(1f, 0f, 0f)
        }
        findViewById<Button>(R.id.minusX).setOnClickListener{
            glview.camera.translate(-1f, 0f, 0f)
        }
        findViewById<Button>(R.id.plusY).setOnClickListener{
            glview.camera.translate(0f, 1f, 0f)
        }
        findViewById<Button>(R.id.minusY).setOnClickListener{
            glview.camera.translate(0f, -1f, 0f)
        }
    }
}