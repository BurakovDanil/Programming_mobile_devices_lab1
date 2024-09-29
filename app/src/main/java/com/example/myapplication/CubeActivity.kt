package com.example.myapplication

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity

class CubeActivity : ComponentActivity() {

    private lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurfaceView = GLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(1)
        setContentView(glSurfaceView)
    }
}
