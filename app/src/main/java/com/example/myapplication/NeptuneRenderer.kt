package com.example.myapplication


import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import com.example.myapplication.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class NeptuneView(context: Context) : GLSurfaceView(context) {
    init {
        // Устанавливаем версию OpenGL ES 2.0
        setEGLContextClientVersion(2)
        // Устанавливаем рендерер
        setRenderer(NeptuneRenderer(context))
        // Опционально: включить постоянный рендеринг
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    private class NeptuneRenderer(private val context: Context) : GLSurfaceView.Renderer {
        private var neptuneShaderProgram: Int = 0
        private lateinit var neptune: SphereES2
        private val modelMatrix = FloatArray(16)
        private val viewMatrix = FloatArray(16)
        private val projectionMatrix = FloatArray(16)
        private val mvpMatrix = FloatArray(16)
        private var textureId: Int = 0
        private var angle: Float = 0f

        private var uTimeHandle: Int = 0
        private var uMVPMatrixHandle: Int = 0
        private var uTextureHandle: Int = 0
        private var startTime: Long = 0

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            // Устанавливаем цвет фона (черный)
            GLES20.glClearColor(0f, 0f, 0f, 1f)
            // Включаем тест глубины
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)

            // Инициализируем сферу Нептуна
            neptune = SphereES2(0.5f, 30, 30)

            // Компилируем и связываем шейдерную программу
            neptuneShaderProgram = createShaderProgram(vertexShaderSource, fragmentShaderSource)

            // Загружаем текстуру Нептуна
            textureId = loadTexture(R.drawable.neptune)

            // Получаем ссылки на униформы
            uMVPMatrixHandle = GLES20.glGetUniformLocation(neptuneShaderProgram, "uMVPMatrix")
            uTimeHandle = GLES20.glGetUniformLocation(neptuneShaderProgram, "uTime")
            uTextureHandle = GLES20.glGetUniformLocation(neptuneShaderProgram, "uTexture")

            // Сохраняем время начала
            startTime = System.currentTimeMillis()
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            // Устанавливаем область просмотра(весь экран)
            GLES20.glViewport(0, 0, width, height)
            val aspectRatio = width.toFloat() / height.toFloat()

            // Настраиваем матрицу проекции - перспектива
            Matrix.perspectiveM(projectionMatrix, 0, 45f, aspectRatio, 1f, 10f)

            // Настраиваем матрицу вида (камера)
            Matrix.setLookAtM(viewMatrix, 0,
                0f, 0f, 3f, // Позиция камеры
                0f, 0f, 0f, // Точка, на которую смотрит камера
                0f, 1f, 0f  // направление вектора "вверх"
            )
        }

        override fun onDrawFrame(gl: GL10?) {
            // Очищаем буфер цвета и буфер глубины
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

            // Обновляем угол вращения
            angle += 0.4f

            // Устанавливаем модельную матрицу
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.rotateM(modelMatrix, 0, angle, 0f, 1f, 0f)

            // Вычисляем итоговую матрицу Model-View-Projection
            Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)

            // Используем шейдерную программу
            GLES20.glUseProgram(neptuneShaderProgram)

            // Передаем матрицу MVP в шейдер
            GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, mvpMatrix, 0)

            // Вычисляем время в секундах для анимации волн
            val currentTime = (System.currentTimeMillis() - startTime) / 1000f
            GLES20.glUniform1f(uTimeHandle, currentTime)

            // Активируем текстурный блок 0 и связываем текстуру
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glUniform1i(uTextureHandle, 0)

            // Отрисовываем сферу
            neptune.draw(neptuneShaderProgram)
        }

        /**
         * Загружает текстуру из ресурсов.
         */
        private fun loadTexture(resourceId: Int): Int {
            val textureHandle = IntArray(1)
            GLES20.glGenTextures(1, textureHandle, 0)

            if (textureHandle[0] != 0) {
                val options = BitmapFactory.Options().apply {
                    inScaled = false // Не масштабировать изображение
                }

                // Загружаем изображение из ресурсов
                val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

                // Связываем текстуру
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

                // Устанавливаем параметры фильтрации
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

                // Загружаем изображение в текстуру
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

                // Освобождаем память, занятую битмапом
                bitmap.recycle()
            } else {
                throw RuntimeException("Ошибка загрузки текстуры.")
            }

            return textureHandle[0]
        }

        /**
         * Создает шейдерную программу из вершинного и фрагментного шейдеров.
         */
        private fun createShaderProgram(vertexSource: String, fragmentSource: String): Int {
            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
            val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
            return GLES20.glCreateProgram().also { program ->
                GLES20.glAttachShader(program, vertexShader)
                GLES20.glAttachShader(program, fragmentShader)
                GLES20.glLinkProgram(program)
            }
        }

        /**
         * Компилирует шейдер заданного типа из исходного кода.
         */
        private fun loadShader(type: Int, shaderSource: String): Int {
            return GLES20.glCreateShader(type).also { shader ->
                GLES20.glShaderSource(shader, shaderSource)
                GLES20.glCompileShader(shader)

                // Проверяем статус компиляции
                val compileStatus = IntArray(1)
                GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
                if (compileStatus[0] == 0) {
                    // Если компиляция не удалась, удаляем шейдер
                    GLES20.glDeleteShader(shader)
                    throw RuntimeException("Ошибка компиляции шейдера: ${GLES20.glGetShaderInfoLog(shader)}")
                }
            }
        }

        // Вершинный шейдер с более выраженными волнами на текстуре
        private val vertexShaderSource = """
    uniform mat4 uMVPMatrix;
    uniform float uTime;
    attribute vec4 aPosition;
    attribute vec2 aTexCoordinate;

    varying vec2 vTexCoordinate;

    void main() {
        // Оставляем позицию вершины неизменной
        gl_Position = uMVPMatrix * aPosition;

        // Увеличиваем амплитуду и частоту для более заметных волн
        float waveAmplitude = 0.3;
        float waveFrequency = 10.0;
        float waveSpeed = 5.0;

        // Смещение текстурных координат по оси x и y
        float offsetX = waveAmplitude * sin(aPosition.y * waveFrequency + uTime * waveSpeed);
        float offsetY = waveAmplitude * cos(aPosition.x * waveFrequency + uTime * waveSpeed);
        
        // Применяем смещение к текстурным координатам
        vTexCoordinate = aTexCoordinate + vec2(offsetX, offsetY);
    }
"""


        // Фрагментный шейдер для отображения текстуры
        private val fragmentShaderSource = """
            precision mediump float;
            varying vec2 vTexCoordinate;
            uniform sampler2D uTexture;
            
            void main() {
                vec4 textureColor = texture2D(uTexture, vTexCoordinate);
                gl_FragColor = textureColor;
            }
        """
    }
}