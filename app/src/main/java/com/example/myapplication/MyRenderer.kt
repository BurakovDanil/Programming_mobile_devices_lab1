package com.example.myapplication

import android.content.Context
import android.opengl.GLSurfaceView
import android.opengl.GLU
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val square = TexturedSquare(context)
    private val cube = Cube()
    private var rotationAngle = 0f

    private var selectedPlanetIndex = 0

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

    // черная дыра
    private val blackHole = Sphere(2.9f, shouldRotate = true)
    private var blackHoleState = 0 // 0: снизу вверх, 1: слева направо, 2: снова снизу вверх
    private var blackHolePositionX = 0.0f
    private var blackHolePositionY = -10.0f // Начальная позиция снизу
    private var blackHoleSpeed = 0.08f   // скорость перемещения

    // Массив радиусов планет
    private val planetRadii = floatArrayOf(
        0.2f, // Меркурий
        0.5f, // Венера
        0.5f, // Земля
        0.3f, // Марс
        1.5f, // Юпитер
        1.2f, // Сатурн
        1.0f, // Уран
        0.8f, // Нептун
        0.1f  // Луна
    )

    private var mercuryOrbit = 0f
    private var venusOrbit = 0f
    private var earthOrbit = 0f
    private var marsOrbit = 0f
    private var jupiterOrbit = 0f
    private var saturnOrbit = 0f
    private var uranusOrbit = 0f
    private var neptuneOrbit = 0f
    private var moonOrbit = 0f

    fun setSelectedPlanetIndexPlus() {
        selectedPlanetIndex = (selectedPlanetIndex + 1) % 9
    }

    fun setSelectedPlanetIndexMinus() {
        selectedPlanetIndex = (selectedPlanetIndex - 1 + 9) % 9
    }


    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        gl.glClearColor(0f, 0f, 0f, 1f)
        gl.glEnable(GL10.GL_TEXTURE_2D)
        gl.glEnable(GL10.GL_DEPTH_TEST)

        // Загрузка текстур
        square.loadTexture(gl)
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


        blackHole.loadTexture(gl, context, R.drawable.black_hole);

    }

    override fun onDrawFrame(gl: GL10) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
        gl.glLoadIdentity()
        gl.glTranslatef(0f, 0f, -30f)

        gl.glPushMatrix()
        gl.glTranslatef(0f, 0f, -35f)
        gl.glScalef(25f, 25f, 1.5f)
        gl.glEnable(GL10.GL_TEXTURE_2D)
        square.draw(gl)
        gl.glPopMatrix()

        // Рисуем черную дыру на заднем фоне
        gl.glPushMatrix()
        gl.glLoadIdentity()
        drawBlackHole(gl)
        gl.glPopMatrix()

        // Рисуем Солнце
        gl.glPushMatrix()
        gl.glTranslatef(0f, 0f, 0f)
        sun.draw(gl)
        gl.glPopMatrix()

        // Рисуем планеты
        drawPlanet(gl, mercury, 3f, mercuryOrbit, 0)
        drawPlanet(gl, venus, 5f, venusOrbit, 1)
        drawPlanetWithMoon(gl, earth, moon, 7f, earthOrbit, moonOrbit, 2)
        drawPlanet(gl, mars, 9f, marsOrbit, 3)
        drawPlanet(gl, jupiter, 12f, jupiterOrbit, 4)
        drawPlanet(gl, saturn, 15f, saturnOrbit, 5)
        drawPlanet(gl, uranus, 18f, uranusOrbit, 6)
        drawPlanet(gl, neptune, 21f, neptuneOrbit, 7)

        updateOrbits()



    }

    private fun drawPlanet(gl: GL10, planet: Sphere, distanceFromSun: Float, orbitAngle: Float, index: Int) {
        gl.glPushMatrix()
        gl.glTranslatef(0f, 0f, 0f)
        gl.glRotatef(orbitAngle, 0f, 1f, 0f)
        gl.glTranslatef(distanceFromSun, 0f, 0f)
        planet.draw(gl)

        if (index == selectedPlanetIndex) {
            gl.glPushMatrix()
            gl.glEnable(GL10.GL_BLEND)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
            val planetRadius = planetRadii[index]
            cube.draw(gl, planetRadius)
            gl.glDisable(GL10.GL_BLEND)
            gl.glPopMatrix()
        }

        gl.glPopMatrix()
    }

    private fun drawPlanetWithMoon(gl: GL10, planet: Sphere, moon: Sphere, distanceFromSun: Float, planetOrbit: Float, moonOrbit: Float, index: Int) {
        gl.glPushMatrix()
        gl.glTranslatef(0f, 0f, 0f)
        gl.glRotatef(planetOrbit, 0f, 1f, 0f)
        gl.glTranslatef(distanceFromSun, 0f, 0f)
        planet.draw(gl)

        if (index == selectedPlanetIndex) {
            gl.glPushMatrix()
            gl.glEnable(GL10.GL_BLEND)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
            val planetRadius = planetRadii[index]
            cube.draw(gl, planetRadius)
            gl.glDisable(GL10.GL_BLEND)
            gl.glPopMatrix()
        }

        // Рисуем Луну
        gl.glPushMatrix()
        gl.glRotatef(moonOrbit, 0f, 0f, 1f)
        gl.glTranslatef(1f, 0f, 0f)
        moon.draw(gl)
        // Отображаем куб, если выбрана Луна
        if (selectedPlanetIndex == 8) {
            gl.glPushMatrix()
            gl.glEnable(GL10.GL_BLEND)
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
            cube.draw(gl, 0.1f) // Размер куба для Луны
            gl.glDisable(GL10.GL_BLEND)
            gl.glPopMatrix()
        }

        gl.glPopMatrix() // Заканчиваем отрисовку Луны
        gl.glPopMatrix() // Заканчиваем отрисовку планеты
    }

    private fun updateOrbits() {
        mercuryOrbit += 1.59f
        venusOrbit += 1.18f
        earthOrbit += 1f
        marsOrbit += 0.8f
        jupiterOrbit += 0.4f
        saturnOrbit += 0.3f
        uranusOrbit += 0.2f
        neptuneOrbit += 0.1f
        moonOrbit += 2f
    }

    private fun drawBlackHole(gl: GL10) {
        gl.glPushMatrix()
        gl.glTranslatef(blackHolePositionX, blackHolePositionY, -34f) // Позиция черной дыры
        blackHole.draw(gl)

        // Обновляем позицию в зависимости от состояния
        when (blackHoleState) {
            0 -> { // Движение снизу вверх
                blackHolePositionY += blackHoleSpeed
                if (blackHolePositionY > 25.0f) { // Достигли верхней границы
                    blackHolePositionY = 5.0f // Устанавливаем для слева-направо
                    blackHolePositionX = -25.0f // Слева
                    blackHoleState = 1 // Переход к движению слева направо
                }
            }
            1 -> { // Движение слева направо
                blackHolePositionX += blackHoleSpeed
                if (blackHolePositionX > 25.0f) { // Достигли правой границы
                    blackHolePositionX = 0.0f // Центр
                    blackHolePositionY = -25.0f // Снова вниз
                    blackHoleState = 2 // Переход к движению снизу вверх
                }
            }
            2 -> { // Движение снизу вверх
                blackHolePositionY += blackHoleSpeed
                if (blackHolePositionY > 25.0f) { // Достигли верхней границы
                    blackHolePositionY = 5.0f // Устанавливаем для слева-направо
                    blackHolePositionX = -25.0f // Слева
                    blackHoleState = 1 // Возврат к движению слева направо
                }
            }
        }

        gl.glPopMatrix()
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