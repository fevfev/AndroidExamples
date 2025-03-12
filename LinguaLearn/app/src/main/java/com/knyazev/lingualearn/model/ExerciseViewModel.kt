package com.knyazev.lingualearn.model

import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay
import com.knyazev.lingualearn.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.media.MediaPlayer
import java.io.IOException
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import com.knyazev.lingualearn.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(private val repository: WordRepository) : ViewModel() {

    // ... (предыдущий код) ...

    private val _showLottieAnimation = MutableStateFlow(false) // Флаг для отображения Lottie
    val showLottieAnimation: StateFlow<Boolean> = _showLottieAnimation.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null //Для аудио

    fun startExercise(language: String, exerciseType: ExerciseType) {
        _exerciseType.value = exerciseType
        viewModelScope.launch {
            repository.getWordsByLanguage(language).collect { words ->
                if (words.isNotEmpty()) {
                    //Выбираем тип упражнения
                    if(exerciseType == ExerciseType.AUDITION){
                        val word = words.filter { it.audioUrl != null }.randomOrNull() //Выбираем только те, у которых есть аудио
                        _currentWord.value = word
                    }
                    else{
                        _currentWord.value = words.random()
                    }
                    if(exerciseType == ExerciseType.MULTIPLE_CHOICE || exerciseType == ExerciseType.TRANSLATION_INPUT) {
                        generateOptions(words) // Генерируем варианты ответов, если нужен
                    }
                }
            }
        }
    }

    fun submitAnswer(answer: String) {
        _userAnswer.value = answer
        val isCorrect = answer == _currentWord.value?.translation
        _isAnswerCorrect.value = isCorrect
        if(isCorrect) { //Если правильно
            viewModelScope.launch {
                _showLottieAnimation.value = true // Показываем анимацию
                delay(1500) // Ждем 1.5 секунды
                _showLottieAnimation.value = false // Скрываем
                nextWord() // След. слово
            }
        }
        else{
            nextWord()
        }
    }
    fun playAudio() {
        val audioUrl = _currentWord.value?.audioUrl ?: return

        viewModelScope.launch(Dispatchers.IO) { // Use Dispatchers.IO for network operations
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(audioUrl) //Указываем источник
                        prepare() //Подготавливаем
                        start() //Запускаем
                    }
                } else {
                    mediaPlayer?.stop() //Останавливаем, если уже играет
                    mediaPlayer?.reset() //Сбрасываем
                    mediaPlayer?.setDataSource(audioUrl) //Указываем источник
                    mediaPlayer?.prepare() //Подготавливаем
                    mediaPlayer?.start()//Запускаем
                }

            } catch (e: IOException) {
                Log.e("ExerciseViewModel", "Error playing audio: ${e.message}")
                // Handle the error appropriately, e.g., show a message to the user
            }
        }
    }

    fun releaseMediaPlayer() { //Освобождаем
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        releaseMediaPlayer() // Не забываем освобождать ресурсы
    }
    // ... (остальные методы) ...

}

// Добавляем новый тип упражнения
enum class ExerciseType {
    MULTIPLE_CHOICE,
    TRANSLATION_INPUT,
    AUDITION, // Аудирование
    SENTENCE_BUILDING //Составление предложения
}