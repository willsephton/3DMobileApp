package com.example.newrealrealassessment

import android.opengl.GLSurfaceView
import android.content.Context
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.AttributeSet
import android.util.Log
import freemap.openglwrapper.Camera
import freemap.openglwrapper.GLMatrix
import freemap.openglwrapper.GPUInterface
import freemap.openglwrapper.OpenGLUtils
import java.io.IOException
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10


class OpenGLView(ctx: Context, aSet: AttributeSet): GLSurfaceView(ctx, aSet), GLSurfaceView.Renderer {
    init {
        setEGLContextClientVersion(2)
        setRenderer(this)
    }

    val gpu = GPUInterface("default shader")

    var fbuf: FloatBuffer? = null

    val red = floatArrayOf(1f, 0f, 0f, 1f)
    val yellow = floatArrayOf(1f, 1f, 0f, 1f)
    val blue = floatArrayOf(0f, 0f, 1f, 1f)

    val camera = Camera(0f, 0f, 0f)

    val viewMatrix = GLMatrix()

    val projectionMatrix = GLMatrix()

    override fun onSurfaceCreated(p0: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        GLES20.glClearDepthf(1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        try {
            val success = gpu.loadShaders(context.assets, "vertex.glsl", "fragment.glsl")
            if (!success) {
                Log.e("OpenGLBasic", gpu.lastShaderError)
            }
            fbuf = OpenGLUtils.makeFloatBuffer(
                floatArrayOf(
                    0f, 0f, -3f,
                    1f, 0f, -3f,
                    0.5f, 1f, -3f,
                    -0.5f, 0f, -6f,
                    0.5f, 0f, -6f,
                    0f, 1f, -6f
                )
            )
            gpu.select()
        } catch (e: IOException) {
            Log.e("OpenGLBasic", e.stackTraceToString())
        }
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val hfov = 60.0f
        val aspect: Float = width.toFloat() / height
        projectionMatrix.setProjectionMatrix(hfov, aspect, 0.001f, 100f)

    }

    override fun onDrawFrame(p0: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        val ref_aVertex = gpu.getAttribLocation("aVertex")
        val ref_uColour = gpu.getUniformLocation("uColour")

        val ref_uView = gpu.getUniformLocation("uView")
        val ref_uProj = gpu.getUniformLocation("uProj")

        viewMatrix.setAsIdentityMatrix()
        viewMatrix.translate(-camera.position.x, -camera.position.y, -camera.position.z)


        gpu.sendMatrix(ref_uView, viewMatrix)
        gpu.sendMatrix(ref_uProj, projectionMatrix)

        fbuf?.apply{

            gpu.setUniform4FloatArray(ref_uColour, blue)
            gpu.specifyBufferedDataFormat(ref_aVertex, this, 0)
            gpu.drawBufferedTriangles(0, 3)
            gpu.setUniform4FloatArray(ref_uColour, yellow)
            gpu.drawBufferedTriangles(3, 3)


        }
    }


}