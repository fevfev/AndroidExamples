package com.knyazev.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.knyazev.login.ui.theme.LoginTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Вход")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LoginTheme {
        LoginScreen()
    }
}

/*
1. `@Composable fun LoginScreen() { ... }`:**  Наша composable-функция `LoginScreen`.
2.  **`Column(...)`:**  Используем `Column` для вертикального расположения элементов.
    *   **`modifier = Modifier.fillMaxSize().padding(16.dp)`:**  Занимаем весь экран и добавляем отступы 16dp со всех сторон.
    *   **`horizontalAlignment = Alignment.CenterHorizontally`:**  Выравниваем элементы по центру по горизонтали.
    *   **`verticalArrangement = Arrangement.Center`:**  Выравниваем элементы по центру по вертикали.
3.  **`var username by remember { mutableStateOf("") }`** и **`var password by remember { mutableStateOf("") }`:**  Создаем *состояния* для хранения введенного имени пользователя и пароля.
    *   **`remember`:**  Функция, которая "запоминает" значение между рекомпозициями (обновлениями UI).  Без `remember` значение переменной сбрасывалось бы при каждом обновлении.
    *   **`mutableStateOf("")`:**  Создает изменяемое состояние (mutable state) с начальным значением "" (пустая строка).
    *   **`by`:**  Специальный синтаксис Kotlin (delegated properties), который позволяет обращаться к состоянию напрямую (как к обычной переменной `username`), а не через `.value`.
4.  **`OutlinedTextField(...)` (для имени пользователя):**
    *   **`value = username`:**  Текущее значение поля ввода (связано с состоянием `username`).
    *   **`onValueChange = { username = it }`:**  Обработчик изменения текста.  Когда пользователь вводит текст, вызывается этот код, и значение состояния `username` обновляется.  `it` – это новое значение текста.
    *   **`label = { Text("Username") }`:**  Подсказка (label) для поля ввода.
    *   **`modifier = Modifier.fillMaxWidth()`:**  Заставляет поле ввода занять всю доступную ширину.
5.  **`Spacer(modifier = Modifier.height(16.dp))`:**  Добавляет пустое пространство (разделитель) высотой 16dp.
6.  **`OutlinedTextField(...)` (для пароля):**  Аналогично полю для имени пользователя, но с дополнительными настройками:
    *   **`visualTransformation = PasswordVisualTransformation()`:**  Скрывает введенный пароль (отображает точки вместо символов).
    *   **`keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)`:**  Устанавливает тип клавиатуры (пароль).
7.  **`Spacer(modifier = Modifier.height(24.dp))`:**  Добавляет разделитель высотой 24dp.
8.  **`Button(...)`:**
    *   **`onClick = { }`:**  Обработчик нажатия кнопки.
    *   **`modifier = Modifier.fillMaxWidth()`:**  Заставляет кнопку занять всю доступную ширину.
    *   **`Text("Login")`:**  Текст кнопки.
* */