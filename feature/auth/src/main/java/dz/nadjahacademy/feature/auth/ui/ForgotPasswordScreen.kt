package dz.nadjahacademy.feature.auth.ui

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
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.auth.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onEmailSent: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var emailSent by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) { emailSent = true; viewModel.resetState() }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth()) { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }
        Spacer(Modifier.height(24.dp))

        if (emailSent) {
            Box(modifier = Modifier.size(96.dp).background(NadjahGreen100, RoundedCornerShape(24.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.MarkEmailRead, null, tint = NadjahGreen600, modifier = Modifier.size(52.dp))
            }
            Spacer(Modifier.height(24.dp))
            Text("Check Your Email", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("We've sent a reset link to\n$email", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))
            NadjahButton(text = "Back to Sign In", onClick = onEmailSent, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Didn't receive it?", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                NadjahTextButton(text = "Resend", onClick = { emailSent = false; viewModel.forgotPassword(email) })
            }
        } else {
            Box(modifier = Modifier.size(96.dp).background(NadjahRed100, RoundedCornerShape(24.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.LockReset, null, tint = NadjahRed600, modifier = Modifier.size(52.dp))
            }
            Spacer(Modifier.height(24.dp))
            Text("Forgot Password?", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("Enter your email and we'll send you a reset link.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))

            AnimatedVisibility(visible = uiState.error != null) {
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(12.dp)) { Icon(Icons.Filled.Error, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text(uiState.error ?: "", color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodySmall) }
                }
            }

            NadjahTextField(value = email, onValueChange = { email = it }, label = "Email Address", modifier = Modifier.fillMaxWidth(), leadingIcon = Icons.Outlined.Email, keyboardType = KeyboardType.Email, imeAction = ImeAction.Done, isError = uiState.fieldErrors.containsKey("email"), errorMessage = uiState.fieldErrors["email"], onDone = { viewModel.forgotPassword(email) })
            Spacer(Modifier.height(24.dp))
            NadjahButton(text = "Send Reset Link", onClick = { viewModel.forgotPassword(email) }, modifier = Modifier.fillMaxWidth(), loading = uiState.isLoading)
            Spacer(Modifier.height(16.dp))
            NadjahTextButton(text = "Back to Sign In", onClick = onBack)
        }
        Spacer(Modifier.height(24.dp))
    }
}
