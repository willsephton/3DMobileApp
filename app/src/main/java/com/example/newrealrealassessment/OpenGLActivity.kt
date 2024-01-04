package com.example.newrealrealassessment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class OpenGLActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val glView = OpenGLView(this)
        setContentView(glView)
    }
}