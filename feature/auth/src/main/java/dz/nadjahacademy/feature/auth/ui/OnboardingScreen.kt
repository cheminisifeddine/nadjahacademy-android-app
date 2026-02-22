package dz.nadjahacademy.feature.auth.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val gradient: List<Color>,
)

val onboardingPages = listOf(
    OnboardingPage(
        icon = Icons.Filled.School,
        title = "Learn from Experts",
        subtitle = "Access hundreds of courses taught by Algeria's best instructors and industry professionals.",
        gradient = listOf(NadjahCharcoal700, NadjahRed600),
    ),
    OnboardingPage(
        icon = Icons.Filled.PlayCircle,
        title = "Learn Anywhere",
        subtitle = "Watch lessons on your phone, tablet, or desktop. Download for offline access.",
        gradient = listOf(NadjahGold900, NadjahGold400),
    ),
    OnboardingPage(
        icon = Icons.Filled.EmojiEvents,
        title = "Track Your Progress",
        subtitle = "Stay motivated with learning streaks, achievements, and verified certificates.",
        gradient = listOf(NadjahGold900, NadjahGold400),
    ),
    OnboardingPage(
        icon = Icons.Filled.Groups,
        title = "Join a Community",
        subtitle = "Discuss with classmates, get answers from instructors, and grow your network.",
        gradient = listOf(Color(0xFF7C3AED), Color(0xFF9333EA)),
    ),
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
) {
    val pagerState = rememberPagerState { onboardingPages.size + 1 } // +1 for interests page
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            if (page < onboardingPages.size) {
                OnboardingInfoPage(page = onboardingPages[page])
            } else {
                OnboardingInterestsPage(onGetStarted = onFinished)
            }
        }

        // Skip button
        if (pagerState.currentPage < onboardingPages.size) {
            TextButton(
                onClick = onFinished,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
            ) {
                Text("Skip", color = NadjahWhite.copy(alpha = 0.8f), style = MaterialTheme.typography.labelLarge)
            }
        }

        // Bottom navigation
        if (pagerState.currentPage < onboardingPages.size) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Page indicators
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(onboardingPages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(if (isSelected) 24.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) NadjahWhite else NadjahWhite.copy(alpha = 0.4f))
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NadjahWhite, contentColor = NadjahRed600),
                ) {
                    Text(
                        text = if (pagerState.currentPage == onboardingPages.size - 1) "Get Started" else "Next",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingInfoPage(page: OnboardingPage) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(page.gradient)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(NadjahWhite.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = NadjahWhite,
                    modifier = Modifier.size(72.dp),
                )
            }
            Spacer(Modifier.height(48.dp))
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                color = NadjahWhite,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = page.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = NadjahWhite.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
            )
        }
    }
}

@Composable
fun OnboardingInterestsPage(onGetStarted: () -> Unit) {
    val categories = listOf(
        "Programming" to Icons.Filled.Code,
        "Design" to Icons.Filled.Palette,
        "Business" to Icons.Filled.Business,
        "Marketing" to Icons.Filled.Campaign,
        "Languages" to Icons.Filled.Language,
        "Finance" to Icons.Filled.AttachMoney,
        "Personal Dev" to Icons.Filled.SelfImprovement,
        "Science" to Icons.Filled.Science,
    )
    val selected = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))
        Text(
            text = "What do you want to learn?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Select your interests to get personalized recommendations",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))

        // Category chips grid
        val chunked = categories.chunked(2)
        chunked.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                row.forEach { (label, icon) ->
                    val isSelected = selected.contains(label)
                    FilterChip(
                        modifier = Modifier.weight(1f).height(56.dp),
                        selected = isSelected,
                        onClick = {
                            if (isSelected) selected.remove(label)
                            else if (selected.size < 5) selected.add(label)
                        },
                        label = { Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium) },
                        leadingIcon = {
                            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        ),
                    )
                }
                if (row.size < 2) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(32.dp))
        NadjahButton(
            text = if (selected.isEmpty()) "Skip" else "Continue (${selected.size}/5)",
            onClick = onGetStarted,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(16.dp))
    }
}
