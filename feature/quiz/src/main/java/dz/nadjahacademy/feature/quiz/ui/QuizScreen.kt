package dz.nadjahacademy.feature.quiz.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.quiz.viewmodel.QuizViewModel
import kotlinx.coroutines.delay

@Composable
fun QuizScreen(
    quizId: String,
    onBack: () -> Unit,
    onViewResults: (String) -> Unit,
    viewModel: QuizViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.attemptId) {
        if (uiState.attemptId != null && uiState.quiz?.time_limit != null) {
            while (uiState.result == null) {
                delay(1000)
                viewModel.onTimerTick()
            }
        }
    }

    LaunchedEffect(uiState.result) {
        uiState.result?.let { onViewResults(it.attempt_id) }
    }

    when {
        uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        uiState.quiz == null -> ErrorState(message = uiState.error ?: "Quiz Not Found", onRetry = {}, modifier = Modifier.fillMaxSize())
        uiState.attemptId == null -> QuizIntroScreen(quiz = uiState.quiz!!, onStart = viewModel::startQuiz, onBack = onBack)
        uiState.isSubmitting -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Submitting quiz...", style = MaterialTheme.typography.bodyLarge)
            }
        }
        else -> {
            val quiz = uiState.quiz!!
            val questions = quiz.questions ?: emptyList()
            val currentQ = questions.getOrNull(uiState.currentQuestionIndex)

            Column(modifier = Modifier.fillMaxSize()) {
                Surface(shadowElevation = 2.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp).statusBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Filled.Close, contentDescription = "Exit")
                        }
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Question ${uiState.currentQuestionIndex + 1} of ${questions.size}", style = MaterialTheme.typography.labelMedium)
                            LinearProgressIndicator(
                                progress = { (uiState.currentQuestionIndex + 1f) / questions.size },
                                modifier = Modifier.fillMaxWidth(0.8f).height(6.dp).padding(top = 4.dp),
                            )
                        }
                        if (uiState.timeRemainingSeconds > 0) {
                            val min = uiState.timeRemainingSeconds / 60
                            val sec = uiState.timeRemainingSeconds % 60
                            Surface(
                                color = if (uiState.timeRemainingSeconds < 60) MaterialTheme.colorScheme.errorContainer else NadjahRed100,
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text(
                                    "$min:${sec.toString().padStart(2, '0')}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (uiState.timeRemainingSeconds < 60) MaterialTheme.colorScheme.error else NadjahCharcoal600,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }

                if (currentQ != null) {
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                    ) {
                        Text(currentQ.question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(24.dp))

                        when (currentQ.type) {
                            "fill_blank" -> {
                                val answer = uiState.textAnswers[currentQ.id] ?: ""
                                OutlinedTextField(
                                    value = answer,
                                    onValueChange = { viewModel.setTextAnswer(currentQ.id, it) },
                                    label = { Text("Your answer") },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            else -> {
                                currentQ.options?.forEach { option ->
                                    val selected = uiState.selectedAnswers[currentQ.id] == option.id
                                    AnswerOption(
                                        text = option.option_text,
                                        selected = selected,
                                        onClick = { viewModel.selectAnswer(currentQ.id, option.id) },
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    Surface(shadowElevation = 4.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            if (uiState.currentQuestionIndex > 0) {
                                NadjahOutlinedButton(
                                    text = "Prev",
                                    onClick = viewModel::previousQuestion,
                                    modifier = Modifier.weight(0.5f),
                                )
                            }
                            if (uiState.currentQuestionIndex < questions.size - 1) {
                                NadjahButton(
                                    text = "Next",
                                    onClick = viewModel::nextQuestion,
                                    modifier = Modifier.weight(1f),
                                )
                            } else {
                                NadjahButton(
                                    text = "Submit Quiz",
                                    onClick = viewModel::submitQuiz,
                                    modifier = Modifier.weight(1f),
                                    loading = uiState.isSubmitting,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizIntroScreen(quiz: dz.nadjahacademy.core.network.model.QuizDetail, onStart: () -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(96.dp).background(NadjahRed100, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Quiz, contentDescription = null, tint = NadjahRed600, modifier = Modifier.size(52.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text(quiz.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(quiz.description ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                QuizInfoRow(Icons.Outlined.Quiz, "Questions", "${quiz.questions.size} questions")
                if (quiz.time_limit != null) {
                    QuizInfoRow(Icons.Outlined.AccessTime, "Time Limit", "${quiz.time_limit} minutes")
                }
                QuizInfoRow(Icons.Outlined.Star, "Passing Score", "${quiz.pass_percentage}%")
                if (quiz.max_attempts != null) {
                    QuizInfoRow(Icons.Outlined.Repeat, "Attempts", "Max ${quiz.max_attempts} attempts")
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        NadjahButton(text = "Start Quiz", onClick = onStart, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        NadjahTextButton(text = "Back", onClick = onBack)
    }
}

@Composable
private fun QuizInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icon, contentDescription = null, tint = NadjahRed600, modifier = Modifier.size(20.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun AnswerOption(text: String, selected: Boolean, onClick: () -> Unit) {
    val containerColor = if (selected) NadjahRed50 else MaterialTheme.colorScheme.surface
    val borderColor = if (selected) NadjahRed600 else MaterialTheme.colorScheme.outline
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected, onClick = onClick)
            Spacer(Modifier.width(8.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
