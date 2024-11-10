package com.example.myapplication

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class SphereES2(radius: Float, stacks: Int, slices: Int) {

    // Буферы для вершин, нормалей, текстурных координат и индексов
    private val vertexBuffer: FloatBuffer
    private val normalBuffer: FloatBuffer
    private val textureBuffer: FloatBuffer // Для текстурных координат
    private val indexBuffer: ShortBuffer
    private val vertexCount: Int
    private val indexCount: Int

    init {
        // Создаем массивы для вершин, нормалей, текстурных координат и индексов
        val vertices = ArrayList<Float>()
        val normals = ArrayList<Float>()
        val textures = ArrayList<Float>() // Для текстурных координат
        val indices = ArrayList<Short>()

        // Генерация вершин, нормалей и текстурных координат
        for (stack in 0..stacks) {
            // Угол для каждого слоя
            val theta = stack * Math.PI / stacks
            val sinTheta = Math.sin(theta).toFloat()
            val cosTheta = Math.cos(theta).toFloat()

            for (slice in 0..slices) {
                // Угол для каждого сечения
                val phi = slice * 2 * Math.PI / slices
                val sinPhi = Math.sin(phi).toFloat()
                val cosPhi = Math.cos(phi).toFloat()

                // Вычисление координат вершины
                val x = cosPhi * sinTheta
                val y = cosTheta
                val z = sinPhi * sinTheta

                // Добавляем вершины в список
                vertices.add(x * radius)
                vertices.add(y * radius)
                vertices.add(z * radius)

                // Нормали для каждой вершины (направление нормали)
                normals.add(x)
                normals.add(y)
                normals.add(z)

                // Текстурные координаты (u, v)
                val u = slice.toFloat() / slices
                val v = stack.toFloat() / stacks
                textures.add(u)
                textures.add(v)
            }
        }

        // Генерация индексов для отрисовки треугольников
        for (stack in 0 until stacks) {
            for (slice in 0 until slices) {
                val first = (stack * (slices + 1) + slice).toShort() // Индекс первой вершины
                val second = (first + slices + 1).toShort() // Индекс второй вершины
                indices.add(first)
                indices.add(second)
                indices.add((first + 1).toShort()) // Индекс третьей вершины
                indices.add(second)
                indices.add((second + 1).toShort()) // Индекс четвертой вершины
                indices.add((first + 1).toShort()) // Индекс пятой вершины
            }
        }

        // Количество вершин и индексов
        vertexCount = vertices.size / 3
        indexCount = indices.size

        // Создаем буферы для хранения данных в памяти
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(vertices.toFloatArray())  // Переводим список в массив и записываем в буфер
            position(0)  // Сбрасываем позицию в буфере
        }

        normalBuffer = ByteBuffer.allocateDirect(normals.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(normals.toFloatArray())  // Переводим список нормалей в массив и записываем в буфер
            position(0)
        }

        textureBuffer = ByteBuffer.allocateDirect(textures.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().apply {
            put(textures.toFloatArray())  // Переводим список текстурных координат в массив и записываем в буфер
            position(0)
        }

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2).order(ByteOrder.nativeOrder()).asShortBuffer().apply {
            put(indices.toShortArray())  // Переводим список индексов в массив и записываем в буфер
            position(0)
        }
    }

    // Функция для отрисовки сферы
    fun draw(shaderProgram: Int) {
        // Получаем атрибуты из шейдера для вершин, нормалей и текстурных координат
        val positionHandle = GLES20.glGetAttribLocation(shaderProgram, "aPosition")  // Позиция вершин
        val normalHandle = GLES20.glGetAttribLocation(shaderProgram, "aNormal")  // Нормали
        val textureHandle = GLES20.glGetAttribLocation(shaderProgram, "aTexCoordinate")  // Текстурные координаты

        // Включаем атрибуты для вершин
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)  // Указываем, как данные будут интерпретироваться

        // Включаем атрибуты для нормалей
        GLES20.glEnableVertexAttribArray(normalHandle)
        GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer)

        // Включаем атрибуты для текстур
        GLES20.glEnableVertexAttribArray(textureHandle)
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)

        // Рисуем сферу с использованием индексов
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        // Отключаем атрибуты после рисования
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(normalHandle)
        GLES20.glDisableVertexAttribArray(textureHandle)  // Отключаем текстурные атрибуты
    }
}
