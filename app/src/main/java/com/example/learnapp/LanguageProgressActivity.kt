package com.example.learnapp

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnapp.ui.theme.LearnAppTheme
import kotlinx.parcelize.Parcelize

class LanguageProgressActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedLevels = intent.getParcelableArrayListExtra<LevelProgress>("SELECTED_LEVELS") ?: emptyList()
        setContent {
            LearnAppTheme {
                LanguageProgressScreen(selectedLevels)
            }
        }
    }
}

@Composable
fun LanguageProgressScreen(levels: List<LevelProgress>) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF14161B))
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        Text(
            text = "Your progress",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Добавляем прокрутку для списка уровней
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()) // Прокрутка
        ) {
            levels.forEach { level ->
                ProgressItemDetailStyle(
                    level = level.level,
                    completedWords = level.completedWords,
                    totalWords = level.totalWords
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Button(
            onClick = {
                // Передача всех уровней через Intent
                val intent = Intent(context, WordListActivity::class.java).apply {
                    putParcelableArrayListExtra("SELECTED_LEVELS", ArrayList(levels))
                }
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Start learning all levels", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
@Composable
fun ProgressItemDetailStyle(level: String, completedWords: Int, totalWords: Int) {
    val progress = if (totalWords > 0) {
        (completedWords.toFloat() / totalWords) * 100
    } else {
        0f
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C1E), shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    color = getLevelColor(level),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = level,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$completedWords / $totalWords words learned",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Прогресс-бар с Box для плавного заполнения
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color(0xFF2E2E32), shape = RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress / 100f) // Динамическая ширина
                        .height(8.dp)
                        .background(getLevelColor(level), shape = RoundedCornerShape(4.dp))
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "${progress.toInt()}%",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Composable
fun getLevelColor(level: String): Color {
    return when (level) {
        "A1" -> Color(0xFF00C853) // Зеленый
        "A2" -> Color(0xFF23622A) // Синий
        "B1" -> Color(0xFFFFC107) // Желтый
        "B2" -> Color(0xFFFF5722) // Оранжевый
        "C1" -> Color(0xFFFF0024) // Фиолетовый
        "C2" -> Color(0xFFB41A37) // Красный
        else -> Color.Gray // Цвет по умолчанию
    }
}
