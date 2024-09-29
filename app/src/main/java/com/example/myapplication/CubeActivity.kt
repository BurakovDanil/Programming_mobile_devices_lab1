package com.example.myapplication

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity

class CubeActivity : ComponentActivity() {

    private lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Создаем GLSurfaceView для рендеринга OpenGL
        glSurfaceView = GLSurfaceView(this)

        // Устанавливаем версию OpenGL ES 1.0
        glSurfaceView.setEGLContextClientVersion(1)

        // Устанавливаем рендерер
        //glSurfaceView.setRenderer(MyRenderer())

        // Устанавливаем GLSurfaceView как контент этой активности
        setContentView(glSurfaceView)
    }
}
