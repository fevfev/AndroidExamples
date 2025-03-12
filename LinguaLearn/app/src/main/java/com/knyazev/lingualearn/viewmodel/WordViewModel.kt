package com.knyazev.lingualearn.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knyazev.lingualearn.model.Achievement
import com.knyazev.lingualearn.model.Quiz
import com.knyazev.lingualearn.model.UserProgress
import com.knyazev.lingualearn.model.Word
import com.knyazev.lingualearn.notification.NotificationHelper
import com.knyazev.lingualearn.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordViewModel @Inject constructor(
    private val repository: WordRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _selectedLanguage = MutableStateFlow("en")

    @OptIn(ExperimentalCoroutinesApi::class)
    val words: StateFlow<List<Word>> = _selectedLanguage
        .flatMapLatest { language ->
            repository.getWordsByLanguage(language)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private val _userProgress = MutableStateFlow(UserProgress("user1", "lesson1", emptyList(), 0, 1))
    val userProgress: StateFlow<UserProgress> = _userProgress.asStateFlow()

    private val _currentQuiz = MutableStateFlow<Quiz?>(null)
    val currentQuiz: StateFlow<Quiz?> = _currentQuiz.asStateFlow()

    init {
        loadWords("en")
        //  initial data
        _achievements.value = listOf(
            Achievement("learn_10_words", "First Steps", "Learn 10 words", 10),
            Achievement("reach_level_5", "Level Up!", "Reach level 5", 5),
            Achievement("score_100_points", "Point Collector", "Score 100 points", 100),
        )

        repository.getLearnedWords(_selectedLanguage.value).onEach {
            checkAchievements(it.size)
        }.launchIn(viewModelScope)

        userProgress.onEach {
            checkAchievements(it.level, it.points)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            if (repository.canClaimDailyBonus()) {
                notificationHelper.showDailyBonusNotification()
            }
        }
    }

    private fun checkAchievements(learnedWordsCount: Int = 0, level: Int = 0, points: Int = 0) {
        _achievements.update { currentAchievements ->
            currentAchievements.map { achievement ->
                if (!achievement.isUnlocked) {
                    val unlocked = when (achievement.id) {
                        "learn_10_words" -> learnedWordsCount >= achievement.goal
                        "reach_level_5" -> level >= achievement.goal
                        "score_100_points" -> points >= achievement.goal
                        else -> false
                    }
                    if (unlocked) {
                        Log.d("WordViewModel", "Achievement unlocked: ${achievement.name}")
                    }
                    achievement.copy(isUnlocked = unlocked)
                } else {
                    achievement
                }
            }
        }
    }

    fun insertWord(word: Word) {
        viewModelScope.launch {
            repository.insertWord(word.copy(nextReviewTimestamp = System.currentTimeMillis()))
        }
    }

    fun updateWord(word: Word) {
        viewModelScope.launch {
            repository.updateWord(word)
        }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch {
            repository.deleteWord(word)
        }
    }

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
        loadWords(language)
    }

    fun updateLearnedStatus(wordId: Long, isLearned: Boolean){
        viewModelScope.launch {
            repository.updateLearnedStatus(wordId, isLearned)
        }
    }
    fun loadWords(language: String) {
        viewModelScope.launch {
            repository.refreshWords(language)
        }
    }

    fun resetWordProgress(wordId: Long) {
        viewModelScope.launch {
            repository.resetWordProgress(wordId)
        }
    }

    fun addPoints(points: Int){
        _userProgress.update {
            it.copy(points = it.points + points)
        }
        checkLevelUp()
    }

    private fun checkLevelUp() {
        val currentPoints = _userProgress.value.points
        val nextLevel = _userProgress.value.level + 1
        val requiredPoints = nextLevel * 100

        if (currentPoints >= requiredPoints) {
            _userProgress.update {
                it.copy(level = nextLevel, points = currentPoints - requiredPoints)
            }
        }
    }

    fun startQuiz(language: String) {
        // _currentQuiz.value = quiz
    }
}