package com.example.learnapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnapp.ui.theme.LearnAppTheme
import com.google.firebase.firestore.FirebaseFirestore

class LearnWordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("USER_ID") ?: "unknown_user"
        val level = intent.getStringExtra("LEVEL") ?: "A1"
        val wordName = intent.getStringExtra("WORD_NAME") ?: ""
        val wordTranslation = intent.getStringExtra("WORD_TRANSLATION") ?: ""
        var wordPronunciation = intent.getStringExtra("WORD_PRONUNCIATION") ?: ""
        val repetitions = intent.getIntExtra("WORD_REPETITIONS", 0)

        val db = FirebaseFirestore.getInstance()

        // Если pronunciation пустое, загружаем из Firestore
        if (wordPronunciation.isEmpty()) {
            db.collection("words").document(level).get()
                .addOnSuccessListener { document ->
                    val wordsList = document["words"] as? List<Map<String, Any>> ?: emptyList()
                    val selectedWord = wordsList.find { it["name"] == wordName }
                    wordPronunciation = selectedWord?.get("pronunciation")?.toString() ?: ""

                    // После загрузки pronunciation вызываем функцию настройки экрана
                    setupScreen(db, userId, level, wordName, wordTranslation, wordPronunciation, repetitions)
                }
                .addOnFailureListener {
                    // Если загрузка провалилась, вызываем setupScreen с пустым pronunciation
                    setupScreen(db, userId, level, wordName, wordTranslation, wordPronunciation, repetitions)
                }
        } else {
            // Если pronunciation уже заполнено
            setupScreen(db, userId, level, wordName, wordTranslation, wordPronunciation, repetitions)
        }
    }

    private fun setupScreen(
        db: FirebaseFirestore,
        userId: String,
        level: String,
        wordName: String,
        wordTranslation: String,
        wordPronunciation: String,
        repetitions: Int
    ) {
        val firstWord = Word(
            name = wordName,
            translation = wordTranslation,
            pronunciation = wordPronunciation,
            repetitions = repetitions
        )

        db.collection("words").document(level).get()
            .addOnSuccessListener { document ->
                val wordsList = document["words"] as? List<Map<String, Any>> ?: emptyList()
                val wordObjects = wordsList.map {
                    Word(
                        name = it["name"].toString(),
                        translation = it["translation"].toString(),
                        pronunciation = it["pronunciation"].toString(),
                        repetitions = 0
                    )
                }

                // Добавляем переданное слово первым
                val allWords = mutableListOf(firstWord) + wordObjects.filter { it.name != firstWord.name }

                setContent {
                    LearnAppTheme {
                        LearnWordScreen(
                            words = allWords,
                            level = level,
                            userId = userId,
                            onBack = { finish() },
                            onUpdate = { updatedWord ->
                                updateWordProgress(db, userId, level, updatedWord)
                            }
                        )
                    }
                }
            }
    }

    private fun updateWordProgress(db: FirebaseFirestore, userId: String, level: String, word: Word) {
        db.collection("users")
            .document(userId)
            .collection(level)
            .document(word.name)
            .set(
                mapOf(
                    "name" to word.name,
                    "translation" to word.translation,
                    "pronunciation" to word.pronunciation,
                    "repetitions" to word.repetitions
                )
            )
            .addOnSuccessListener {
                println("Successfully updated word: ${word.name}")
            }
            .addOnFailureListener { e ->
                println("Failed to update word: ${word.name}, error: ${e.message}")
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnWordScreen(
    words: List<Word>,
    level: String,
    userId: String,
    onBack: () -> Unit,
    onUpdate: (Word) -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    var showTranslation by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }

    val currentWord = words.getOrNull(currentIndex)
    val animatedOffsetX by animateFloatAsState(targetValue = offsetX)
    val rotationAngle = (animatedOffsetX / 10).coerceIn(-30f, 30f)

    fun goToNextWord(updatedWord: Word) {
        onUpdate(updatedWord) // Обновляем Firestore
        offsetX = 0f
        currentIndex = (currentIndex + 1) % words.size // Переход к следующему слову
        showTranslation = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Learn new words", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF22252E))
            )
        },
        containerColor = Color(0xFF22252E)
    ) { paddingValues ->
        if (currentWord != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentWord.name,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "[${currentWord.pronunciation}]", // Отображение pronunciation
                    color = Color.Gray,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(200.dp)
                        .graphicsLayer(
                            translationX = animatedOffsetX,
                            rotationZ = rotationAngle
                        )
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    if (offsetX > 150) { // I Know
                                        val updatedRepetitions = (currentWord.repetitions + 1).coerceAtMost(4)
                                        goToNextWord(currentWord.copy(repetitions = updatedRepetitions))
                                    } else if (offsetX < -150) { // Learn
                                        val updatedRepetitions = maxOf(0, currentWord.repetitions - 1)
                                        goToNextWord(currentWord.copy(repetitions = updatedRepetitions))
                                    } else {
                                        offsetX = 0f
                                    }
                                },
                                onHorizontalDrag = { _, dragAmount -> offsetX += dragAmount }
                            )
                        }
                        .background(Color(0xFF313843), shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (showTranslation) {
                        Text(text = currentWord.translation, color = Color.White, fontSize = 24.sp)
                    } else {
                        IconButton(onClick = { showTranslation = true }) {
                            Icon(Icons.Default.Visibility, contentDescription = "Show Translation", tint = Color.Gray)
                        }
                    }
                }
            }
        } else {
            Text("No words available", color = Color.White, fontSize = 18.sp)
        }
    }
}
