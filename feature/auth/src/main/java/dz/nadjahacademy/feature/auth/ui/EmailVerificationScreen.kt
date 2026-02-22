package dz.nadjahacademy.feature.auth.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun EmailVerificationScreen(
    onVerified: () -> Unit,
    onSkip: () -> Unit,
    email: String = "",
    onNavigateToLogin: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var cooldown by remember { mutableIntStateOf(0) }
    var resendDone by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) { if (uiState.isSuccess) { resendDone = true; viewModel.resetState() } }
    LaunchedEffect(cooldown) { if (cooldown > 0) { delay(1000); cooldown-- } }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(modifier = Modifier.size(120.dp).background(NadjahRed100, RoundedCornerShape(30.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Email, null, tint = NadjahRed600, modifier = Modifier.size(64.dp))
        }
        Spacer(Modifier.height(32.dp))
        Text("Verify Your Email", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text("We sent a verification link to", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Text(email, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = NadjahRed600, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))

        AnimatedVisibility(visible = uiState.error != null) {
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), shape = RoundedCornerShape(12.dp)) {
                Text(uiState.error ?: "", color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall)
            }
        }
        AnimatedVisibility(visible = resendDone) {
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = NadjahGreen100), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, null, tint = NadjahGreen600, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp))
                    Text("Verification email resent!", color = NadjahGreen600, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        NadjahButton(text = "I've Verified My Email", onClick = onVerified, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        NadjahOutlinedButton(
            text = if (cooldown > 0) "Resend in ${cooldown}s" else "Resend Verification Email",
            onClick = { if (cooldown == 0) { viewModel.resendVerification(); cooldown = 60; resendDone = false } },
            modifier = Modifier.fillMaxWidth(),
            enabled = cooldown == 0 && !uiState.isLoading,
        )
        Spacer(Modifier.height(12.dp))
        NadjahTextButton(text = "Back to Sign In", onClick = onNavigateToLogin)
    }
}
