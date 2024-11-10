package com.example.myapplication

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MoonView(context: Context) : GLSurfaceView(context) {
    init {
        // Устанавливаем версию OpenGL ES 2.0
        setEGLContextClientVersion(2)
        // Устанавливаем рендерер, который будет отвечать за отрисовку сцены
        setRenderer(MoonRenderer(context))
    }

    private class MoonRenderer(private val context: Context) : Renderer {
        private var shaderProgram = 0  // Программа шейдера
        private lateinit var moon: SphereES2  // Объект Луны (сфера)
        private val modelMatrix = FloatArray(16)  // Матрица модели
        private val viewMatrix = FloatArray(16)  // Матрица вида (камеры)
        private val projectionMatrix = FloatArray(16)  // Матрица проекции
        private val mvpMatrix = FloatArray(16)  // Матрица MVP (модель + вид + проекция)
        private var textureId = 0  // Идентификатор текстуры
        private val lightPosition = floatArrayOf(3f, 5f, 6f)  // Позиция источника света

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            // Настройка начальных параметров OpenGL
            GLES20.glClearColor(0f, 0f, 0f, 1f)  // Устанавливаем цвет фона (черный)
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)  // Включаем тест глубины для 3D
            moon = SphereES2(0.5f, 30, 30)  // Создаем объект Луны (сферу)
            shaderProgram = createShaderProgram(vertexShaderSource, fragmentShaderSource)  // Создаем шейдер
            textureId = loadTexture(R.drawable.moon)  // Загружаем текстуру для Луны
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            // Настройка вида при изменении размера окна
            GLES20.glViewport(0, 0, width, height)  // Устанавливаем размер области рендеринга
            val aspectRatio = width.toFloat() / height.toFloat()  // Соотношение сторон экрана
            Matrix.perspectiveM(projectionMatrix, 0, 45f, aspectRatio, 1f, 10f)  // Устанавливаем перспективную проекцию
            Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1f, 0f)  // Устанавливаем камеру
        }

        override fun onDrawFrame(gl: GL10?) {
            // Отрисовываем каждый кадр
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)  // Очищаем экран и буфер глубины
            Matrix.setIdentityM(modelMatrix, 0)  // Сбрасываем матрицу модели
            Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)  // Применяем матрицу вида к модели
            Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)  // Применяем проекцию

            GLES20.glUseProgram(shaderProgram)  // Используем программу шейдера

            // Передаем матрицу MVP в шейдер
            GLES20.glUniformMatrix4fv(
                GLES20.glGetUniformLocation(shaderProgram, "uMVPMatrix"),
                1, false, mvpMatrix, 0
            )

            // Передаем позицию источника света в шейдер
            GLES20.glUniform3f(
                GLES20.glGetUniformLocation(shaderProgram, "uLightPosition"),
                lightPosition[0], lightPosition[1], lightPosition[2]
            )

            // Передаем матрицу модели в шейдер
            GLES20.glUniformMatrix4fv(
                GLES20.glGetUniformLocation(shaderProgram, "uModelMatrix"),
                1, false, modelMatrix, 0
            )

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)  // Активируем текстуру
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)  // Привязываем текстуру
            GLES20.glUniform1i(
                GLES20.glGetUniformLocation(shaderProgram, "uTexture"),
                0
            )

            moon.draw(shaderProgram)  // Рисуем Луну
        }

        // Загружаем текстуру из ресурса
        private fun loadTexture(resourceId: Int): Int {
            val textureHandle = IntArray(1)  // Массив для хранения идентификатора текстуры
            GLES20.glGenTextures(1, textureHandle, 0)  // Генерируем текстуру
            if (textureHandle[0] != 0) {
                val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)  // Загружаем изображение
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])  // Привязываем текстуру
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)  // Устанавливаем параметры текстуры
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)  // Загружаем изображение в текстуру
                bitmap.recycle()  // Освобождаем ресурсы изображения
            } else {
                throw RuntimeException("Ошибка загрузки текстуры.")  // Ошибка, если не удалось создать текстуру
            }
            return textureHandle[0]  // Возвращаем идентификатор текстуры
        }

        // Создаем программу шейдера
        private fun createShaderProgram(vertexSource: String, fragmentSource: String): Int {
            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)  // Загружаем вершинный шейдер
            val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)  // Загружаем фрагментный шейдер
            return GLES20.glCreateProgram().apply {
                GLES20.glAttachShader(this, vertexShader)  // Прикрепляем вершинный шейдер
                GLES20.glAttachShader(this, fragmentShader)  // Прикрепляем фрагментный шейдер
                GLES20.glLinkProgram(this)  // Линкуем программу шейдера
            }
        }

        // Загружаем шейдер
        private fun loadShader(type: Int, shaderSource: String): Int {
            return GLES20.glCreateShader(type).also { shader ->
                GLES20.glShaderSource(shader, shaderSource)  // Загружаем исходный код шейдера
                GLES20.glCompileShader(shader)  // Компилируем шейдер
            }
        }

        // Исходный код вершинного шейдера
        private val vertexShaderSource = """
            uniform mat4 uMVPMatrix;
            uniform mat4 uModelMatrix;
            attribute vec4 aPosition;
            attribute vec3 aNormal;
            attribute vec2 aTexCoordinate;
            varying vec3 vNormal;
            varying vec3 vPosition;
            varying vec2 vTexCoordinate;
            void main() {
                vec4 worldPosition = uModelMatrix * aPosition;
                vPosition = worldPosition.xyz;
                vNormal = mat3(uModelMatrix) * aNormal;
                vTexCoordinate = aTexCoordinate;
                gl_Position = uMVPMatrix * aPosition;
            }
        """

        // Исходный код фрагментного шейдера
        private val fragmentShaderSource = """
            precision mediump float;
            varying vec3 vNormal;
            varying vec3 vPosition;
            varying vec2 vTexCoordinate;
            uniform vec3 uLightPosition;
            uniform sampler2D uTexture;
            void main() {
                vec3 normal = normalize(vNormal);
                vec3 lightDir = normalize(uLightPosition - vPosition);
                float lightIntensity = 1.9;  // Множитель интенсивности света
                float diff = max(dot(normal, lightDir), 0.0) * lightIntensity;
                vec3 diffuse = diff * vec3(1.0, 1.0, 1.0);
                vec4 textureColor = texture2D(uTexture, vTexCoordinate);
                gl_FragColor = vec4(diffuse * textureColor.rgb, textureColor.a);
            }
        """
    }
}
