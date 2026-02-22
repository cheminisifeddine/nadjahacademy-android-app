package dz.nadjahacademy.feature.quiz.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.quiz.viewmodel.QuizResultViewModel

@Composable
fun QuizResultScreen(
    attemptId: String,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    viewModel: QuizResultViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        uiState.result != null -> {
            val result = uiState.result!!
            val passed = result.passed
            Column(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(
                            Brush.verticalGradient(
                                if (passed) listOf(NadjahGreen600, NadjahGold600)
                                else listOf(NadjahError600, NadjahGold700)
                            )
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (passed) Icons.Filled.EmojiEvents else Icons.Outlined.SentimentDissatisfied,
                            contentDescription = null,
                            tint = NadjahWhite,
                            modifier = Modifier.size(72.dp),
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            if (passed) "Congratulations!" else "Keep Trying!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = NadjahWhite,
                        )
                        Text(
                            if (passed) "You passed the quiz" else "You didn't pass this time",
                            style = MaterialTheme.typography.bodyLarge,
                            color = NadjahWhite.copy(alpha = 0.9f),
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "${result.percentage.toInt()}%",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = NadjahWhite,
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    NadjahButton(text = "Back to Course", onClick = onBack, modifier = Modifier.fillMaxWidth())
                    if (!passed) {
                        NadjahOutlinedButton(text = "Retry Quiz", onClick = onRetry, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
        else -> {
            Column(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Brush.verticalGradient(listOf(NadjahCharcoal600, NadjahGold600))),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = NadjahWhite,
                            modifier = Modifier.size(72.dp),
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Quiz Complete",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = NadjahWhite,
                        )
                        Text(
                            "Attempt: $attemptId",
                            style = MaterialTheme.typography.bodyLarge,
                            color = NadjahWhite.copy(alpha = 0.9f),
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    NadjahButton(text = "Back to Course", onClick = onBack, modifier = Modifier.fillMaxWidth())
                    NadjahOutlinedButton(text = "Retry Quiz", onClick = onRetry, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun ResultStatCard(
    label: String,
    value: String,
    bgColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = textColor)
            Text(label, style = MaterialTheme.typography.bodySmall, color = textColor.copy(alpha = 0.8f))
        }
    }
}
