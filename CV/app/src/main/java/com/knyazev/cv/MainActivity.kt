package com.knyazev.cv

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.knyazev.cv.ui.theme.CVTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CVTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF7F3FF) // фон
                ) {
                    ProfileScreen()
                }
            }
        }
    }
}

@Composable
fun SkillsSection(skills: List<String>) {
    Column {
        Text("Мои навыки:", style = MaterialTheme.typography.titleMedium)
        skills.forEach { skill ->
            Text("• $skill", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileImage()
        Spacer(modifier = Modifier.height(16.dp))
        NameBlock()
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            thickness = 4.dp,
            color = Color(0xFFB0B0B0)
        )
        Spacer(modifier = Modifier.height(8.dp))
        StudyInfo()
        Spacer(modifier = Modifier.height(16.dp))
        AndroidSkill()
        Spacer(modifier = Modifier.height(32.dp))
        SocialLinks()
    }
}

@Composable
fun ProfileImage() {
    // Замените R.drawable.profile на ваше изображение в drawable
    val image: Painter = painterResource(R.drawable.profile)
    Image(
        painter = image,
        contentDescription = null,
        modifier = Modifier
            .size(220.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(24.dp))
    )
}

@Composable
fun NameBlock() {
    Text(
        text = "Иванов Иван Иванович",
        color = Color(0xFF2EB4FF),
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
fun StudyInfo() {
    Text(
        text = "Я ⏳ учусь в МарГУ группе: 10А",
        color = Color.Black,
        fontSize = 20.sp,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center
    )
}

@Composable
fun AndroidSkill() {
    Text(
        text = "Я изучаю Android 📱",
        color = Color(0xFF00FF00),
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

@Composable
fun SocialLinks() {
    val context = LocalContext.current
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = {
            val intent = Intent(Intent.ACTION_VIEW, "https://vk.com/yourprofile".toUri())
            context.startActivity(intent)
        }) {
            Icon(
                painter = painterResource(R.drawable.ic_vk), // по желанию поменяте на свою иконку VK в drawable
                contentDescription = "VK",
                tint = Color.Unspecified,
                modifier = Modifier.size(32.dp)
            )
        }
        IconButton(onClick = {
            val intent = Intent(Intent.ACTION_VIEW, "https://t.me/yourprofile".toUri())
            context.startActivity(intent)
        }) {
            Icon(
                painter = painterResource(R.drawable.ic_telegram), // по желанию поменяте на  свою иконку Telegram в drawable
                contentDescription = "Telegram",
                tint = Color.Unspecified,
                modifier = Modifier.size(32.dp)
            )
        }
        // Добавьте другие соцсети по желанию
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CVTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF7F3FF) // фон
        ) {
            ProfileScreen()
        }
    }
}