package com.example.learnapp

import com.google.firebase.firestore.FirebaseFirestore

object FirestoreHelper {
    private val firestore = FirebaseFirestore.getInstance()

    fun addWordsToFirestore() {
        // Слова для уровней с переводами и произношением
        val levels = mapOf(
            "A1" to listOf(
                mapOf("name" to "mother", "translation" to "мать", "pronunciation" to "[ˈmʌðər]"),
                mapOf("name" to "day", "translation" to "день", "pronunciation" to "[deɪ]"),
                mapOf("name" to "you", "translation" to "ты", "pronunciation" to "[juː]"),
                mapOf("name" to "get", "translation" to "получать", "pronunciation" to "[ɡɛt]"),
                mapOf("name" to "put", "translation" to "положить", "pronunciation" to "[pʊt]"),
                mapOf("name" to "race", "translation" to "гонка", "pronunciation" to "[reɪs]"),
                mapOf("name" to "start", "translation" to "начинать", "pronunciation" to "[stɑːrt]"),
                mapOf("name" to "finish", "translation" to "заканчивать", "pronunciation" to "[ˈfɪnɪʃ]"),
                mapOf("name" to "design", "translation" to "дизайн", "pronunciation" to "[dɪˈzaɪn]")
            ),
            "A2" to listOf(
                mapOf("name" to "create", "translation" to "создавать", "pronunciation" to "[kriˈeɪt]"),
                mapOf("name" to "develop", "translation" to "развивать", "pronunciation" to "[dɪˈvɛləp]"),
                mapOf("name" to "imagine", "translation" to "воображать", "pronunciation" to "[ɪˈmædʒɪn]"),
                mapOf("name" to "improve", "translation" to "улучшать", "pronunciation" to "[ɪmˈpruːv]"),
                mapOf("name" to "build", "translation" to "строить", "pronunciation" to "[bɪld]"),
                mapOf("name" to "analyze", "translation" to "анализировать", "pronunciation" to "[ˈænəˌlaɪz]"),
                mapOf("name" to "test", "translation" to "тестировать", "pronunciation" to "[tɛst]"),
                mapOf("name" to "fix", "translation" to "исправлять", "pronunciation" to "[fɪks]"),
                mapOf("name" to "research", "translation" to "исследовать", "pronunciation" to "[rɪˈsɜːrtʃ]"),
                mapOf("name" to "plan", "translation" to "планировать", "pronunciation" to "[plæn]")
            ),

                "B1" to listOf(
                    mapOf("name" to "performance", "translation" to "производительность", "pronunciation" to "[pərˈfɔːrməns]"),
                    mapOf("name" to "interaction", "translation" to "взаимодействие", "pronunciation" to "[ˌɪntərˈækʃən]"),
                    mapOf("name" to "achievement", "translation" to "достижение", "pronunciation" to "[əˈtʃiːvmənt]"),
                    mapOf("name" to "growth", "translation" to "рост", "pronunciation" to "[ɡroʊθ]"),
                    mapOf("name" to "feedback", "translation" to "обратная связь", "pronunciation" to "[ˈfiːdbæk]"),
                    mapOf("name" to "quality", "translation" to "качество", "pronunciation" to "[ˈkwɑːləti]"),
                    mapOf("name" to "vision", "translation" to "видение", "pronunciation" to "[ˈvɪʒən]"),
                    mapOf("name" to "strategy", "translation" to "стратегия", "pronunciation" to "[ˈstrætədʒi]"),
                    mapOf("name" to "initiative", "translation" to "инициатива", "pronunciation" to "[ɪˈnɪʃətɪv]"),
                    mapOf("name" to "collaboration", "translation" to "сотрудничество", "pronunciation" to "[kəˌlæbəˈreɪʃən]")
                ),
                "B2" to listOf(
                    mapOf("name" to "critical", "translation" to "критический", "pronunciation" to "[ˈkrɪtɪkəl]"),
                    mapOf("name" to "evaluation", "translation" to "оценка", "pronunciation" to "[ɪˌvæljuˈeɪʃən]"),
                    mapOf("name" to "productivity", "translation" to "продуктивность", "pronunciation" to "[ˌprɒdʌkˈtɪvɪti]"),
                    mapOf("name" to "optimization", "translation" to "оптимизация", "pronunciation" to "[ˌɒptɪmaɪˈzeɪʃən]"),
                    mapOf("name" to "implementation", "translation" to "реализация", "pronunciation" to "[ˌɪmplɪmenˈteɪʃən]"),
                    mapOf("name" to "alignment", "translation" to "согласование", "pronunciation" to "[əˈlaɪnmənt]"),
                    mapOf("name" to "coordination", "translation" to "координация", "pronunciation" to "[koʊˌɔːrdɪˈneɪʃən]"),
                    mapOf("name" to "leadership", "translation" to "лидерство", "pronunciation" to "[ˈliːdərʃɪp]"),
                    mapOf("name" to "resilience", "translation" to "устойчивость", "pronunciation" to "[rɪˈzɪliəns]"),
                    mapOf("name" to "consistency", "translation" to "последовательность", "pronunciation" to "[kənˈsɪstənsi]")
                ),
                "C1" to listOf(
                    mapOf("name" to "perspective", "translation" to "перспектива", "pronunciation" to "[pərˈspɛktɪv]"),
                    mapOf("name" to "clarity", "translation" to "ясность", "pronunciation" to "[ˈklærɪti]"),
                    mapOf("name" to "articulation", "translation" to "чёткое выражение", "pronunciation" to "[ɑːˌtɪkjuˈleɪʃən]"),
                    mapOf("name" to "proficiency", "translation" to "умение", "pronunciation" to "[prəˈfɪʃənsi]"),
                    mapOf("name" to "synthesis", "translation" to "синтез", "pronunciation" to "[ˈsɪnθəsɪs]"),
                    mapOf("name" to "validation", "translation" to "утверждение", "pronunciation" to "[ˌvælɪˈdeɪʃən]"),
                    mapOf("name" to "interpretation", "translation" to "интерпретация", "pronunciation" to "[ɪnˌtɜːrprɪˈteɪʃən]"),
                    mapOf("name" to "innovation", "translation" to "инновация", "pronunciation" to "[ˌɪnəˈveɪʃən]"),
                    mapOf("name" to "creativity", "translation" to "креативность", "pronunciation" to "[ˌkriːeɪˈtɪvɪti]"),
                    mapOf("name" to "adaptability", "translation" to "адаптивность", "pronunciation" to "[əˌdæptəˈbɪlɪti]")
                ),
                "C2" to listOf(
                    mapOf("name" to "persuasion", "translation" to "убеждение", "pronunciation" to "[pərˈsweɪʒən]"),
                    mapOf("name" to "negotiation", "translation" to "переговоры", "pronunciation" to "[nɪˌɡoʊʃiˈeɪʃən]"),
                    mapOf("name" to "diplomacy", "translation" to "дипломатия", "pronunciation" to "[dɪˈploʊməsi]"),
                    mapOf("name" to "sophistication", "translation" to "утончённость", "pronunciation" to "[səˌfɪstɪˈkeɪʃən]"),
                    mapOf("name" to "advocacy", "translation" to "защита интересов", "pronunciation" to "[ˈædvəkəsi]"),
                    mapOf("name" to "eloquence", "translation" to "красноречие", "pronunciation" to "[ˈɛləkwəns]"),
                    mapOf("name" to "ingenuity", "translation" to "изобретательность", "pronunciation" to "[ˌɪndʒəˈnjuːəti]"),
                    mapOf("name" to "mastery", "translation" to "мастерство", "pronunciation" to "[ˈmæstəri]"),
                    mapOf("name" to "brilliance", "translation" to "блестящая идея", "pronunciation" to "[ˈbrɪljəns]"),
                    mapOf("name" to "exceptionality", "translation" to "исключительность", "pronunciation" to "[ɪkˌsɛpʃəˈnæləti]")
                )
            )

        // Добавление слов с переводами и произношением в Firestore
        levels.forEach { (level, words) ->
            firestore.collection("words").document(level)
                .set(mapOf("words" to words))
                .addOnSuccessListener {
                    println("Words for $level added successfully!")
                }
                .addOnFailureListener { e ->
                    println("Error adding words for $level: ${e.message}")
                }
        }
    }
}
