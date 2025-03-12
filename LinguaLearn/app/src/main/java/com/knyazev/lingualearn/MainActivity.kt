package com.knyazev.lingualearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.knyazev.lingualearn.model.Achievement
import com.knyazev.lingualearn.model.ExerciseType
import com.knyazev.lingualearn.model.Word
import com.knyazev.lingualearn.ui.theme.LinguaLearnTheme
import com.knyazev.lingualearn.viewmodel.QuizViewModel
import com.knyazev.lingualearn.viewmodel.WordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LinguaLearnTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyAppNavHost()
                }
            }
        }
    }
}

@Composable
fun MyAppNavHost() {
    val navController = rememberNavController()
    var showSplashScreen by remember { mutableStateOf(true) }

    NavHost(navController = navController, startDestination = if (showSplashScreen) "splash" else "wordList") {
        composable("splash") {
            SplashScreen(onAnimationFinished = {
                showSplashScreen = false
                navController.popBackStack()
                navController.navigate("wordList")
            })
        }
        composable(
            route = "wordList",
            enterTransition = { fadeIn(animationSpec = tween(500)) },
            exitTransition = { fadeOut(animationSpec = tween(500)) }
        ) {
            WordListScreen(
                onNavigateToExercise = { language, exerciseType ->
                    navController.navigate("exercise/$language/$exerciseType")
                },
                onNavigateToAllWords = {
                    navController.navigate("allWords")
                },
                onNavigateToQuiz = {
                    navController.navigate("quiz")
                }
            )
        }
        composable(
            route = "exercise/{language}/{exerciseType}",
            arguments = listOf(
                navArgument("language") { defaultValue = "en" },
                navArgument("exerciseType"){ defaultValue = ExerciseType.MULTIPLE_CHOICE.name}
            ),
            enterTransition = {fadeIn(animationSpec = tween(500))},
            exitTransition = { fadeOut(animationSpec = tween(500)) }

        ) { backStackEntry ->
            val language = backStackEntry.arguments?.getString("language") ?: "en"
            val exerciseTypeString = backStackEntry.arguments?.getString("exerciseType") ?: ExerciseType.MULTIPLE_CHOICE.name
            val exerciseType = ExerciseType.valueOf(exerciseTypeString)
            ExerciseScreen(language = language, exerciseType = exerciseType)
        }
        composable(
            route = "allWords"
        ){
            AllWordsScreen()
        }
        composable(
            route = "quiz"
        ){
            val quizViewModel = hiltViewModel<QuizViewModel>()
            val wordViewModel = hiltViewModel<WordViewModel>()
            val currentQuiz by wordViewModel.currentQuiz.collectAsState()
            currentQuiz?.let {
                QuizScreen(quiz = it, onQuizFinished = {
                    navController.popBackStack()
                })
            }
        }
    }
}

