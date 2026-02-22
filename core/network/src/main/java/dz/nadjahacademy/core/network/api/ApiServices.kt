package dz.nadjahacademy.core.network.api

import dz.nadjahacademy.core.network.model.*
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.*

// ─── Response Wrapper ──────────────────────────────────────
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: String? = null,
    val pagination: Pagination? = null,
)

@Serializable
data class Pagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val total_pages: Int,
    val has_next: Boolean,
    val has_prev: Boolean,
)

// ─── Auth API ──────────────────────────────────────────────
interface AuthApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/social-login")
    suspend fun socialLogin(@Body request: SocialLoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<ApiResponse<TokenResponse>>

    @POST("auth/logout")
    suspend fun logout(@Body request: LogoutRequest): Response<ApiResponse<Unit>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<Unit>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ApiResponse<Unit>>

    @POST("auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<ApiResponse<Unit>>

    @POST("auth/resend-verification")
    suspend fun resendVerification(): Response<ApiResponse<Unit>>

    @POST("auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Unit>>

    @DELETE("auth/account")
    suspend fun deleteAccount(@Body request: DeleteAccountRequest): Response<ApiResponse<Unit>>
}

// ─── Courses API ───────────────────────────────────────────
interface CoursesApiService {
    @GET("courses")
    suspend fun getCourses(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null,
        @Query("level") level: String? = null,
        @Query("language") language: String? = null,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null,
        @Query("min_rating") minRating: Double? = null,
        @Query("sort") sort: String = "newest",
        @Query("is_free") isFree: Boolean? = null,
        @Query("search") search: String? = null,
    ): Response<ApiResponse<List<CourseListItem>>>

    @GET("courses/featured")
    suspend fun getFeaturedCourses(): Response<ApiResponse<List<CourseListItem>>>

    @GET("courses/popular")
    suspend fun getPopularCourses(): Response<ApiResponse<List<CourseListItem>>>

    @GET("courses/trending")
    suspend fun getTrendingCourses(): Response<ApiResponse<List<CourseListItem>>>

    @GET("courses/new")
    suspend fun getNewCourses(): Response<ApiResponse<List<CourseListItem>>>

    @GET("courses/recommended")
    suspend fun getRecommendedCourses(): Response<ApiResponse<List<CourseListItem>>>

    @GET("courses/{slug}")
    suspend fun getCourseDetail(@Path("slug") slug: String): Response<ApiResponse<CourseDetail>>

    @GET("courses/{id}/reviews")
    suspend fun getCourseReviews(
        @Path("id") courseId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
    ): Response<ApiResponse<List<Review>>>

    @POST("courses/{id}/reviews")
    suspend fun submitReview(
        @Path("id") courseId: String,
        @Body request: SubmitReviewRequest,
    ): Response<ApiResponse<Review>>

    @PUT("courses/{courseId}/reviews/{reviewId}")
    suspend fun updateReview(
        @Path("courseId") courseId: String,
        @Path("reviewId") reviewId: String,
        @Body request: SubmitReviewRequest,
    ): Response<ApiResponse<Unit>>

    @DELETE("courses/{courseId}/reviews/{reviewId}")
    suspend fun deleteReview(
        @Path("courseId") courseId: String,
        @Path("reviewId") reviewId: String,
    ): Response<ApiResponse<Unit>>

    @GET("courses/{id}/curriculum")
    suspend fun getCourseCurriculum(@Path("id") courseId: String): Response<ApiResponse<List<CourseSection>>>

    @POST("courses/{id}/enroll")
    suspend fun enrollFree(@Path("id") courseId: String): Response<ApiResponse<Unit>>

    @GET("courses/{id}/certificate")
    suspend fun getCertificate(@Path("id") courseId: String): Response<ApiResponse<Certificate>>

    @POST("courses/{id}/bookmark")
    suspend fun toggleBookmark(@Path("id") courseId: String): Response<ApiResponse<BookmarkResponse>>

    @GET("courses/{id}/announcements")
    suspend fun getAnnouncements(@Path("id") courseId: String): Response<ApiResponse<List<Announcement>>>
}

// ─── Lessons API ───────────────────────────────────────────
interface LessonsApiService {
    @GET("lessons/{id}")
    suspend fun getLesson(@Path("id") lessonId: String): Response<ApiResponse<LessonDetail>>

    @POST("lessons/{id}/complete")
    suspend fun completeLesson(
        @Path("id") lessonId: String,
        @Body request: LessonProgressRequest,
    ): Response<ApiResponse<LessonCompletionResponse>>

    @POST("lessons/{id}/progress")
    suspend fun saveProgress(
        @Path("id") lessonId: String,
        @Body request: LessonProgressRequest,
    ): Response<ApiResponse<Unit>>

    @GET("lessons/{id}/notes")
    suspend fun getNotes(@Path("id") lessonId: String): Response<ApiResponse<List<Note>>>

    @POST("lessons/{id}/notes")
    suspend fun createNote(
        @Path("id") lessonId: String,
        @Body request: CreateNoteRequest,
    ): Response<ApiResponse<Note>>

    @PUT("lessons/{lessonId}/notes/{noteId}")
    suspend fun updateNote(
        @Path("lessonId") lessonId: String,
        @Path("noteId") noteId: String,
        @Body request: CreateNoteRequest,
    ): Response<ApiResponse<Unit>>

    @DELETE("lessons/{lessonId}/notes/{noteId}")
    suspend fun deleteNote(
        @Path("lessonId") lessonId: String,
        @Path("noteId") noteId: String,
    ): Response<ApiResponse<Unit>>

    @GET("lessons/{id}/timestamp-bookmarks")
    suspend fun getTimestampBookmarks(@Path("id") lessonId: String): Response<ApiResponse<List<TimestampBookmark>>>

    @POST("lessons/{id}/timestamp-bookmarks")
    suspend fun addTimestampBookmark(
        @Path("id") lessonId: String,
        @Body request: TimestampBookmarkRequest,
    ): Response<ApiResponse<TimestampBookmark>>
}

// ─── More APIs ─────────────────────────────────────────────
interface QuizzesApiService {
    @GET("quizzes/{id}")
    suspend fun getQuiz(@Path("id") quizId: String): Response<ApiResponse<QuizDetail>>

    @POST("quizzes/{id}/start")
    suspend fun startQuiz(@Path("id") quizId: String): Response<ApiResponse<QuizAttemptStart>>

    @POST("quizzes/{id}/submit")
    suspend fun submitQuiz(
        @Path("id") quizId: String,
        @Body request: QuizSubmitRequest,
    ): Response<ApiResponse<QuizResult>>

    @GET("quizzes/{id}/results/{attemptId}")
    suspend fun getResults(
        @Path("id") quizId: String,
        @Path("attemptId") attemptId: String,
    ): Response<ApiResponse<QuizAttempt>>
}

interface CategoriesApiService {
    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<Category>>>

    @GET("categories/{slug}")
    suspend fun getCategoryDetail(@Path("slug") slug: String): Response<ApiResponse<Category>>

    @GET("categories/{slug}/courses")
    suspend fun getCategoryCourses(
        @Path("slug") slug: String,
        @Query("page") page: Int = 1,
    ): Response<ApiResponse<List<CourseListItem>>>
}

interface InstructorsApiService {
    @GET("instructors")
    suspend fun getInstructors(
        @Query("featured") featured: Boolean? = null,
    ): Response<ApiResponse<List<InstructorListItem>>>

    @GET("instructors/{slug}")
    suspend fun getInstructorDetail(@Path("slug") slug: String): Response<ApiResponse<InstructorDetail>>

    @POST("instructors/{id}/follow")
    suspend fun toggleFollow(@Path("id") instructorId: String): Response<ApiResponse<FollowResponse>>
}

interface BlogsApiService {
    @GET("blogs")
    suspend fun getBlogs(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 12,
        @Query("category") category: String? = null,
        @Query("featured") featured: Boolean? = null,
        @Query("search") search: String? = null,
        @Query("sort") sort: String = "newest",
    ): Response<ApiResponse<List<BlogListItem>>>

    @GET("blogs/featured")
    suspend fun getFeaturedBlogs(): Response<ApiResponse<List<BlogListItem>>>

    @GET("blogs/categories")
    suspend fun getBlogCategories(): Response<ApiResponse<List<BlogCategory>>>

    @GET("blogs/{slug}")
    suspend fun getBlogDetail(@Path("slug") slug: String): Response<ApiResponse<BlogDetail>>

    @GET("blogs/{id}/comments")
    suspend fun getComments(@Path("id") blogId: String): Response<ApiResponse<List<BlogComment>>>

    @POST("blogs/{id}/comments")
    suspend fun addComment(
        @Path("id") blogId: String,
        @Body request: AddCommentRequest,
    ): Response<ApiResponse<BlogComment>>

    @DELETE("blogs/{blogId}/comments/{commentId}")
    suspend fun deleteComment(
        @Path("blogId") blogId: String,
        @Path("commentId") commentId: String,
    ): Response<ApiResponse<Unit>>

    @POST("blogs/{id}/like")
    suspend fun toggleLike(@Path("id") blogId: String): Response<ApiResponse<LikeResponse>>
}

interface UsersApiService {
    @GET("users/me")
    suspend fun getMe(): Response<ApiResponse<UserProfile>>

    @PUT("users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<UserProfile>>

    @GET("users/me/stats")
    suspend fun getStats(): Response<ApiResponse<UserStats>>

    @GET("users/me/achievements")
    suspend fun getAchievements(): Response<ApiResponse<AchievementsResponse>>

    @GET("users/me/certificates")
    suspend fun getCertificates(): Response<ApiResponse<List<Certificate>>>

    @GET("users/me/streak-calendar")
    suspend fun getStreakCalendar(): Response<ApiResponse<List<StreakDay>>>

    @GET("users/me/notes")
    suspend fun getNotes(@Query("course_id") courseId: String? = null): Response<ApiResponse<List<Note>>>

    @GET("users/me/bookmarks")
    suspend fun getBookmarks(@Query("type") type: String = "course"): Response<ApiResponse<List<CourseListItem>>>

    @PUT("users/me/notification-settings")
    suspend fun updateNotificationSettings(@Body request: NotificationSettingsRequest): Response<ApiResponse<Unit>>

    @POST("users/me/push-device")
    suspend fun registerPushDevice(@Body request: PushDeviceRequest): Response<ApiResponse<Unit>>

    @DELETE("users/me/push-device")
    suspend fun unregisterPushDevice(@Body request: UnregisterDeviceRequest): Response<ApiResponse<Unit>>

    @Multipart
    @POST("upload/avatar")
    suspend fun uploadAvatar(@Part file: okhttp3.MultipartBody.Part): Response<ApiResponse<UploadResponse>>
}

interface NotificationsApiService {
    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("unread_only") unreadOnly: Boolean = false,
    ): Response<ApiResponse<List<Notification>>>

    @GET("notifications/unread-count")
    suspend fun getUnreadCount(): Response<ApiResponse<UnreadCountResponse>>

    @POST("notifications/mark-read")
    suspend fun markRead(@Body request: MarkReadRequest): Response<ApiResponse<Unit>>

    @DELETE("notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: String): Response<ApiResponse<Unit>>
}

interface PaymentsApiService {
    @POST("payments/create-intent")
    suspend fun createPaymentIntent(@Body request: PaymentIntentRequest): Response<ApiResponse<PaymentIntentResponse>>

    @POST("payments/validate-coupon")
    suspend fun validateCoupon(@Body request: ValidateCouponRequest): Response<ApiResponse<CouponResponse>>

    @POST("payments/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<ApiResponse<OrderResponse>>

    @GET("payments/orders")
    suspend fun getOrders(): Response<ApiResponse<List<Order>>>

    @GET("payments/orders/{id}")
    suspend fun getOrderDetail(@Path("id") orderId: String): Response<ApiResponse<OrderDetail>>

    @POST("payments/redeem-code")
    suspend fun redeemCode(@Body request: RedeemCodeRequest): Response<ApiResponse<RedeemResponse>>

    @POST("payments/refund-request")
    suspend fun requestRefund(@Body request: RefundRequest): Response<ApiResponse<Unit>>
}

interface SearchApiService {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("type") type: String = "all",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
    ): Response<ApiResponse<SearchResults>>

    @GET("search/suggestions")
    suspend fun getSuggestions(@Query("q") query: String): Response<ApiResponse<List<SearchSuggestion>>>

    @GET("search/trending")
    suspend fun getTrending(): Response<ApiResponse<List<TrendingSearch>>>

    @GET("search/history")
    suspend fun getHistory(): Response<ApiResponse<List<SearchHistory>>>

    @DELETE("search/history")
    suspend fun deleteHistory(@Body request: DeleteHistoryRequest? = null): Response<ApiResponse<Unit>>
}

interface DiscussionsApiService {
    @GET("discussions")
    suspend fun getDiscussions(
        @Query("course_id") courseId: String,
        @Query("lesson_id") lessonId: String? = null,
        @Query("page") page: Int = 1,
    ): Response<ApiResponse<List<Discussion>>>

    @POST("discussions")
    suspend fun createDiscussion(@Body request: CreateDiscussionRequest): Response<ApiResponse<Discussion>>

    @GET("discussions/{id}")
    suspend fun getDiscussionDetail(@Path("id") id: String): Response<ApiResponse<DiscussionDetail>>

    @POST("discussions/{id}/replies")
    suspend fun addReply(
        @Path("id") discussionId: String,
        @Body request: AddReplyRequest,
    ): Response<ApiResponse<DiscussionReply>>

    @POST("discussions/{id}/vote")
    suspend fun vote(
        @Path("id") discussionId: String,
        @Body request: VoteRequest,
    ): Response<ApiResponse<Unit>>

    @PATCH("discussions/replies/{replyId}/best-answer")
    suspend fun markBestAnswer(@Path("replyId") replyId: String): Response<ApiResponse<Unit>>
}

interface MyLearningApiService {
    @GET("my-learning")
    suspend fun getEnrolledCourses(
        @Query("filter") filter: String = "all",
    ): Response<ApiResponse<List<EnrolledCourse>>>

    @GET("my-learning/wishlist")
    suspend fun getWishlist(): Response<ApiResponse<List<CourseListItem>>>

    @POST("my-learning/wishlist/{courseId}")
    suspend fun toggleWishlist(@Path("courseId") courseId: String): Response<ApiResponse<WishlistResponse>>

    @GET("my-learning/{courseId}")
    suspend fun getEnrolledCourseDetail(@Path("courseId") courseId: String): Response<ApiResponse<EnrolledCourseDetail>>

    @GET("certificates/{certId}")
    suspend fun getCertificate(@Path("certId") certId: String): Certificate
}

interface MiscApiService {
    @GET("config")
    suspend fun getAppConfig(): Response<ApiResponse<AppConfig>>

    @GET("banners")
    suspend fun getBanners(@Query("position") position: String = "home"): Response<ApiResponse<List<Banner>>>

    @GET("testimonials")
    suspend fun getTestimonials(): Response<ApiResponse<List<Testimonial>>>

    @GET("faqs")
    suspend fun getFaqs(
        @Query("category") category: String? = null,
        @Query("lang") lang: String = "en",
    ): Response<ApiResponse<List<Faq>>>

    @GET("pages/{slug}")
    suspend fun getPage(@Path("slug") slug: String): Response<ApiResponse<StaticPage>>

    @GET("health")
    suspend fun healthCheck(): Response<ApiResponse<HealthResponse>>

    @POST("analytics/events")
    suspend fun trackEvent(@Body event: AnalyticsEvent): Response<ApiResponse<Unit>>

    @POST("analytics/batch-events")
    suspend fun trackBatchEvents(@Body request: BatchEventsRequest): Response<ApiResponse<Unit>>
}

interface SupportApiService {
    @POST("support/tickets")
    suspend fun createTicket(@Body request: CreateTicketRequest): Response<ApiResponse<TicketResponse>>

    @GET("support/tickets")
    suspend fun getTickets(): Response<ApiResponse<List<SupportTicket>>>

    @GET("support/tickets/{id}")
    suspend fun getTicketDetail(@Path("id") id: String): Response<ApiResponse<SupportTicketDetail>>

    @POST("support/tickets/{id}/messages")
    suspend fun sendMessage(
        @Path("id") ticketId: String,
        @Body request: SendMessageRequest,
    ): Response<ApiResponse<Unit>>
}
