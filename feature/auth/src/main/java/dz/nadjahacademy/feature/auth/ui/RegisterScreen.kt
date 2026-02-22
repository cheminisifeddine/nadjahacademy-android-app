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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dz.nadjahacademy.core.ui.components.*
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.auth.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) { if (uiState.isSuccess) onRegisterSuccess() }

    val passwordStrength = remember(password) {
        when {
            password.length < 6 -> 0
            password.length < 8 -> 1
            password.length >= 8 && password.any { it.isDigit() } && password.any { it.isLetter() } -> 3
            else -> 2
        }
    }
    val strengthColor = when (passwordStrength) { 0 -> MaterialTheme.colorScheme.error; 1 -> NadjahGold700; 2 -> NadjahGold600; else -> NadjahGreen500 }
    val strengthLabel = when (passwordStrength) { 0 -> "Too short"; 1 -> "Weak"; 2 -> "Fair"; else -> "Strong" }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))
        Box(modifier = Modifier.size(80.dp).background(Brush.linearGradient(listOf(NadjahRed600, NadjahGold600)), RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.School, contentDescription = null, tint = NadjahWhite, modifier = Modifier.size(44.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text("Create Account", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Join thousands of learners today", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))

        AnimatedVisibility(visible = uiState.error != null) {
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Error, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(uiState.error ?: "", color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    IconButton(onClick = viewModel::clearError, modifier = Modifier.size(24.dp)) { Icon(Icons.Filled.Close, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onErrorContainer) }
                }
            }
        }

        NadjahTextField(value = fullName, onValueChange = { fullName = it }, label = "Full Name", modifier = Modifier.fillMaxWidth(), leadingIcon = Icons.Outlined.Person, imeAction = ImeAction.Next, isError = uiState.fieldErrors.containsKey("fullName"), errorMessage = uiState.fieldErrors["fullName"])
        Spacer(Modifier.height(16.dp))
        NadjahTextField(value = email, onValueChange = { email = it }, label = "Email Address", modifier = Modifier.fillMaxWidth(), leadingIcon = Icons.Outlined.Email, keyboardType = KeyboardType.Email, imeAction = ImeAction.Next, isError = uiState.fieldErrors.containsKey("email"), errorMessage = uiState.fieldErrors["email"])
        Spacer(Modifier.height(16.dp))
        NadjahTextField(value = phone, onValueChange = { phone = it }, label = "Phone (Optional)", modifier = Modifier.fillMaxWidth(), leadingIcon = Icons.Outlined.Phone, keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next, isError = false)
        Spacer(Modifier.height(16.dp))
        NadjahTextField(value = password, onValueChange = { password = it }, label = "Password", modifier = Modifier.fillMaxWidth(), leadingIcon = Icons.Outlined.Lock, keyboardType = KeyboardType.Password, imeAction = ImeAction.Next, isError = uiState.fieldErrors.containsKey("password"), errorMessage = uiState.fieldErrors["password"], visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { passwordVisible = !passwordVisible }) { Icon(if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, null) } })

        if (password.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                repeat(4) { index -> Box(modifier = Modifier.weight(1f).height(4.dp).padding(end = if (index < 3) 4.dp else 0.dp).background(if (index < passwordStrength) strengthColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(2.dp))) }
                Spacer(Modifier.width(8.dp))
                Text(strengthLabel, style = MaterialTheme.typography.labelSmall, color = strengthColor)
            }
        }

        Spacer(Modifier.height(16.dp))
        NadjahTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = "Confirm Password", modifier = Modifier.fillMaxWidth(), leadingIcon = Icons.Outlined.Lock, keyboardType = KeyboardType.Password, imeAction = ImeAction.Done, isError = confirmPassword.isNotEmpty() && confirmPassword != password, errorMessage = if (confirmPassword.isNotEmpty() && confirmPassword != password) "Passwords do not match" else null, visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) { Icon(if (confirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, null) } }, onDone = { if (termsAccepted) viewModel.register(email, password, fullName, phone.ifBlank { null }) })

        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = termsAccepted, onCheckedChange = { termsAccepted = it })
            Spacer(Modifier.width(4.dp))
            Text("I agree to the ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Terms of Service", style = MaterialTheme.typography.bodySmall, color = NadjahRed600, modifier = Modifier.clickable {})
            Text(" and ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Privacy Policy", style = MaterialTheme.typography.bodySmall, color = NadjahRed600, modifier = Modifier.clickable {})
        }

        Spacer(Modifier.height(24.dp))
        NadjahButton(text = "Create Account", onClick = { viewModel.register(email, password, fullName, phone.ifBlank { null }) }, modifier = Modifier.fillMaxWidth(), loading = uiState.isLoading, enabled = termsAccepted)
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text("Already have an account?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            NadjahTextButton(text = "Sign In", onClick = onNavigateToLogin)
        }
        Spacer(Modifier.height(24.dp))
    }
}
