package com.knyazev.notesvault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.knyazev.notesvault.data.SettingsManager
import com.knyazev.notesvault.ui.theme.NotesVaultTheme

class MainActivity : ComponentActivity() {

    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsManager = SettingsManager(this)
        settingsManager.applyCurrentTheme() // Применяем сохраненную тему при старте

        setContent {
            val isDarkTheme = settingsManager.getThemeMode() == SettingsManager.THEME_DARK
            var themeSwitcherState by remember { mutableStateOf(isDarkTheme) } // Состояние переключателя

            // Устанавливаем тему для всего приложения
            NotesVaultTheme(darkTheme = themeSwitcherState) { // Используем состояние для темы Compose
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Настройки темы", style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Темная тема")
                            Spacer(modifier = Modifier.width(10.dp))
                            // Переключатель для смены темы
                            Switch(
                                checked = themeSwitcherState,
                                onCheckedChange = { isChecked ->
                                    themeSwitcherState = isChecked
                                    settingsManager.setThemeMode(
                                        if (isChecked) SettingsManager.THEME_DARK else SettingsManager.THEME_LIGHT
                                    )
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(text = "Смена темы работает!", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}