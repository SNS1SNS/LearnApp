package com.example.learnapp

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnapp.ui.theme.LearnAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.parcel.Parcelize

@Suppress("DEPRECATED_ANNOTATION")
@Parcelize
data class LevelProgress(
    val level: String,
    val description: String,
    var completedWords: Int = 0,
    var totalWords: Int = 0
) : Parcelable

class LanguageDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectedLanguage = intent.getStringExtra("SELECTED_LANGUAGE") ?: "Unknown"

        setContent {
            LearnAppTheme {
                LanguageDetailScreen(selectedLanguage = selectedLanguage)
            }
        }
    }
}

@Composable
fun LanguageDetailScreen(selectedLanguage: String) {
    // Firebase Auth и Firestore
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"

    // Хранение списка уровней с прогрессом
    var levelsWithProgress by remember { mutableStateOf(listOf<LevelProgress>()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val levelData = listOf(
            LevelProgress("A1", "1-100 words"),
            LevelProgress("A2", "101 - 1k words"),
            LevelProgress("B1", "1k - 2k words"),
            LevelProgress("B2", "2k - 3k words"),
            LevelProgress("C1", "3k - 4k words"),
            LevelProgress("C2", "4k - 5k words")
        )

        val loadedLevels = mutableListOf<LevelProgress>()

        for (level in levelData) {
            val levelProgress = level.copy()

            val wordsTask = db.collection("words").document(level.level).get()
            wordsTask.addOnSuccessListener { document ->
                if (document.exists()) {
                    val totalWords = (document["words"] as? List<Map<String, String>>)?.size ?: 0
                    levelProgress.totalWords = totalWords
                }

                val progressTask = db.collection("users")
                    .document(userId)
                    .collection(level.level)
                    .whereEqualTo("learn", true)
                    .get()

                progressTask.addOnSuccessListener { snapshot ->
                    levelProgress.completedWords = snapshot.size()
                    loadedLevels.add(levelProgress)

                    if (loadedLevels.size == levelData.size) {
                        // Сортируем уровни перед отображением
                        levelsWithProgress = loadedLevels.sortedWith(compareBy { getOrderIndex(it.level) })
                        loading = false
                    }
                }.addOnFailureListener {
                    loadedLevels.add(levelProgress)
                    if (loadedLevels.size == levelData.size) {
                        levelsWithProgress = loadedLevels.sortedWith(compareBy { getOrderIndex(it.level) })
                        loading = false
                    }
                }
            }.addOnFailureListener {
                loadedLevels.add(levelProgress)
                if (loadedLevels.size == levelData.size) {
                    levelsWithProgress = loadedLevels.sortedWith(compareBy { getOrderIndex(it.level) })
                    loading = false
                }
            }
        }

}

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    } else {
        LanguageDetailContent(selectedLanguage, levelsWithProgress)
    }
}
fun getOrderIndex(level: String): Int {
    return when (level) {
        "A1" -> 1
        "A2" -> 2
        "B1" -> 3
        "B2" -> 4
        "C1" -> 5
        "C2" -> 6
        else -> Int.MAX_VALUE // Для непредусмотренных уровней
    }
}

@Composable
fun LanguageDetailContent(selectedLanguage: String, levelsWithProgress: List<LevelProgress>) {
    val context = LocalContext.current
    var selectedLevels by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF14161B))
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        Text(
            text = "Select categories for learning $selectedLanguage",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            levelsWithProgress.forEach { level ->
                val isSelected = selectedLevels.contains(level.level)
                LanguageLevelItem(
                    level = level.level,
                    description = "${level.completedWords} / ${level.totalWords} words learned",
                    progress = if (level.totalWords > 0) (level.completedWords * 100) / level.totalWords else 0,
                    isSelected = isSelected,
                    onClick = {
                        selectedLevels = if (isSelected) {
                            selectedLevels - level.level
                        } else {
                            selectedLevels + level.level
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        Button(
            onClick = {
                val selectedLevelList = levelsWithProgress.filter { selectedLevels.contains(it.level) }
                val intent = Intent(context, LanguageProgressActivity::class.java)
                intent.putParcelableArrayListExtra("SELECTED_LEVELS", ArrayList(selectedLevelList))
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Continue", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LanguageLevelItem(
    level: String,
    description: String,
    progress: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) Color(0xFF1E88E5).copy(alpha = 0.2f) else Color(0xFF1C1C1E),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Левый блок с обозначением уровня
        Box(
            modifier = Modifier
                .size(40.dp)
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
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Колонка с текстом и прогрессом
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = description,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Прогресс-бар с Canvas API
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color(0xFF2E2E32), shape = RoundedCornerShape(4.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress / 100f)
                        .height(8.dp)
                        .background(getLevelColor(level), shape = RoundedCornerShape(4.dp))
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Чекбокс
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onClick() },
            colors = CheckboxDefaults.colors(
                checkmarkColor = Color.Black,
                uncheckedColor = Color.Gray,
                checkedColor = Color(0xFF1E88E5)
            )
        )
    }
}






