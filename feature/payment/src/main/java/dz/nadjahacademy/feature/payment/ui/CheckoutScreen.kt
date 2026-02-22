package dz.nadjahacademy.feature.payment.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import dz.nadjahacademy.core.network.model.CourseDetail
import dz.nadjahacademy.core.ui.theme.*
import dz.nadjahacademy.feature.payment.viewmodel.PaymentViewModel

// ─── Constants ───────────────────────────────────────────────────────────────

/** Replace with your actual WhatsApp business number — digits only, no '+'. */
private const val WHATSAPP_NUMBER = "213XXXXXXXXX"

private val PrimaryRed = Color(0xFFC62828)
private val PrimaryRedDark = Color(0xFF8E0000)
private val PrimaryRedLight = Color(0xFFFFEBEE)
private val AccentGold = Color(0xFFF9A825)
private val AccentGoldLight = Color(0xFFFFF8E1)
private val WhatsAppGreen = Color(0xFF25D366)

private val ALGERIAN_WILAYAS = listOf(
    "01 - الجزائر",
    "02 - الشلف",
    "03 - الأغواط",
    "04 - أم البواقي",
    "05 - باتنة",
    "06 - بجاية",
    "07 - بسكرة",
    "08 - بشار",
    "09 - البليدة",
    "10 - البويرة",
    "11 - تمنراست",
    "12 - تبسة",
    "13 - تلمسان",
    "14 - تيارت",
    "15 - تيزي وزو",
    "16 - الجزائر العاصمة",
    "17 - الجلفة",
    "18 - جيجل",
    "19 - سطيف",
    "20 - سعيدة",
    "21 - سكيكدة",
    "22 - سيدي بلعباس",
    "23 - عنابة",
    "24 - قالمة",
    "25 - قسنطينة",
    "26 - المدية",
    "27 - مستغانم",
    "28 - المسيلة",
    "29 - معسكر",
    "30 - ورقلة",
    "31 - وهران",
    "32 - البيض",
    "33 - إليزي",
    "34 - برج بوعريريج",
    "35 - بومرداس",
    "36 - الطارف",
    "37 - تندوف",
    "38 - تسمسيلت",
    "39 - الوادي",
    "40 - خنشلة",
    "41 - سوق أهراس",
    "42 - تيبازة",
    "43 - ميلة",
    "44 - عين الدفلى",
    "45 - النعامة",
    "46 - عين تموشنت",
    "47 - غرداية",
    "48 - رليزان",
    "49 - تيميمون",
    "50 - برج باجي مختار",
    "51 - أولاد جلال",
    "52 - بني عباس",
    "53 - عين صالح",
    "54 - عين قزام",
    "55 - تقرت",
    "56 - جانت",
    "57 - المغير",
    "58 - المنيعة",
)

