package dz.nadjahacademy.feature.payment.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dz.nadjahacademy.core.network.api.CoursesApiService
import dz.nadjahacademy.core.network.api.PaymentsApiService
import dz.nadjahacademy.core.network.api.data
import dz.nadjahacademy.core.network.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val isLoading: Boolean = false,
    val course: CourseDetail? = null,
    // COD contact form fields
    val fullName: String = "",
    val phone: String = "",
    val wilaya: String = "",
    // coupon (retained for future use)
    val selectedMethod: String = "cash-on-delivery",
    val couponCode: String = "",
    val couponDiscount: Double? = null,
    val couponError: String? = null,
    val isProcessing: Boolean = false,
    val paymentSuccess: Boolean = false,
    val orderId: String? = null,
    val error: String? = null,
)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val coursesApi: CoursesApiService,
    private val paymentsApi: PaymentsApiService,
) : ViewModel() {

    // Supports both legacy "courseId" and new "courseIds" nav arg
    private val _savedCourseIds: String? =
        savedStateHandle["courseIds"] ?: savedStateHandle["courseId"]

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        _savedCourseIds?.let { loadCourses(it) }
    }

    /** Load the first course in the comma-separated list for display purposes. */
    fun loadCourses(courseIds: String) {
        // Avoid re-fetching if already loaded for the same course
        val firstId = courseIds.split(",").firstOrNull()?.trim() ?: return
        if (_uiState.value.course?.id == firstId) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { coursesApi.getCourseDetail(firstId) }
                .onSuccess { r ->
                    _uiState.update { it.copy(isLoading = false, course = r.data) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    // ── Form field setters ────────────────────────────────────────────────────

    fun setFullName(value: String) = _uiState.update { it.copy(fullName = value) }
    fun setPhone(value: String) = _uiState.update { it.copy(phone = value) }
    fun setWilaya(value: String) = _uiState.update { it.copy(wilaya = value) }
    fun selectMethod(method: String) = _uiState.update { it.copy(selectedMethod = method) }
    fun setCouponCode(code: String) =
        _uiState.update { it.copy(couponCode = code, couponError = null, couponDiscount = null) }

    fun applyCoupon() {
        val code = _uiState.value.couponCode.trim()
        if (code.isEmpty()) return
        viewModelScope.launch {
            runCatching {
                paymentsApi.validateCoupon(ValidateCouponRequest(code = code, subtotal = 0.0))
            }
                .onSuccess { r ->
                    _uiState.update { it.copy(couponDiscount = r.data?.discount) }
                }
                .onFailure {
                    _uiState.update { it.copy(couponError = "كود الخصم غير صالح أو منتهي الصلاحية") }
                }
        }
    }

    /** Place a Cash-on-Delivery order using the current form state. */
    fun checkout() {
        val state = _uiState.value
        val course = state.course ?: return
        // Use saved nav arg first, fall back to loaded course id
        val ids = (_savedCourseIds ?: course.id)
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        if (ids.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            runCatching {
                paymentsApi.createOrder(
                    CreateOrderRequest(
                        course_ids = ids,
                        full_name = state.fullName.trim(),
                        email = "",
                        phone = state.phone.trim(),
                        wilaya = state.wilaya.ifBlank { null },
                        payment_method = "cash-on-delivery",
                        coupon_code = state.couponCode.trim().takeIf { it.isNotBlank() },
                    )
                )
            }
                .onSuccess { r ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            paymentSuccess = true,
                            orderId = r.data?.order_id,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isProcessing = false, error = e.message) }
                }
        }
    }
}
