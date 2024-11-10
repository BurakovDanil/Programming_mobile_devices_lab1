package com.example.myapplication

import android.os.Bundle
import android.opengl.GLSurfaceView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity

class MoonInfoActivity : ComponentActivity() {

    private lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glSurfaceView = GLSurfaceView(this).apply {
            setEGLContextClientVersion(1)
            setRenderer(MoonRenderer(this@MoonInfoActivity))
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }

        setContentView(glSurfaceView)
    }
}

