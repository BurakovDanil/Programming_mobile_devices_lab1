package com.example.myapplication

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

object BufferUtil {
    fun floatToBuffer(arr: FloatArray): FloatBuffer {
        bb.order(ByteOrder.nativeOrder())
        val fb = bb.asFloatBuffer()
        fb.put(arr)
        fb.position(0)
        return fb
    }
}