@Composable
fun WordListScreen(
    viewModel: WordViewModel = hiltViewModel(),
    onNavigateToExercise: (String, ExerciseType) -> Unit,
    onNavigateToAllWords: () -> Unit,
    onNavigateToQuiz: () -> Unit
) {
    val words by viewModel.words.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var newWordText by remember { mutableStateOf("") }
    var newWordTranslation by remember { mutableStateOf("") }
    var newImageUrl by remember { mutableStateOf("") }
    var newAudioUrl by remember { mutableStateOf("") }

    var selectedLanguage by remember { mutableStateOf("en")}
    val achievements by viewModel.achievements.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Word")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    selectedLanguage = "en"
                    viewModel.setLanguage(selectedLanguage)
                }) {
                    Text("English")
                }
                Button(onClick = {
                    selectedLanguage = "es"
                    viewModel.setLanguage(selectedLanguage)
                }) {
                    Text("Spanish")
                }
                Button(onClick = {
                    selectedLanguage = "fr"
                    viewModel.setLanguage(selectedLanguage)
                }) {
                    Text("French")
                }
            }

            LazyColumn {
                items(words) { word ->
                    WordCard(word = word, onLearnedChange = { isLearned ->
                        viewModel.updateLearnedStatus(word.id, isLearned)
                    },
                        onResetProgress = {wordId ->
                            viewModel.resetWordProgress(wordId)
                        })
                }
            }

            Button(onClick = { onNavigateToExercise(selectedLanguage, ExerciseType.MULTIPLE_CHOICE) }) {
                Text("Start Exercise (Multiple Choice)")
            }
            Button(onClick = { onNavigateToExercise(selectedLanguage, ExerciseType.TRANSLATION_INPUT)
            }) {
                Text("Start Exercise (Input)")
            }
            Button(onClick = { onNavigateToExercise(selectedLanguage, ExerciseType.AUDITION)
            }) {
                Text("Start Exercise (Audition)")
            }
            Button(onClick = {
                onNavigateToExercise(selectedLanguage, ExerciseType.SENTENCE_BUILDING)
            }) {
                Text("Start Exercise (Sentence Building)")
            }
            Button(onClick = { onNavigateToExercise(selectedLanguage, ExerciseType.WORD_SCRAMBLE)
            }) {
                Text("Start Exercise (Word Scramble)")
            }
            Button(onClick = {
                onNavigateToExercise(selectedLanguage, ExerciseType.MATCHING)
            }) {
                Text("Start Exercise (Matching)")
            }
            Button(onClick = onNavigateToAllWords) {
                Text("All Words")
            }
            Button(onClick = {
                viewModel.startQuiz(selectedLanguage)
                onNavigateToQuiz()
            }) {
                Text("Start Quiz")
            }
            Text(text = "Achievements", style = MaterialTheme.typography.titleLarge)

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(achievements) { achievement ->
                    AchievementItem(achievement = achievement)
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Word") },
            text = {
                Column {
                    TextField(
                        value = newWordText,
                        onValueChange = { newWordText = it },
                        label = { Text("Word") }
                    )
                    TextField(
                        value = newWordTranslation,
                        onValueChange = { newWordTranslation = it },
                        label = { Text("Translation") }
                    )
                    TextField(
                        value = newImageUrl,
                        onValueChange = { newImageUrl = it},
                        label = { Text("Image URL (optional)")}
                    )
                    TextField(
                        value = newAudioUrl,
                        onValueChange = { newAudioUrl = it},
                        label = { Text("Audio URL (optional)")}
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newWordText.isNotBlank() && newWordTranslation.isNotBlank()) {
                            viewModel.insertWord(
                                Word(
                                    word = newWordText,
                                    translation = newWordTranslation,
                                    language = selectedLanguage,
                                    imageUrl = newImageUrl.ifBlank { null },
                                    audioUrl = newAudioUrl.ifBlank { null }
                                )
                            )
                            newWordText = ""
                            newWordTranslation = ""
                            newImageUrl = ""
                            newAudioUrl = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun WordCard(word: Word, onLearnedChange: (Boolean) -> Unit, onResetProgress: (Long) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = word.word, style = MaterialTheme.typography.titleLarge)
                    Text(text = word.translation, style = MaterialTheme.typography.bodyMedium)
                }
                if (word.exampleSentence != null) {
                    Text(
                        text = "Example: ${word.exampleSentence}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = word.learned,
                        onCheckedChange = onLearnedChange,
                    )
                    Text(text = "Learned")
                    IconButton(onClick = { onResetProgress(word.id) }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Progress",
                            tint = Color.Gray
                        )
                    }
                }
            }

        }
    }
}
@Composable
fun AchievementItem(achievement: Achievement) {
    Card(
        modifier = Modifier
            .size(100.dp)
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) Color.Green else Color.Gray,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = achievement.name,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            if(!achievement.isUnlocked){
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun AllWordsScreen(viewModel: WordViewModel = hiltViewModel()) {
    val words by viewModel.words.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showLearnedOnly by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("en")}

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon"
                )
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Show learned only")
            Switch(
                checked = showLearnedOnly,
                onCheckedChange = { showLearnedOnly = it },
                thumbContent = if (showLearnedOnly) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else {
                    null
                }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                selectedLanguage = "en"
                viewModel.setLanguage(selectedLanguage)
            }) {
                Text("English")
            }
            Button(onClick = {
                selectedLanguage = "es"
                viewModel.setLanguage(selectedLanguage)
            }) {
                Text("Spanish")
            }
            Button(onClick = {
                selectedLanguage = "fr"
                viewModel.setLanguage(selectedLanguage)
            }) {
                Text("French")
            }
        }

        LazyColumn {
            items(
                words.filter { word ->
                    (word.word.contains(searchQuery, ignoreCase = true) ||
                            word.translation.contains(searchQuery, ignoreCase = true))
                            && (!showLearnedOnly || word.learned)
                }
            ) { word ->
                WordCard(word = word, onLearnedChange = { isLearned ->
                    viewModel.updateLearnedStatus(word.id, isLearned)
                },
                    onResetProgress = {wordId ->
                        viewModel.resetWordProgress(wordId)
                    })
            }
        }
    }
}