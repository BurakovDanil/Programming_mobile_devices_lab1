package com.example.myapplication

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MoonRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val moon = Sphere(1.0f) // Модель Луны

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        gl.glClearColor(0f, 0f, 0f, 1f)
        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glEnable(GL10.GL_LIGHTING)
        gl.glEnable(GL10.GL_LIGHT0)
        gl.glEnable(GL10.GL_COLOR_MATERIAL)

        // Настройка параметров освещения
        val ambientLight = floatArrayOf(0.1f, 0.1f, 0.1f, 1.0f)
        val diffuseLight = floatArrayOf(0.8f, 0.8f, 0.8f, 1.0f)
        val specularLight = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
        val lightPosition = floatArrayOf(5f, 5f, 5f, 1.0f)

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientLight, 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseLight, 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, specularLight, 0)
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosition, 0)
    }

    override fun onDrawFrame(gl: GL10) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
        gl.glLoadIdentity()
        GLU.gluLookAt(gl, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f)

        // Настройка материалов для модели Луны
        val materialAmbient = floatArrayOf(0.2f, 0.2f, 0.2f, 1.0f)
        val materialDiffuse = floatArrayOf(0.8f, 0.8f, 0.8f, 1.0f)
        val materialSpecular = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
        val shininess = 64.0f

        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, materialAmbient, 0)
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, materialDiffuse, 0)
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, materialSpecular, 0)
        gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, shininess)

        // Отрисовка Луны
        moon.draw(gl)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        gl.glViewport(0, 0, width, height)
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()
        GLU.gluPerspective(gl, 45.0f, width.toFloat() / height.toFloat(), 1f, 100f)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
    }
}