// ─── Screen ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    courseIds: String,
    onBack: () -> Unit,
    onSuccess: (String) -> Unit,
    viewModel: PaymentViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Trigger course load from the composable-level parameter
    LaunchedEffect(courseIds) {
        viewModel.loadCourses(courseIds)
    }

    // Navigate on successful order placement
    LaunchedEffect(uiState.paymentSuccess) {
        if (uiState.paymentSuccess) uiState.orderId?.let { onSuccess(it) }
    }

    // Local validation error state — each field clears its own error on edit
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var wilayaError by remember { mutableStateOf<String?>(null) }

    fun validateForm(): Boolean {
        val phoneRegex = Regex("^(05|06|07)[0-9]{8}$")
        fullNameError = if (uiState.fullName.trim().length < 3)
            "الرجاء إدخال الاسم الكامل (٣ أحرف على الأقل)" else null
        phoneError = if (!phoneRegex.matches(uiState.phone.trim()))
            "رقم الهاتف غير صحيح — الصيغة: 05/06/07 XXXXXXXX" else null
        wilayaError = if (uiState.wilaya.isBlank()) "الرجاء اختيار الولاية" else null
        return fullNameError == null && phoneError == null && wilayaError == null
    }

    fun openWhatsApp() {
        val course = uiState.course
        val price = course?.price?.let { String.format("%.0f", it) } ?: "—"
        val title = course?.title_ar ?: course?.title ?: "الدورة"
        val message = buildString {
            appendLine("مرحبا، أريد الاشتراك في الدورة: $title - $price دج")
            appendLine("الاسم: ${uiState.fullName.ifBlank { "—" }}")
            appendLine("الهاتف: ${uiState.phone.ifBlank { "—" }}")
            append("الولاية: ${uiState.wilaya.ifBlank { "—" }}")
        }
        val encoded = Uri.encode(message)
        val waUri = Uri.parse("https://wa.me/$WHATSAPP_NUMBER?text=$encoded")
        val intent = Intent(Intent.ACTION_VIEW, waUri).apply { setPackage("com.whatsapp") }
        runCatching { context.startActivity(intent) }.onFailure {
            // WhatsApp not installed — fall back to browser / web.whatsapp.com
            context.startActivity(Intent(Intent.ACTION_VIEW, waUri))
        }
    }

    // Force RTL for the entire screen (Arabic-first content)
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "إتمام الطلب",
                            fontWeight = FontWeight.ExtraBold,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "رجوع",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrimaryRed,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                    ),
                )
            },
            bottomBar = {
                BottomActionBar(
                    course = uiState.course,
                    isProcessing = uiState.isProcessing,
                    onWhatsApp = { openWhatsApp() },
                    onConfirm = { if (validateForm()) viewModel.checkout() },
                )
            },
        ) { contentPadding ->
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = PrimaryRed)
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {

                        // ── Error banner ─────────────────────────────────────
                        if (uiState.error != null) {
                            ErrorBanner(message = uiState.error!!)
                        }

                        // ── COD-only badge ───────────────────────────────────
                        CodBadge()

                        // ── Order summary ────────────────────────────────────
                        CheckoutSectionHeader(label = "ملخص الطلب", icon = Icons.Filled.ShoppingCart)
                        uiState.course?.let { course ->
                            OrderSummaryCard(course = course)
                        }

                        // ── Contact / delivery form ──────────────────────────
                        CheckoutSectionHeader(label = "معلومات التسليم", icon = Icons.Filled.Person)

                        // Full name
                        OutlinedTextField(
                            value = uiState.fullName,
                            onValueChange = {
                                fullNameError = null
                                viewModel.setFullName(it)
                            },
                            label = { Text("الاسم الكامل") },
                            placeholder = { Text("مثال: أحمد بن علي") },
                            leadingIcon = {
                                Icon(Icons.Filled.Person, contentDescription = null, tint = PrimaryRed)
                            },
                            isError = fullNameError != null,
                            supportingText = {
                                fullNameError?.let { err ->
                                    Text(err, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                        )

                        // Phone number
                        OutlinedTextField(
                            value = uiState.phone,
                            onValueChange = {
                                phoneError = null
                                viewModel.setPhone(it)
                            },
                            label = { Text("رقم الهاتف") },
                            placeholder = { Text("0612345678") },
                            leadingIcon = {
                                Icon(Icons.Filled.Phone, contentDescription = null, tint = PrimaryRed)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            isError = phoneError != null,
                            supportingText = {
                                if (phoneError != null) {
                                    Text(phoneError!!, color = MaterialTheme.colorScheme.error)
                                } else {
                                    Text(
                                        text = "يقبل أرقام الجزائر: 05 / 06 / 07 XXXXXXXX",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                        )

                        // Wilaya — dropdown with all 58 Algerian wilayas
                        var wilayaExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = wilayaExpanded,
                            onExpandedChange = { wilayaExpanded = it },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            OutlinedTextField(
                                value = uiState.wilaya,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("الولاية") },
                                placeholder = { Text("اختر ولايتك") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.LocationOn,
                                        contentDescription = null,
                                        tint = PrimaryRed,
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = wilayaExpanded)
                                },
                                isError = wilayaError != null,
                                supportingText = {
                                    wilayaError?.let { err ->
                                        Text(err, color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            )
                            ExposedDropdownMenu(
                                expanded = wilayaExpanded,
                                onDismissRequest = { wilayaExpanded = false },
                            ) {
                                ALGERIAN_WILAYAS.forEach { wilaya ->
                                    val isSelected = uiState.wilaya == wilaya
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = wilaya,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) PrimaryRed else Color.Unspecified,
                                            )
                                        },
                                        onClick = {
                                            wilayaError = null
                                            viewModel.setWilaya(wilaya)
                                            wilayaExpanded = false
                                        },
                                        trailingIcon = {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Filled.CheckCircle,
                                                    contentDescription = null,
                                                    tint = PrimaryRed,
                                                    modifier = Modifier.size(18.dp),
                                                )
                                            }
                                        },
                                    )
                                }
                            }
                        }

                        // ── Payment method — COD only ────────────────────────
                        CheckoutSectionHeader(
                            label = "طريقة الدفع",
                            icon = Icons.Filled.AccountBalanceWallet,
                        )
                        CodPaymentCard()

                        // Bottom spacing so content clears the bottom bar
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

// ─── Sub-components ──────────────────────────────────────────────────────────

@Composable
private fun CodBadge() {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AccentGoldLight),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = AccentGold,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "الدفع عند الاستلام فقط",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF7B5800),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun OrderSummaryCard(course: CourseDetail) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = course.thumbnail_url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NadjahGray100),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = course.title_ar ?: course.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                )
                if (!course.instructor_name.isNullOrBlank()) {
                    Text(
                        text = course.instructor_name_ar ?: course.instructor_name ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    course.original_price
                        ?.takeIf { it > course.price }
                        ?.let { orig ->
                            Text(
                                text = "${String.format("%.0f", orig)} دج",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    textDecoration = TextDecoration.LineThrough,
                                ),
                                color = NadjahGray400,
                            )
                        }
                    Text(
                        text = "${String.format("%.0f", course.price)} دج",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryRed,
                    )
                }
            }
        }
    }
}

