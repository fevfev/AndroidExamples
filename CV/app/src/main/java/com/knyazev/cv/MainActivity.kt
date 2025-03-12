package com.knyazev.cv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.knyazev.cv.ui.theme.CVTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CVTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CVCard()
                }
            }
        }
    }
}

@Composable
fun CVCard() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorResource(R.color.light_blue),
                        colorResource(R.color.light_gray)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Аватар
            Image(
                painter = painterResource(R.drawable.avatar), // Замените на ваше изображение
                contentDescription = stringResource(R.string.profile_image),
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .border(width = 4.dp, color = colorResource(R.color.colorPrimary), shape = CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ФИО
            Text(
                text = stringResource(R.string.name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.colorPrimary)
                )
            )

            // Должность/специальность
            Text(
                text = stringResource(R.string.title),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = colorResource(R.color.white)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Карточка с контактной информацией
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    ContactInfoRow(
                        iconRes = R.drawable.ic_phone,
                        text = stringResource(R.string.phone)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ContactInfoRow(
                        iconRes = R.drawable.ic_email,
                        text = stringResource(R.string.email)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ContactInfoRow(
                        iconRes = R.drawable.ic_website,
                        text = stringResource(R.string.website)
                    )
                }
            }
        }
    }
}

@Composable
fun ContactInfoRow(iconRes: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = colorResource(R.color.colorPrimary),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CVTheme {
        CVCard()
    }
}
