package com.knyazev.lingualearn

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knyazev.lingualearn.model.Question
import com.knyazev.lingualearn.model.Quiz
import com.knyazev.lingualearn.viewmodel.QuizViewModel

@Composable
fun QuizScreen(quiz: Quiz, onQuizFinished: (Int) -> Unit, viewModel: QuizViewModel = hiltViewModel()) {

    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val quizCompleted by viewModel.quizCompleted.collectAsState()
    val currentQuestion = quiz.questions.getOrNull(currentQuestionIndex)

    LaunchedEffect(quiz) {
        viewModel.startQuiz(quiz)
    }

    if (quizCompleted) {
        val score = viewModel.calculateScore()
        QuizResultScreen(score = score, totalQuestions = quiz.questions.size, onRestart = {
            viewModel.resetQuiz()
            viewModel.startQuiz(quiz)
        }, onFinish = {
            viewModel.resetQuiz()
            onQuizFinished(score)
        }
        )
    } else if (currentQuestion != null) {
        QuestionScreen(
            question = currentQuestion,
            onAnswerSelected = { answer ->
                viewModel.submitAnswer(answer)
            }
        )
    } else {
        Text("No questions available")
    }
}

@Composable
fun QuestionScreen(question: Question, onAnswerSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = question.text, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(question.options) { option ->
                Button(
                    onClick = { onAnswerSelected(option) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(option)
                }
            }
        }
    }
}

@Composable
fun QuizResultScreen(score: Int, totalQuestions: Int, onRestart: () -> Unit, onFinish: ()-> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Quiz Completed!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Your Score: $score / $totalQuestions", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onRestart) {
            Text("Restart Quiz")
        }
        Button(onClick = onFinish) {
            Text("Finish")
        }
    }
}