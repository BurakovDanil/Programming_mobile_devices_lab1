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
            // Устанавливаем цвет фона
            gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            // Включаем тест глубины для правильного отображения куба
            gl.glEnable(GL10.GL_DEPTH_TEST)

            // Создаем объект куба
            cube = Cube()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        try {
            // Устанавливаем вьюпорт
            gl.glViewport(0, 0, width, height)

            // Устанавливаем матрицу проекции
            val ratio: Float = width.toFloat() / height
            gl.glMatrixMode(GL10.GL_PROJECTION)
            gl.glLoadIdentity()
            gl.glFrustumf(-ratio, ratio, -1f, 1f, 3f, 7f)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDrawFrame(gl: GL10) {
        try {
            // Очищаем экран и буфер глубины
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

            // Настраиваем видовую матрицу
            gl.glMatrixMode(GL10.GL_MODELVIEW)
            gl.glLoadIdentity()
            gl.glTranslatef(0.0f, 0.0f, -5.0f)

            // Вращаем куб
            gl.glRotatef(30f, 1f, 1f, 1f)

            // Рисуем куб
            cube.draw(gl)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


