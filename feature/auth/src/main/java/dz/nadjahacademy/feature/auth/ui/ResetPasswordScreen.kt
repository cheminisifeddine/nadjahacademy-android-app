package dz.nadjahacademy.feature.auth.ui

import androidx.compose.animation.AnimatedVisibility
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
fun ResetPasswordScreen(
    token: String,
    onResetSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) { if (uiState.isSuccess) onResetSuccess() }

    val strength = remember(newPassword) { when { newPassword.length < 6 -> 0; newPassword.length < 8 -> 1; newPassword.length >= 8 && newPassword.any { it.isDigit() } && newPassword.any { it.isLetter() } -> 3; else -> 2 } }
    val strengthColor = when (strength) { 0 -> MaterialTheme.colorScheme.error; 1 -> NadjahGold700; 2 -> NadjahGold600; else -> NadjahGreen500 }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(48.dp))
        Box(modifier = Modifier.size(96.dp).background(NadjahRed100, RoundedCornerShape(24.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.Lock, null, tint = NadjahRed600, modifier = Modifier.size(52.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text("Reset Password", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Create a new strong password.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))

        AnimatedVisibility(visible = uiState.error != null) {
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Error, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp))
                    Text(uiState.error ?: "", color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    IconButton(onClick = viewModel::clearError, modifier = Modifier.size(24.dp)) { Icon(Icons.Filled.Close, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onErrorContainer) }
                }
            }
        }

        NadjahTextField(value = newPassword, onValueChange = { newPassword = it }, label = "New Password", modifier = Modifier.fillMaxWidth(), leadingIcon = Icons.Outlined.Lock, keyboardType = KeyboardType.Password, imeAction = ImeAction.Next, isError = uiState.fieldErrors.containsKey("password"), errorMessage = uiState.fieldErrors["password"], visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) { Icon(if (newPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, null) } })

        if (newPassword.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                repeat(4) { i -> Box(modifier = Modifier.weight(1f).height(4.dp).padding(end = if (i < 3) 4.dp else 0.dp).background(if (i < strength) strengthColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(2.dp))) }
                Spacer(Modifier.width(8.dp))
                Text(when (strength) { 0 -> "Too short"; 1 -> "Weak"; 2 -> "Fair"; else -> "Strong" }, style = MaterialTheme.typography.labelSmall, color = strengthColor)
            }
        }
        Spacer(Modifier.height(16.dp))
        NadjahTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = "Confirm Password", modifier = Modifier.fillMaxWidth(), leadingIcon = Icons.Outlined.Lock, keyboardType = KeyboardType.Password, imeAction = ImeAction.Done, isError = confirmPassword.isNotEmpty() && confirmPassword != newPassword, errorMessage = if (confirmPassword.isNotEmpty() && confirmPassword != newPassword) "Passwords do not match" else null, visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(if (confirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, null) } }, onDone = { viewModel.resetPassword(token, newPassword, confirmPassword) })

        Spacer(Modifier.height(24.dp))
        NadjahButton(text = "Reset Password", onClick = { viewModel.resetPassword(token, newPassword, confirmPassword) }, modifier = Modifier.fillMaxWidth(), loading = uiState.isLoading)
        Spacer(Modifier.height(16.dp))
        NadjahTextButton(text = "Back to Sign In", onClick = onNavigateToLogin)
        Spacer(Modifier.height(24.dp))
    }
}
