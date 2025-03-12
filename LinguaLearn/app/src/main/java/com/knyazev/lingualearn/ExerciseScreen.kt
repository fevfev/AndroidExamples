package com.knyazev.lingualearn

import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.knyazev.lingualearn.model.DifficultyLevel
import com.knyazev.lingualearn.model.ExerciseType
import com.knyazev.lingualearn.model.Word
import com.knyazev.lingualearn.repository.WordRepository
import com.knyazev.lingualearn.viewmodel.ExerciseViewModel
import com.knyazev.lingualearn.viewmodel.WordViewModel
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
    private val repository: WordRepository,
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
                repository.updateRepetitionLevel(_currentWord.value!!.id, false) // Помечаем как не правильно отвеченное
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

@Composable
fun ExerciseScreen(
    language: String,
    exerciseType: ExerciseType,
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    val currentWord by viewModel.currentWord.collectAsState()
    val options by viewModel.options.collectAsState()
    val userAnswer by viewModel.userAnswer.collectAsState()
    val isAnswerCorrect by viewModel.isAnswerCorrect.collectAsState()
    val showLottie by viewModel.showLottieAnimation.collectAsState()
    val wordsToReview by viewModel.repository.getWordsToReview(language, viewModel.difficultyLevel.value).collectAsState(initial = emptyList())
    val currentWordIndex = wordsToReview.indexOf(currentWord)
    val progress = if (wordsToReview.isNotEmpty()) {
        (currentWordIndex + 1).toFloat() / wordsToReview.size.toFloat()
    } else {
        0f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500), label = ""
    )

    LaunchedEffect(language, exerciseType) {
        viewModel.startExercise(language, exerciseType)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Выбор сложности
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { viewModel.setDifficultyLevel(DifficultyLevel.EASY) }) {
                Text("Easy")
            }
            Button(onClick = { viewModel.setDifficultyLevel(DifficultyLevel.MEDIUM) }) {
                Text("Medium")
            }
            Button(onClick = { viewModel.setDifficultyLevel(DifficultyLevel.HARD) }) {
                Text("Hard")
            }
        }
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth()
        )
        if (currentWord != null) {
            Text(text = "Translate: ${currentWord?.word}", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            when (exerciseType) {
                ExerciseType.MULTIPLE_CHOICE -> {
                    options.forEach { option ->
                        Button(
                            onClick = { viewModel.submitAnswer(option) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(option)
                        }
                    }
                }
                ExerciseType.TRANSLATION_INPUT ->{
                    var inputText by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = { Text("Translation") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(onClick = {viewModel.submitAnswer(inputText)}) {
                        Text(text = "Submit")
                    }
                }
                ExerciseType.AUDITION -> {
                    AuditionExercise(
                        word = currentWord!!,
                        onPlayAudio = { viewModel.playAudio() },
                        onSubmitAnswer = { answer -> viewModel.submitAnswer(answer) }
                    )
                }
                ExerciseType.SENTENCE_BUILDING ->{
                    SentenceBuildingExercise(
                        word = currentWord!!,
                        onSubmitAnswer = {answer -> viewModel.submitAnswer(answer)}
                    )
                }
                ExerciseType.WORD_SCRAMBLE -> {
                    WordScrambleExercise(
                        word = currentWord!!,
                        onSubmitAnswer = { answer -> viewModel.submitAnswer(answer) }
                    )
                }
                ExerciseType.MATCHING -> {
                    MatchingExercise(
                        words = wordsToReview,
                        onSubmitAnswer = { viewModel.submitAnswer(if(it) "true" else "false") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isAnswerCorrect != null) {
                Text(
                    text = if (isAnswerCorrect == true) "Correct!" else "Incorrect. Correct answer: ${currentWord?.translation}",
                    color = if (isAnswerCorrect == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Button(onClick = { viewModel.nextWord() }) {
                    Text(text = "Next Word")
                }
            }
            else{
                Button(onClick = { viewModel.nextWord() }) {
                    Text(text = "Skip")
                }
            }
            if (showLottie && isAnswerCorrect == true) {
                CorrectAnswerAnimation()
            }

        } else {
            Text("Loading...")
        }
    }
}

@Composable
fun AuditionExercise(word: Word, onPlayAudio: () -> Unit, onSubmitAnswer: (String) -> Unit) {
    var userAnswer by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onPlayAudio,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Play audio",
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Play Audio")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = userAnswer,
            onValueChange = { userAnswer = it },
            label = { Text("Your translation") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onSubmitAnswer(userAnswer) },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Submit")
        }
    }
}

@Composable
fun SentenceBuildingExercise(word: Word, onSubmitAnswer: (String) -> Unit) {
    val sentence = word.exampleSentence ?: return

    val shuffledWords = remember { sentence.split(" ").shuffled() }
    var selectedWords by remember { mutableStateOf(listOf<String>()) }
    var currentSentence by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Build the sentence:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            shuffledWords.forEach { word ->
                if(!selectedWords.contains(word)){
                    Button(onClick = {
                        selectedWords = selectedWords + word
                        currentSentence += "$word "
                    }) {
                        Text(text = word)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = currentSentence, style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            onSubmitAnswer(currentSentence.trim())
        }) {
            Text("Submit")
        }
        Button(onClick = {
            selectedWords = emptyList()
            currentSentence = ""
        }) {
            Text(text = "Reset")
        }
    }
}
@Composable
fun WordScrambleExercise(word: Word, onSubmitAnswer: (String) -> Unit) {
    val originalWord = remember { word.word }
    val shuffledWord = remember { word.word.toCharArray().toList().shuffled().joinToString("") }
    var userAnswer by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Unscramble the word:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = shuffledWord, style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userAnswer,
            onValueChange = { userAnswer = it },
            label = { Text("Your answer") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onSubmitAnswer(userAnswer) }) {
            Text("Submit")
        }
    }
}
@Composable
fun MatchingExercise(words: List<Word>, onSubmitAnswer: (Boolean) -> Unit) {

    val shuffledWords = remember { words.shuffled() }
    val shuffledTranslations = remember { words.map { it.translation }.shuffled() }

    var selectedWordIndex by remember { mutableStateOf<Int?>(null) }
    var selectedTranslationIndex by remember { mutableStateOf<Int?>(null) }
    var matchedPairs by remember { mutableStateOf(setOf<Pair<Int, Int>>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Match the words with their translations:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column {
                shuffledWords.forEachIndexed { index, word ->
                    val isMatched = matchedPairs.any { it.first == index }
                    val isSelected = selectedWordIndex == index
                    Button(
                        onClick = {
                            if (!isMatched) {
                                if (selectedWordIndex == null) {
                                    selectedWordIndex = index
                                } else {
                                    selectedWordIndex = null
                                    selectedTranslationIndex = null
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                isMatched -> Color.Green
                                isSelected -> Color.Blue
                                else -> MaterialTheme.colorScheme.primary
                            }
                        ),
                        enabled = !isMatched
                    ) {
                        Text(word.word)
                    }
                }
            }

            Column {
                shuffledTranslations.forEachIndexed { index, translation ->
                    val isMatched = matchedPairs.any { it.second == index }
                    val isSelected = selectedTranslationIndex == index
                    Button(
                        onClick = {
                            if (!isMatched) {
                                if (selectedTranslationIndex == null) {
                                    selectedTranslationIndex = index
                                } else {
                                    selectedTranslationIndex = null
                                    selectedWordIndex = null
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                isMatched -> Color.Green
                                isSelected -> Color.Blue
                                else -> MaterialTheme.colorScheme.primary
                            }
                        ),
                        enabled = !isMatched
                    ) {
                        Text(translation)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedWordIndex != null && selectedTranslationIndex != null) {
            val word = shuffledWords[selectedWordIndex!!]
            val translation = shuffledTranslations[selectedTranslation
            if (word.translation == translation) {
                matchedPairs = matchedPairs + (selectedWordIndex!! to selectedTranslationIndex!!)
                selectedWordIndex = null
                selectedTranslationIndex = null

                if(matchedPairs.size == words.size){
                    onSubmitAnswer(true)
                }

            } else {
                selectedWordIndex = null
                selectedTranslationIndex = null
            }
        }
    }
}

@Composable
fun CorrectAnswerAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.correct_animation))
    val progress by animateLottieCompositionAsState(composition)

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(200.dp)
    )
}