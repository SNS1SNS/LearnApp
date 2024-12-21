package com.example.learnapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Word(
    val name: String = "",
    val translation: String = "",
    val pronunciation: String = "", // Добавлено поле
    var repetitions: Int = 0,
    var learn: Boolean = false
)

class WordListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedLevels = intent.getParcelableArrayListExtra<LevelProgress>("SELECTED_LEVELS") ?: emptyList()
        setContent {
            WordListScreen(selectedLevels)
        }
    }
}

@Composable
fun WordListScreen(selectedLevels: List<LevelProgress>) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"
    var allWords by remember { mutableStateOf(mapOf<String, List<Word>>()) }
    var loading by remember { mutableStateOf(true) }

    // Загрузка данных из Firestore
    LaunchedEffect(selectedLevels) {
        val levelWords = mutableMapOf<String, List<Word>>()
        var completedLevels = 0 // Счётчик загруженных уровней

        selectedLevels.forEach { levelProgress ->
            db.collection("words").document(levelProgress.level).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val words = (document["words"] as? List<Map<String, String>>)?.map {
                            Word(
                                name = it["name"].orEmpty(),
                                translation = it["translation"].orEmpty(),
                                repetitions = 0,
                                learn = false
                            )
                        } ?: emptyList()

                        // Проверяем прогресс пользователя
                        db.collection("users")
                            .document(userId)
                            .collection(levelProgress.level)
                            .get()
                            .addOnSuccessListener { snapshot ->
                                val progressMap = snapshot.documents.associate { doc ->
                                    val repetitions = (doc["repetitions"] as? Long)?.toInt() ?: 0
                                    val learn = (doc["learn"] as? Boolean) ?: false
                                    doc.id to Word(
                                        name = doc.id,
                                        translation = doc["translation"].toString(),
                                        repetitions = repetitions,
                                        learn = learn
                                    )
                                }

                                levelWords[levelProgress.level] = words.map { word ->
                                    progressMap[word.name] ?: word
                                }
                                completedLevels++

                                // Проверяем, загрузились ли все уровни
                                if (completedLevels == selectedLevels.size) {
                                    allWords = levelWords
                                    loading = false
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.e("FirestoreError", "Error fetching user progress", exception)
                                completedLevels++
                                if (completedLevels == selectedLevels.size) {
                                    allWords = levelWords
                                    loading = false
                                }
                            }
                    } else {
                        completedLevels++
                        if (completedLevels == selectedLevels.size) {
                            allWords = levelWords
                            loading = false
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FirestoreError", "Error fetching words for level ${levelProgress.level}", exception)
                    completedLevels++
                    if (completedLevels == selectedLevels.size) {
                        allWords = levelWords
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF14161B))
                .padding(16.dp)
        ) {
            allWords.forEach { (level, words) ->
                item {
                    Text(
                        text = "$level Level",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(words) { word ->
                    WordItem(
                        word = word,
                        level = level, // Передача текущего уровня
                        onLearnClick = {
                            if (word.repetitions < 4) {
                                val updatedWord = word.copy(repetitions = word.repetitions + 1)
                                if (updatedWord.repetitions >= 4) updatedWord.learn = true

                                allWords = allWords.mapValues { entry ->
                                    if (entry.key == level) {
                                        entry.value.map {
                                            if (it.name == word.name) updatedWord else it
                                        }
                                    } else entry.value
                                }

                                updateWordProgress(db, userId, level, updatedWord)
                            }
                        }
                    )
                }

            }
        }
    }
}
@Composable
fun WordItem(
    word: Word,
    maxSteps: Int = 4,
    level: String, // Уровень передается как параметр
    onLearnClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFF1C1C1E), shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Название и перевод слова
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = word.name,
                color = if (word.learn) Color.Gray else Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = word.translation,
                color = if (word.learn) Color.Gray else Color.LightGray,
                fontSize = 14.sp
            )
        }

        // Прогресс-индикаторы
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(maxSteps) { index ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = if (index < word.repetitions) Color(0xFF108C15) else Color.Gray,
                            shape = CircleShape
                        )
                )
                if (index < maxSteps - 1) {
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Кнопка Check
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (word.learn) Color(0xFF108C15) else Color(0xFF2E2E32),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Check",
                tint = if (word.learn) Color.White else Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        val context = LocalContext.current
        // Кнопка Learn
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"
        Button(
            onClick = {
                val intent = Intent(context, LearnWordActivity::class.java).apply {
                    putExtra("USER_ID", userId)
                    putExtra("LEVEL", level)
                    putExtra("WORD_NAME", word.name)
                    putExtra("WORD_TRANSLATION", word.translation)
                    putExtra("WORD_PRONUNCIATION", word.pronunciation) // Добавлено это поле
                    putExtra("WORD_REPETITIONS", word.repetitions)
                    putExtra("WORD_LEARN", word.learn)
                }
                context.startActivity(intent)
            },
            modifier = Modifier
                .height(36.dp)
                .padding(horizontal = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        )
        {
            Text(text = "Learn")
        }
    }
}



fun updateWordProgress(db: FirebaseFirestore, userId: String, level: String, word: Word) {
    db.collection("users")
        .document(userId)
        .collection(level)
        .document(word.name)
        .set(
            mapOf(
                "name" to word.name,
                "translation" to word.translation,
                "repetitions" to word.repetitions,
                "learn" to word.learn
            )
        )
        .addOnSuccessListener { Log.d("FirestoreUpdate", "Word progress updated successfully!") }
        .addOnFailureListener { e -> Log.e("FirestoreUpdate", "Error updating word progress", e) }
}
