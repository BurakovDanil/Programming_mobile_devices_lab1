package com.example.myapplication

import android.content.Context
import android.opengl.GLES10
import android.opengl.GLUtils
import android.graphics.BitmapFactory
import javax.microedition.khronos.opengles.GL10

class TexturedSquare(private val context: Context) {

    // Координаты вершин квадрата
    private val vertices = floatArrayOf(
        -1f, -1f, 0f,  // нижний левый
        1f, -1f, 0f,   // нижний правый
        -1f, 1f, 0f,   // верхний левый
        1f, 1f, 0f     // верхний правый
    )

    // Текстурные координаты для каждой вершины
    private val textureCoords = floatArrayOf(
        0f, 1f,  // нижний левый угол текстуры
        1f, 1f,  // нижний правый угол текстуры
        0f, 0f,  // верхний левый угол текстуры
        1f, 0f   // верхний правый угол текстуры
    )

    private val textureIds = IntArray(1)

    // Загружаем текстуру
    fun loadTexture(gl: GL10) {
        // Декодируем текстуру из ресурсов
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.space1)

        // Генерируем ID текстуры
        GLES10.glGenTextures(1, textureIds, 0)

        // Привязываем текстуру
        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, textureIds[0])

        // Загружаем текстуру в OpenGL
        GLUtils.texImage2D(GLES10.GL_TEXTURE_2D, 0, bitmap, 0)

        // Устанавливаем параметры текстуры (минимизация и фильтрация)
        GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_LINEAR.toFloat())
        GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_LINEAR.toFloat())

        // Освобождаем bitmap после загрузки в память OpenGL
        bitmap.recycle()
    }

    fun draw(gl: GL10) {
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY)
        GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY)

        // Привязываем текстуру
        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, textureIds[0])

        // Указываем массив вершин
        gl.glVertexPointer(3, GLES10.GL_FLOAT, 0, BufferUtil.floatToBuffer(vertices))

        // Указываем массив текстурных координат
        gl.glTexCoordPointer(2, GLES10.GL_FLOAT, 0, BufferUtil.floatToBuffer(textureCoords))

        // Рисуем квадрат
        gl.glDrawArrays(GLES10.GL_TRIANGLE_STRIP, 0, 4)

        // Отключаем массивы
        GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY)
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY)
    }
}