@Composable
private fun CodPaymentCard() {
    Card(
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryRed),
        colors = CardDefaults.cardColors(containerColor = PrimaryRedLight),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Filled radio circle indicator
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .border(2.dp, PrimaryRed, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(PrimaryRed, CircleShape),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "الدفع عند الاستلام",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryRed,
                )
                Text(
                    text = "ادفع نقداً عند تأكيد وصول طلبك",
                    style = MaterialTheme.typography.bodySmall,
                    color = NadjahGray600,
                )
            }
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = PrimaryRed,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun CheckoutSectionHeader(label: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryRed,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = NadjahGray900,
        )
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NadjahRed100),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = NadjahRed600,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = message,
                color = NadjahRed600,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun BottomActionBar(
    course: CourseDetail?,
    isProcessing: Boolean,
    onWhatsApp: () -> Unit,
    onConfirm: () -> Unit,
) {
    Surface(
        shadowElevation = 16.dp,
        tonalElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Price summary row
            if (course != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "المجموع الكلي:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NadjahGray700,
                    )
                    Text(
                        text = "${String.format("%.0f", course.price)} دج",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryRed,
                    )
                }
                HorizontalDivider(color = NadjahGray200)
            }

            // WhatsApp order button
            Button(
                onClick = onWhatsApp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WhatsAppGreen,
                    contentColor = Color.White,
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "طلب عبر واتساب",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            // Confirm order button
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = !isProcessing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryRed,
                    contentColor = Color.White,
                    disabledContainerColor = PrimaryRedDark,
                    disabledContentColor = Color.White.copy(alpha = 0.7f),
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp,
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "جاري تأكيد الطلب...",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "تأكيد الطلب",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}
