package com.example.myapplication

import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlin.random.Random


data class AdvertisementItem(
    val id: Int,
    val title: String,
    var likes: Int = 0
)

class AdvertisementViewModel : ViewModel(){
    private val advertisements = mutableStateListOf(
        AdvertisementItem(1, "Учёные нашли способ продлевать жизнь на 20 лет\n" +
                "Исследователи из Института здоровья объявили о разработке метода, который может значительно замедлить старение клеток."),
        AdvertisementItem(2, "Роботы начнут выполнять домашние дела уже в следующем году\n" +
                "Крупная технологическая компания представила робота-домохозяина, который сможет убирать, готовить и даже присматривать за детьми."),
        AdvertisementItem(3, "Новая платформа для работы из любой точки мира\n" +
                "Стартап представил революционную платформу для удалённой работы, которая позволяет работать без доступа к интернету в отдалённых уголках планеты."),
        AdvertisementItem(4, "Марс готовится принять первых колонизаторов\n" +
                "Космическое агентство объявило, что первые люди высадятся на Марсе уже через два года. Начались работы по строительству первых жилых комплексов."),
        AdvertisementItem(5, "Запущена программа по борьбе с изменением климата\n" +
                "Международная инициатива объединила более 50 стран для разработки совместного плана по снижению выбросов углекислого газа."),
        AdvertisementItem(6, "Летающие машины могут появиться в массовом производстве к 2030 году\n" +
                "Компания \"FutureAir\" заявила, что завершает тестирование первого прототипа безопасного летающего автомобиля для городских условий."),
        AdvertisementItem(7, "Мировой рекорд по скоростному бегу побит спортсменом из Кении\n" +
                "На мировом чемпионате спортсмен установил новый рекорд, пробежав марафон за 1 час и 50 минут."),
        AdvertisementItem(8, "Мобильные устройства будущего будут работать без подзарядки\n" +
                "Разработчики создали технологию, которая позволяет устройствам использовать энергию окружающей среды для работы, устранив необходимость в подзарядке."),
        AdvertisementItem(9, "Новая социальная сеть взорвала интернет за сутки\n" +
                "Приложение \"ConnectAll\" собрало более 10 миллионов пользователей в первый день запуска, предложив уникальный формат общения без текстовых сообщений."),
        AdvertisementItem(10, "Виртуальная реальность становится новой реальностью\n" +
                "Создано первое VR-пространство, в котором люди могут жить, работать и взаимодействовать, не выходя из дома.")
    )

    var displayedAdvertisements by mutableStateOf(listOf<AdvertisementItem>())
        private set

    init {
        displayedAdvertisements = advertisements.take(4)
    }

    fun likeAdvertisement(advertisement: AdvertisementItem) {
        val updatedList = displayedAdvertisements.map {
            if (it.id == advertisement.id) {
                it.copy(likes = it.likes + 1)
            } else {
                it
            }
        }
        displayedAdvertisements = updatedList
    }

    fun updateRandomNews() {
        val unusedAdvertisements = advertisements - displayedAdvertisements
        val randomIndex = Random.nextInt(displayedAdvertisements.size)
        val randomAdvertisements = unusedAdvertisements.random()
        displayedAdvertisements = displayedAdvertisements.toMutableList().also {
            it[randomIndex] = randomAdvertisements
        }
    }
}

@Composable
private fun AdvertisementWindow(viewModel: AdvertisementViewModel = viewModel(), activity: MainActivity) {
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            viewModel.updateRandomNews()
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(Modifier.weight(1f)) {
            Advertisement(modifier = Modifier.weight(1f), advertisement = viewModel.displayedAdvertisements[0], onLike = { viewModel.likeAdvertisement(it) })
            Advertisement(modifier = Modifier.weight(1f), advertisement = viewModel.displayedAdvertisements[1], onLike = { viewModel.likeAdvertisement(it) })
        }
        Row(Modifier.weight(1f)) {
            Advertisement(modifier = Modifier.weight(1f), advertisement = viewModel.displayedAdvertisements[2], onLike = { viewModel.likeAdvertisement(it) })
            Advertisement(modifier = Modifier.weight(1f), advertisement = viewModel.displayedAdvertisements[3], onLike = { viewModel.likeAdvertisement(it) })
        }

        // Добавляем кнопку для перехода на экран с кубом
        Spacer(modifier = Modifier.height(16.dp)) // Добавляем отступ
        Button(
            onClick = {
                // Запускаем CubeActivity по нажатию кнопки
                activity.startActivity(Intent(activity, CubeActivity::class.java))
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Показать Куб")
        }
    }
}

@Composable
private fun Advertisement(modifier: Modifier = Modifier, advertisement: AdvertisementItem, onLike: (AdvertisementItem) -> Unit) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Текст новости с прокруткой
                Box(
                    modifier = Modifier
                        .weight(9f) // 90% высоты
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()) // Добавляем прокрутку
                ) {
                    Text(
                        text = "${advertisement.id}. ${advertisement.title}",
                        color = Color.Green,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                // Панель лайков занимает 10% от высоты и находится в центре
                Box(
                    modifier = Modifier
                        .weight(1f) // 10% высоты
                        .background(Color.Blue.copy(alpha = 0.5f))
                        .clickable { onLike(advertisement) }
                        .fillMaxWidth(), // Заполняет ширину для центрирования внутри
                    contentAlignment = Alignment.Center // Центрирование по вертикали и горизонтали
                ) {
                    Text(
                        text = "Лайки: ${advertisement.likes}",
                        color = Color.White
                    )
                }
            }
        }
    }
}

class MainActivity : ComponentActivity() {

    private lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Создаем GLSurfaceView для рендеринга OpenGL
        glSurfaceView = GLSurfaceView(this).apply {
            setEGLContextClientVersion(1)
            setRenderer(MyRenderer())  // Используем рендерер для куба
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }

        setContent {
            // Управляем состоянием: показывать новости или куб
            var showCube by remember { mutableStateOf(false) }

            Column(modifier = Modifier.fillMaxSize()) {
                if (showCube) {
                    // Отображаем OpenGL куб
                    AndroidView(factory = { glSurfaceView })
                } else {
                    // Отображаем новости
                    //AdvertisementWindow()
                }

                // Кнопка для переключения между новостями и кубом
                Button(
                    onClick = { showCube = !showCube },  // Переключение состояния
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = if (showCube) "Показать новости" else "Показать куб")
                }
            }
        }

        // Устанавливаем флаг для предотвращения выключения экрана
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}


