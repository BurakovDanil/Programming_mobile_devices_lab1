package com.example.myapplication

import android.content.Context
import android.opengl.GLSurfaceView
import android.opengl.GLU
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val square = TexturedSquare(context)
    private val cube = Cube()
    private var rotationAngle = 0f

    private val sun = Sphere(2f)  // Солнце
    private val mercury = Sphere(0.2f)
    private val venus = Sphere(0.5f)
    private val earth = Sphere(0.5f)
    private val mars = Sphere(0.3f)
    private val jupiter = Sphere(1.5f)
    private val saturn = Sphere(1.2f)
    private val uranus = Sphere(1.0f)
    private val neptune = Sphere(0.8f)
    private val moon = Sphere(0.1f) // Луна

    // Переменные для углов вращения
    private var mercuryOrbit = 0f
    private var venusOrbit = 0f
    private var earthOrbit = 0f
    private var marsOrbit = 0f
    private var jupiterOrbit = 0f
    private var saturnOrbit = 0f
    private var uranusOrbit = 0f
    private var neptuneOrbit = 0f
    private var moonOrbit = 0f

    //private var angleEarthOrbit = 0f
    //private var angleMoonOrbit = 0f

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // Настройка среды
        gl.glClearColor(0f, 0f, 0f, 1f)

        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glEnable(GL10.GL_DEPTH_TEST)

        // Загрузка текстуры для квадрата
        square.loadTexture(gl)

        gl.glClearColor(0f, 0f, 0f, 1f)
        gl.glEnable(GL10.GL_DEPTH_TEST)
        gl.glEnable(GL10.GL_TEXTURE_2D)

        // Загрузка текстур для планет
        sun.loadTexture(gl, context, R.drawable.sun)
        mercury.loadTexture(gl, context, R.drawable.mercury)
        venus.loadTexture(gl, context, R.drawable.venus)
        earth.loadTexture(gl, context, R.drawable.earth)
        mars.loadTexture(gl, context, R.drawable.mars)
        jupiter.loadTexture(gl, context, R.drawable.jupiter)
        saturn.loadTexture(gl, context, R.drawable.saturn)
        uranus.loadTexture(gl, context, R.drawable.uranus)
        neptune.loadTexture(gl, context, R.drawable.neptune)
        moon.loadTexture(gl, context, R.drawable.moon)

    }

    override fun onDrawFrame(gl: GL10) {
        // Очистка экрана и буфера глубины
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

        // (текстурированный квадрат)
        gl.glLoadIdentity()
        gl.glTranslatef(0f, 0f, -30f)  // Отодвигаем квадрат на задний план
        gl.glPushMatrix() // Сохраняем состояние матрицы для фона
        gl.glTranslatef(0f, 0f, -35f)  // Отодвигаем фон дальше от планет
        gl.glScalef(25f, 25f, 1.5f)       // Масштабируем, чтобы покрывал весь экран
        // Включаем текстуру снова, если это нужно для следующей отрисовки
        gl.glEnable(GL10.GL_TEXTURE_2D)
        square.draw(gl)
        gl.glPopMatrix()

        /*gl.glLoadIdentity()
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
*/

        // Рисуем Солнце в центре сцены
        gl.glPushMatrix()  // Сохраняем текущее состояние матрицы
        gl.glTranslatef(0f, 0f, -10f)  // Солнце в центре координат (не смещено)
        sun.draw(gl)
        gl.glPopMatrix()  // Восстанавливаем матрицу для рисования планет

        // Рисуем Меркурий
        drawPlanet(gl, mercury, 3f, mercuryOrbit)

        // Рисуем Венеру
        drawPlanet(gl, venus, 5f, venusOrbit)

        // Рисуем Землю и Луну
        drawPlanetWithMoon(gl, earth, moon, 7f, earthOrbit, moonOrbit)

        // Рисуем Марс
        drawPlanet(gl, mars, 9f, marsOrbit)

        // Рисуем Юпитер
        drawPlanet(gl, jupiter, 12f, jupiterOrbit)

        // Рисуем Сатурн
        drawPlanet(gl, saturn, 15f, saturnOrbit)

        // Рисуем Уран
        drawPlanet(gl, uranus, 18f, uranusOrbit)

        // Рисуем Нептун
        drawPlanet(gl, neptune, 21f, neptuneOrbit)


        // Обновляем углы вращения для следующего кадра
        updateOrbits()
    }

    private fun drawPlanet(gl: GL10, planet: Sphere, distanceFromSun: Float, orbitAngle: Float) {
        gl.glPushMatrix()
        gl.glTranslatef(0f, 0f, -10f)  // Центр Солнечной системы (Солнце в центре)
        gl.glRotatef(orbitAngle, 0f, 1f, 0f)  // Орбитальное вращение
        gl.glTranslatef(distanceFromSun, 0f, 0f)  // Расстояние от Солнца
        planet.draw(gl)  // Рисуем планету
        gl.glPopMatrix()
    }

    private fun drawPlanetWithMoon(gl: GL10, planet: Sphere, moon: Sphere, distanceFromSun: Float, planetOrbit: Float, moonOrbit: Float) {
        gl.glPushMatrix()
        gl.glTranslatef(0f, 0f, -10f)  // Центр Солнечной системы
        gl.glRotatef(planetOrbit, 0f, 1f, 0f)  // Орбитальное вращение планеты
        gl.glTranslatef(distanceFromSun, 0f, 0f)  // Расстояние от Солнца

        // Рисуем планету
        planet.draw(gl)

        // Рисуем Луну
        gl.glPushMatrix()
        gl.glRotatef(moonOrbit, 0f, 0f, 1f)  // Вращение Луны вокруг Земли (перпендикулярно плоскости)
        gl.glTranslatef(1f, 0f, 0f)  // Расстояние Луны от Земли
        moon.draw(gl)
        gl.glPopMatrix()

        gl.glPopMatrix()
    }

    private fun updateOrbits() {
        mercuryOrbit += 1.59f  // Скорость вращения Меркурия
        venusOrbit += 1.18f  // Скорость вращения Венеры
        earthOrbit += 1f  // Скорость вращения Земли
        marsOrbit += 0.8f  // Скорость вращения Марса
        jupiterOrbit += 0.4f  // Скорость вращения Юпитера
        saturnOrbit += 0.3f  // Скорость вращения Сатурна
        uranusOrbit += 0.2f  // Скорость вращения Урана
        neptuneOrbit += 0.1f  // Скорость вращения Нептуна
        moonOrbit += 2f  // Вращение Луны вокруг Земли
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        // Установка области видимости и проекции
        /*gl.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()
        gl.glFrustumf(-ratio, ratio, -1f, 1f, 1f, 10f)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()*/
        gl.glViewport(0, 0, width, height)
        gl.glMatrixMode(GL10.GL_PROJECTION)
        gl.glLoadIdentity()
        GLU.gluPerspective(gl, 45.0f, width.toFloat() / height.toFloat(), 1f, 100f)
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        gl.glLoadIdentity()
    }
}




