package com.example.myapplication

import android.content.Context
import android.opengl.GLES10
import android.opengl.GLUtils
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Square(private val context: Context) {

    private val vertices = floatArrayOf(
        -1f, -1f, 0f,
        1f, -1f, 0f,
        -1f, 1f, 0f,
        1f, 1f, 0f
    )

    private val textureCoords = floatArrayOf(
        0f, 1f,
        1f, 1f,
        0f, 0f,
        1f, 0f
    )

    private val textureIds = IntArray(1)

    fun loadTexture(gl: GL10) {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.space1)
        GLES10.glGenTextures(1, textureIds, 0)
        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, textureIds[0])
        GLUtils.texImage2D(GLES10.GL_TEXTURE_2D, 0, bitmap, 0)
        GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_NEAREST.toFloat())
        GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_LINEAR.toFloat())
        bitmap.recycle()
    }

    fun draw(gl: GL10) {
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY)
        GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY)
        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, textureIds[0])

        gl.glVertexPointer(3, GLES10.GL_FLOAT, 0, BufferUtil.floatToBuffer(vertices))
        gl.glTexCoordPointer(2, GLES10.GL_FLOAT, 0, BufferUtil.floatToBuffer(textureCoords))
        gl.glDrawArrays(GLES10.GL_TRIANGLE_STRIP, 0, 4)

        GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY)
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY)
    }
}