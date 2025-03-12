package com.knyazev.lingualearn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knyazev.lingualearn.model.Quiz
import com.knyazev.lingualearn.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(private val quizRepository: QuizRepository) : ViewModel() {

    private val _currentQuiz = MutableStateFlow<Quiz?>(null)
    val currentQuiz: StateFlow<Quiz?> = _currentQuiz.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _userAnswers = MutableStateFlow<List<String>>(emptyList())
    val userAnswers: StateFlow<List<String>> = _userAnswers.asStateFlow()

    private val _quizCompleted = MutableStateFlow(false)
    val quizCompleted: StateFlow<Boolean> = _quizCompleted.asStateFlow()

    fun startQuiz(quiz: Quiz) {
        _currentQuiz.value = quiz
        _currentQuestionIndex.value = 0
        _userAnswers.value = emptyList()
        _quizCompleted.value = false
    }

    fun submitAnswer(answer: String) {
        _userAnswers.update { currentAnswers ->
            currentAnswers + answer
        }

        if (_currentQuestionIndex.value < (_currentQuiz.value?.questions?.size ?: 0) - 1) {
            _currentQuestionIndex.value++
        } else {
            _quizCompleted.value = true
        }
    }
    fun calculateScore(): Int {
        var score = 0
        val quiz = _currentQuiz.value ?: return 0
        val questions = quiz.questions
        for (i in questions.indices) {
            if (i < _userAnswers.value.size && _userAnswers.value[i] == questions[i].correctAnswer) {
                score++
            }
        }
        return score
    }

    fun resetQuiz() {
        _currentQuiz.value = null
        _currentQuestionIndex.value = 0
        _userAnswers.value = emptyList()
        _quizCompleted.value = false
    }

    fun loadQuizzes(language: String) {
        viewModelScope.launch {
            val quizzes = quizRepository.getQuizzes(language)
            if (quizzes.isNotEmpty()) {
                _currentQuiz.value = quizzes.first() //  берем первый попавшийся квиз
            }
        }
    }
}