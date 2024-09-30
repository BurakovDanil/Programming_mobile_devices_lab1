package com.example.myapplication

import android.content.Context
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val square = TexturedSquare(context)
    private val cube = Cube()
    private var rotationAngle = 0f

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // Настройка среды
        gl.glClearColor(0f, 0f, 0f, 1f)

        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glEnable(GL10.GL_DEPTH_TEST)

        // Загрузка текстуры для квадрата
        square.loadTexture(gl)
    }

    override fun onDrawFrame(gl: GL10) {
        // Очистка экрана и буфера глубины
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

        // (текстурированный квадрат)
        gl.glLoadIdentity()
        gl.glTranslatef(0f, 0f, -5f)  // Отодвигаем квадрат на задний план
        gl.glScalef(5f, 5f, 2f)       // Масштабируем, чтобы покрывал весь экран
        square.draw(gl)

        gl.glLoadIdentity()
        gl.glTranslatef(0f, 0f, -4f)
        //gl.glRotatef(30f, 1f, 1f, 1f)
        rotationAngle += 1f
        if (rotationAngle >= 360) {
            rotationAngle = 0f
        }
        gl.glRotatef(rotationAngle, 1f, 1f, 1f)

        // Отключаем текстуру перед отрисовкой куба
        gl.glDisable(GL10.GL_TEXTURE_2D)

        // Отрисовка куба
        cube.draw(gl)

        // Включаем текстуру снова, если это нужно для следующей отрисовки
        gl.glEnable(GL10.GL_TEXTURE_2D)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        // Установка области видимости и проекции
        gl.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()
        gl.glFrustumf(-ratio, ratio, -1f, 1f, 1f, 10f)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
    }
}




