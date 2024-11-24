package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

class NeptuneInfoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                NeptuneInfoScreen()
            }
        }
    }
}

@Composable
fun NeptuneInfoScreen() {
    val context = LocalContext

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(400.dp)
                    .background(Color.Black, shape = RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                AndroidView(factory = { context -> NeptuneView(context) })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Нептун — восьмая по удалённости от Солнца планета Солнечной системы." +
                        "Масса планеты превышает массу Земли в 17,2 раза, а диаметр превышает земной в 3,9 раза." +
                        "В атмосфере Нептуна бушуют самые сильные ветры среди планет Солнечной системы. По некоторым оценкам, их скорость может достигать 600 м/с."
                ,


                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { (context as Activity).finish() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Назад")
            }
        }
    }
}
