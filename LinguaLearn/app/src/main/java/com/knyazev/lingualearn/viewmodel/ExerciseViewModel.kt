package com.knyazev.lingualearn.viewmodel

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.compose.*
import com.knyazev.lingualearn.model.DifficultyLevel
import com.knyazev.lingualearn.model.ExerciseType
import com.knyazev.lingualearn.model.Word
import com.knyazev.lingualearn.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    val repository: WordRepository,
    private val wordViewModel: WordViewModel
) : ViewModel() {

    private val _currentWord = MutableStateFlow<Word?>(null)
    val currentWord: StateFlow<Word?> = _currentWord.asStateFlow()

    private val _options = MutableStateFlow<List<String>>(emptyList())
    val options: StateFlow<List<String>> = _options.asStateFlow()

    private val _userAnswer = MutableStateFlow<String?>(null)
    val userAnswer: StateFlow<String?> = _userAnswer.asStateFlow()

    private val _isAnswerCorrect = MutableStateFlow<Boolean?>(null)
    val isAnswerCorrect: StateFlow<Boolean?> = _isAnswerCorrect.asStateFlow()

    private val _exerciseType = MutableStateFlow<ExerciseType>(ExerciseType.MULTIPLE_CHOICE)
    val exerciseType: StateFlow<ExerciseType> = _exerciseType.asStateFlow()

    private val _showLottieAnimation = MutableStateFlow(false)
    val showLottieAnimation: StateFlow<Boolean> = _showLottieAnimation.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    private val _difficultyLevel = MutableStateFlow(DifficultyLevel.MEDIUM)
    val difficultyLevel: StateFlow<DifficultyLevel> = _difficultyLevel.asStateFlow()

    fun setDifficultyLevel(level: DifficultyLevel) {
        _difficultyLevel.value = level
    }

    fun startExercise(language: String, exerciseType: ExerciseType) {
        _exerciseType.value = exerciseType
        viewModelScope.launch {
            val wordsFlow =  when(exerciseType){
                ExerciseType.MATCHING -> repository.getWordsByLanguage(language)
                else -> repository.getWordsToReview(language, _difficultyLevel.value)
            }
            wordsFlow.collect { words ->
                if (words.isNotEmpty()) {
                    if(exerciseType == ExerciseType.AUDITION){
                        val word = words.filter { it.audioUrl != null }.randomOrNull()
                        _currentWord.value = word
                    }
                    else{
                        _currentWord.value = words.random()
                    }
                    if(exerciseType == ExerciseType.MULTIPLE_CHOICE || exerciseType == ExerciseType.TRANSLATION_INPUT) {
                        generateOptions(words)
                    }
                }
            }
        }
    }

    fun submitAnswer(answer: String) {
        _userAnswer.value = answer
        val isCorrect = when(_exerciseType.value){
            ExerciseType.MULTIPLE_CHOICE, ExerciseType.TRANSLATION_INPUT, ExerciseType.AUDITION -> {
                answer == _currentWord.value?.translation
            }
            ExerciseType.WORD_SCRAMBLE -> {
                answer == _currentWord.value?.word
            }
            ExerciseType.SENTENCE_BUILDING -> {
                answer == _currentWord.value?.exampleSentence
            }
            ExerciseType.MATCHING ->{
                answer.toBoolean()
            }
        }
        _isAnswerCorrect.value = isCorrect

        viewModelScope.launch {
            if (isCorrect) {
                repository.updateRepetitionLevel(_currentWord.value!!.id, true)
                _showLottieAnimation.value = true
                delay(1500)
                _showLottieAnimation.value = false
                wordViewModel.addPoints(10)
            } else {
                repository.updateRepetitionLevel(_currentWord.value!!.id, false)
            }
            nextWord()
        }
    }

    private fun generateOptions(words: List<Word>) {
        val correctTranslation = _currentWord.value?.translation ?: return
        val incorrectOptions = words.filter { it.id != _currentWord.value?.id }
            .map { it.translation }
            .shuffled()
            .take(3)
        _options.value = (incorrectOptions + correctTranslation).shuffled()
    }

    fun playAudio() {
        val audioUrl = _currentWord.value?.audioUrl ?: return

        viewModelScope.launch(Dispatchers.IO) { // Use Dispatchers.IO for network operations
            try {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(audioUrl)
                        prepare()
                        start()
                    }
                } else {
                    mediaPlayer?.stop()
                    mediaPlayer?.reset()
                    mediaPlayer?.setDataSource(audioUrl)
                    mediaPlayer?.prepare()
                    mediaPlayer?.start()
                }

            } catch (e: IOException) {
                Log.e("ExerciseViewModel", "Error playing audio: ${e.message}")
                // Handle the error appropriately, e.g., show a message to the user
            }
        }
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        releaseMediaPlayer()
    }
    fun nextWord(){
        _userAnswer.value = null
        _isAnswerCorrect.value = null
        _currentWord.value = null

        viewModelScope.launch {
            val wordsFlow =  when(_exerciseType.value){
                ExerciseType.MATCHING -> repository.getWordsByLanguage(_selectedLanguage.value)
                else -> repository.getWordsToReview(_selectedLanguage.value, _difficultyLevel.value)
            }
            wordsFlow.collect { words ->
                if (words.isNotEmpty()) {
                    if(_exerciseType.value == ExerciseType.AUDITION){
                        val word = words.filter { it.audioUrl != null }.randomOrNull()
                        _currentWord.value = word
                    }
                    else{
                        _currentWord.value = words.random()
                    }
                    if(_exerciseType.value == ExerciseType.MULTIPLE_CHOICE || _exerciseType.value == ExerciseType.TRANSLATION_INPUT) {
                        generateOptions(words)
                    }

                }
            }
        }
    }
    private val _selectedLanguage = MutableStateFlow("en")
}