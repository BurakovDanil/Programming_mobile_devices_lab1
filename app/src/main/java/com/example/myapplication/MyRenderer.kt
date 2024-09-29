package com.example.myapplication


import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer : GLSurfaceView.Renderer {
    private lateinit var cube: Cube

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
        try {
            gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            gl.glEnable(GL10.GL_DEPTH_TEST)
            cube = Cube()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        try {
            gl.glViewport(0, 0, width, height)

            val ratio: Float = width.toFloat()/height
            gl.glMatrixMode(GL10.GL_PROJECTION)
            gl.glLoadIdentity()
            gl.glFrustumf(-ratio, ratio, -1f, 1f, 3f, 7f)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDrawFrame(gl: GL10) {
        try {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

            gl.glMatrixMode(GL10.GL_MODELVIEW)
            gl.glLoadIdentity()
            gl.glTranslatef(0.0f, 0.0f, -5.0f)
            gl.glRotatef(30f, 1f, 1f, 1f)

            cube.draw(gl)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


