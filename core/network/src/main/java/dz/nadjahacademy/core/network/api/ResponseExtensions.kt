package dz.nadjahacademy.core.network.api

import retrofit2.Response

/**
 * Extension property that unwraps a Retrofit [Response] containing an [ApiResponse] body,
 * returning the inner [ApiResponse.data] value.
 *
 * This allows ViewModels using `runCatching { api.call() }.onSuccess { response -> response.data }`
 * to work without manually calling `.body()?.data` everywhere.
 *
 * Usage:
 * ```
 * runCatching { api.getCourse(id) }
 *     .onSuccess { response -> // response: Response<ApiResponse<CourseDetail>>
 *         val course: CourseDetail? = response.data
 *     }
 * ```
 */
val <T> Response<ApiResponse<T>>.data: T?
    get() = body()?.data

/**
 * Returns true if the HTTP response was successful (2xx) AND the [ApiResponse.success] flag
 * is true.
 */
val <T> Response<ApiResponse<T>>.isApiSuccess: Boolean
    get() = isSuccessful && body()?.success == true

/**
 * Returns the error message from the [ApiResponse] body, or the raw HTTP error message if the
 * body is null.
 */
val <T> Response<ApiResponse<T>>.apiErrorMessage: String?
    get() = body()?.error ?: body()?.message ?: errorBody()?.string()
