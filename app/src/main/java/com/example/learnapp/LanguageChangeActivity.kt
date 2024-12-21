package com.example.learnapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learnapp.ui.theme.LearnAppTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class LanguageChangeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LearnAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LanguageSelectionScreen()
                }
            }
        }
    }
}

@Composable
fun LanguageSelectionScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF14161B))
            .padding(16.dp)
            .statusBarsPadding(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(
                onClick = { /* Handle back action, e.g., finish() */ },
                modifier = Modifier
                    .background(Color(0xFF333333), shape = RoundedCornerShape(8.dp))
                    .size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Text(
                fontSize = 24.sp,
                text = "Какой язык ты хочешь изучать?",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }

        LanguageOption("English", R.drawable.english) {
            openLanguageScreen(context, "English")
        }
//        Spacer(modifier = Modifier.height(16.dp))
//        LanguageOption("Spain", R.drawable.spain) {
//            openLanguageScreen(context, "Spain")
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        LanguageOption("French", R.drawable.french) {
//            openLanguageScreen(context, "French")
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        LanguageOption("Russian", R.drawable.russian) {
//            openLanguageScreen(context, "Russian")
//        }
    }
}

fun openLanguageScreen(context: Context, language: String) {
    val intent = Intent(context, LanguageDetailActivity::class.java).apply {
        putExtra("SELECTED_LANGUAGE", language)
    }
    context.startActivity(intent)
}

@Composable
fun LanguageOption(language: String, flagIcon: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF14161B), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = flagIcon),
            contentDescription = "$language Flag",
            tint = Color.Unspecified,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = language,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Preview(showBackground = true)
@Composable
fun Prev() {
    LearnAppTheme {
        LanguageSelectionScreen()
    }
}
