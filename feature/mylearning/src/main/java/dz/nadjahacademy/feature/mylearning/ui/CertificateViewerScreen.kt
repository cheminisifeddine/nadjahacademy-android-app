package dz.nadjahacademy.feature.mylearning.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.network.api.MyLearningApiService
import dz.nadjahacademy.core.network.model.Certificate
import dz.nadjahacademy.core.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ──────────────────────────────────────────
// ViewModel
// ──────────────────────────────────────────

data class CertificateUiState(
    val isLoading: Boolean = true,
    val certificate: Certificate? = null,
    val error: String? = null,
)

@HiltViewModel
class CertificateViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val myLearningApi: MyLearningApiService,
) : ViewModel() {

    private val certId: String = checkNotNull(savedStateHandle["certId"])

    private val _uiState = MutableStateFlow(CertificateUiState())
    val uiState: StateFlow<CertificateUiState> = _uiState

    init { loadCertificate() }

    private fun loadCertificate() {
        viewModelScope.launch {
            _uiState.value = CertificateUiState(isLoading = true)
            runCatching { myLearningApi.getCertificate(certId) }
                .onSuccess { cert -> _uiState.value = CertificateUiState(certificate = cert) }
                .onFailure { e -> _uiState.value = CertificateUiState(isLoading = false, error = e.message) }
        }
    }
}

// ──────────────────────────────────────────
// Screen
// ──────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CertificateViewerScreen(
    certId: String,
    onBack: () -> Unit,
    viewModel: CertificateViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Certificate") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* share intent */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = { /* download PDF */ }) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(color = NadjahCharcoal600)

                uiState.error != null -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text("Failed to load certificate", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { /* retry */ }) { Text("Retry") }
                }

                uiState.certificate != null -> CertificateCard(
                    certificate = uiState.certificate!!,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                )
            }
        }
    }
}

@Composable
private fun CertificateCard(
    certificate: Certificate,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(NadjahRed50, Color.White, NadjahGold50)
                )
            )
            .border(2.dp, NadjahRed200, RoundedCornerShape(16.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Header
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = NadjahCharcoal600,
                modifier = Modifier.size(64.dp),
            )

            Text(
                text = "Certificate of Completion",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NadjahCharcoal800,
                ),
                textAlign = TextAlign.Center,
            )

            // Divider
            HorizontalDivider(
                color = NadjahRed200,
                modifier = Modifier.fillMaxWidth(0.7f),
            )

            Text(
                text = "This certifies that",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = certificate.user_name ?: "Student",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = NadjahCharcoal900,
                ),
                textAlign = TextAlign.Center,
            )

            Text(
                text = "has successfully completed",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = certificate.course_title ?: "Course",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = NadjahCharcoal700,
                ),
                textAlign = TextAlign.Center,
            )

            HorizontalDivider(
                color = NadjahRed200,
                modifier = Modifier.fillMaxWidth(0.7f),
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Issue Date",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = certificate.issued_at.take(10),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Certificate ID",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = certificate.id.take(8).uppercase(),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    )
                }
            }
        }
    }
}
