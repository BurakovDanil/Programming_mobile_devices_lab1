package com.example.myapplication

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

class Cube {
    private val vertices = floatArrayOf(
        -1.0f, -1.0f, -1.0f,  // Низ задней грани
        1.0f, -1.0f, -1.0f,  // Низ передней грани
        1.0f,  1.0f, -1.0f,  // Верх передней грани
        -1.0f,  1.0f, -1.0f,  // Верх задней грани
        -1.0f, -1.0f,  1.0f,  // Низ задней грани (спереди)
        1.0f, -1.0f,  1.0f,  // Низ передней грани (спереди)
        1.0f,  1.0f,  1.0f,  // Верх передней грани (спереди)
        -1.0f,  1.0f,  1.0f   // Верх задней грани (спереди)
    )

    private val indices = byteArrayOf(
        0, 1, 3, 1, 2, 3,  // Задняя грань
        4, 5, 7, 5, 6, 7,  // Передняя грань
        0, 1, 4, 1, 5, 4,  // Нижняя грань
        2, 3, 6, 3, 7, 6,  // Верхняя грань
        0, 3, 4, 3, 7, 4,  // Левая грань
        1, 2, 5, 2, 6, 5   // Правая грань
    )

    private val colors = floatArrayOf(
        1.0f, 0.0f, 0.0f, 1.0f,  // Красный
        0.0f, 1.0f, 0.0f, 1.0f,  // Зеленый
        0.0f, 0.0f, 1.0f, 1.0f,  // Синий
        1.0f, 1.0f, 0.0f, 1.0f,  // Желтый
        1.0f, 0.0f, 1.0f, 1.0f,  // Фиолетовый
        0.0f, 1.0f, 1.0f, 1.0f,  // Голубой
        1.0f, 1.0f, 1.0f, 1.0f,  // Белый
        0.0f, 0.0f, 0.0f, 1.0f   // Черный
    )

    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var indexBuffer: ByteBuffer
    private lateinit var colorBuffer: FloatBuffer

    init {
        try {
            // Буфер для вершин
            val vb = ByteBuffer.allocateDirect(vertices.size * 4)
            vb.order(ByteOrder.nativeOrder())
            vertexBuffer = vb.asFloatBuffer()
            vertexBuffer.put(vertices)
            vertexBuffer.position(0)

            // Буфер для индексов
            indexBuffer = ByteBuffer.allocateDirect(indices.size)
            indexBuffer.put(indices)
            indexBuffer.position(0)

            // Буфер для цветов
            val cb = ByteBuffer.allocateDirect(colors.size * 4)
            cb.order(ByteOrder.nativeOrder())
            colorBuffer = cb.asFloatBuffer()
            colorBuffer.put(colors)
            colorBuffer.position(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun draw(gl: GL10) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY)

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer)
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer)

        gl.glDrawElements(GL10.GL_TRIANGLES, indices.size, GL10.GL_UNSIGNED_BYTE, indexBuffer)

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY)
    }
}


