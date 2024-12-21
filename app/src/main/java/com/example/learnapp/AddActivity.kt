package com.example.learnapp

import android.os.Bundle
import androidx.activity.ComponentActivity

class AddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirestoreHelper.addWordsToFirestore()
    }
}
