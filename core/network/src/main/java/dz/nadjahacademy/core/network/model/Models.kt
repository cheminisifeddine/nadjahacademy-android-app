package dz.nadjahacademy.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ─── Auth Models ──────────────────────────────────────────
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val full_name: String,
    val phone: String? = null,
    val language: String = "ar",
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
    val device_info: String? = null,
)

@Serializable
data class SocialLoginRequest(
    val provider: String,
    val id_token: String,
    val device_info: String? = null,
)

@Serializable
data class RefreshTokenRequest(val refresh_token: String)

@Serializable
data class LogoutRequest(val refresh_token: String? = null)

@Serializable
data class ForgotPasswordRequest(val email: String)

@Serializable
data class ResetPasswordRequest(val token: String, val password: String)

@Serializable
data class VerifyEmailRequest(val token: String)

@Serializable
data class ChangePasswordRequest(
    val current_password: String,
    val new_password: String,
)

@Serializable
data class DeleteAccountRequest(val password: String? = null)

@Serializable
data class AuthResponse(
    val access_token: String,
    val refresh_token: String,
    val token_type: String = "Bearer",
    val expires_in: Int = 900,
    val user: AuthUser,
)

@Serializable
data class TokenResponse(
    val access_token: String,
    val refresh_token: String,
    val token_type: String,
    val expires_in: Int,
)

@Serializable
data class AuthUser(
    val id: String,
    val email: String,
    val full_name: String,
    val role: String,
    val email_verified: Boolean,
    val language: String,
    val avatar_url: String? = null,
    val learning_streak: Int = 0,
    val total_points: Int = 0,
    val level: Int = 1,
)

// ─── User Models ──────────────────────────────────────────
@Serializable
data class UserProfile(
    val id: String,
    val email: String,
    val full_name: String,
    val username: String? = null,
    val phone: String? = null,
    val avatar_url: String? = null,
    val bio: String? = null,
    val headline: String? = null,
    val website: String? = null,
    val location: String? = null,
    val role: String,
    val email_verified: Boolean,
    val language: String,
    val theme: String,
    val total_points: Int,
    val level: Int,
    val learning_streak: Int,
    val longest_streak: Int,
    val last_activity_date: String? = null,
    val notification_email: Boolean = true,
    val notification_push: Boolean = true,
    val notify_course_updates: Boolean = true,
    val notify_promotions: Boolean = true,
    val notify_reminders: Boolean = true,
    val notify_community: Boolean = true,
    val created_at: String,
)

@Serializable
data class UpdateProfileRequest(
    val full_name: String? = null,
    val username: String? = null,
    val phone: String? = null,
    val bio: String? = null,
    val headline: String? = null,
    val website: String? = null,
    val location: String? = null,
    val language: String? = null,
    val theme: String? = null,
)

@Serializable
data class UserStats(
    val enrolled_courses: Int,
    val completed_courses: Int,
    val total_notes: Int,
    val total_reviews: Int,
    val total_certificates: Int,
    val learning_streak: Int,
    val longest_streak: Int,
    val total_points: Int,
    val level: Int,
)

@Serializable
data class AchievementsResponse(
    val earned: List<Achievement>,
    val locked: List<Achievement>,
)

@Serializable
data class Achievement(
    val id: String,
    val slug: String,
    val name: String,
    val name_ar: String? = null,
    val description: String? = null,
    val icon_url: String? = null,
    val badge_url: String? = null,
    val points: Int,
    val type: String,
    val rarity: String,
    val earned_at: String? = null,
)

@Serializable
data class StreakDay(
    val date: String,
    val lessons_completed: Int,
    val minutes_learned: Int,
)

@Serializable
data class NotificationSettingsRequest(
    val notification_email: Boolean? = null,
    val notification_push: Boolean? = null,
    val notify_course_updates: Boolean? = null,
    val notify_promotions: Boolean? = null,
    val notify_reminders: Boolean? = null,
    val notify_community: Boolean? = null,
)

@Serializable
data class PushDeviceRequest(
    val fcm_token: String,
    val platform: String = "android",
    val device_name: String? = null,
    val app_version: String? = null,
)

@Serializable
data class UnregisterDeviceRequest(val fcm_token: String? = null)

@Serializable
data class UploadResponse(val url: String, val key: String)

// ─── Course Models ────────────────────────────────────────
@Serializable
data class CourseListItem(
    val id: String,
    val slug: String,
    val title: String,
    val title_ar: String? = null,
    val title_fr: String? = null,
    val subtitle: String? = null,
    val subtitle_ar: String? = null,
    val price: Double,
    val original_price: Double? = null,
    val currency: String = "DZD",
    val level: String,
    val language: String,
    val thumbnail_url: String? = null,
    val total_duration: Int = 0,
    val total_lessons: Int = 0,
    val total_students: Int = 0,
    val average_rating: Double = 0.0,
    val total_reviews: Int = 0,
    val is_featured: Boolean = false,
    val is_bestseller: Boolean = false,
    val is_new: Boolean = false,
    val is_trending: Boolean = false,
    val is_free: Boolean = false,
    val has_certificate: Boolean = false,
    val category_name: String? = null,
    val category_name_ar: String? = null,
    val category_slug: String? = null,
    val instructor_name: String? = null,
    val instructor_name_ar: String? = null,
    val instructor_avatar: String? = null,
    val instructor_verified: Boolean = false,
    val created_at: String? = null,
)

@Serializable
data class CourseDetail(
    val id: String,
    val slug: String,
    val title: String,
    val title_ar: String? = null,
    val subtitle: String? = null,
    val subtitle_ar: String? = null,
    val description: String? = null,
    val description_ar: String? = null,
    val price: Double,
    val original_price: Double? = null,
    val currency: String = "DZD",
    val level: String,
    val language: String,
    val thumbnail_url: String? = null,
    val cover_url: String? = null,
    val preview_video_url: String? = null,
    val what_you_will_learn: List<String> = emptyList(),
    val requirements: List<String> = emptyList(),
    val total_duration: Int = 0,
    val total_lessons: Int = 0,
    val total_sections: Int = 0,
    val total_students: Int = 0,
    val average_rating: Double = 0.0,
    val total_reviews: Int = 0,
    val rating_breakdown: RatingBreakdown? = null,
    val is_featured: Boolean = false,
    val is_bestseller: Boolean = false,
    val has_certificate: Boolean = false,
    val certificate_enabled: Boolean = false,
    val discussions_enabled: Boolean = true,
    val category_name: String? = null,
    val category_name_ar: String? = null,
    val category_slug: String? = null,
    val instructor_id: String? = null,
    val instructor_name: String? = null,
    val instructor_name_ar: String? = null,
    val instructor_bio: String? = null,
    val instructor_bio_ar: String? = null,
    val instructor_avatar: String? = null,
    val instructor_headline: String? = null,
    val instructor_students: Int = 0,
    val instructor_courses: Int = 0,
    val instructor_rating: Double = 0.0,
    val instructor_verified: Boolean = false,
    val instructor_twitter: String? = null,
    val instructor_linkedin: String? = null,
    val sections: List<CourseSection> = emptyList(),
    val is_enrolled: Boolean = false,
    val enrollment: EnrollmentInfo? = null,
    val created_at: String? = null,
)

@Serializable
data class RatingBreakdown(
    @SerialName("1") val one: Int = 0,
    @SerialName("2") val two: Int = 0,
    @SerialName("3") val three: Int = 0,
    @SerialName("4") val four: Int = 0,
    @SerialName("5") val five: Int = 0,
)

@Serializable
data class CourseSection(
    val id: String,
    val title: String,
    val title_ar: String? = null,
    val sort_order: Int,
    val total_duration: Int = 0,
    val total_lessons: Int = 0,
    val lessons: List<LessonItem> = emptyList(),
)

@Serializable
data class LessonItem(
    val id: String,
    val section_id: String,
    val title: String,
    val title_ar: String? = null,
    val type: String,
    val duration: Int = 0,
    val sort_order: Int,
    val is_preview: Boolean = false,
    val video_url: String? = null,
    val bunny_video_guid: String? = null,
    val is_completed: Boolean = false,
    val watch_position: Int = 0,
    val watch_percentage: Double = 0.0,
)

@Serializable
data class EnrollmentInfo(
    val id: String,
    val enrolled_at: String,
    val progress_percentage: Double = 0.0,
    val completed_lessons: Int = 0,
    val total_lessons: Int = 0,
    val is_completed: Boolean = false,
    val last_lesson_id: String? = null,
    val last_lesson_position: Int = 0,
)

@Serializable
data class Review(
    val id: String,
    val course_id: String,
    val user_id: String,
    val rating: Int,
    val title: String? = null,
    val body: String? = null,
    val helpful_count: Int = 0,
    val user_name: String? = null,
    val user_avatar: String? = null,
    val created_at: String,
)

@Serializable
data class SubmitReviewRequest(
    val rating: Int,
    val title: String? = null,
    val body: String? = null,
)

@Serializable
data class BookmarkResponse(val bookmarked: Boolean)

@Serializable
data class Announcement(
    val id: String,
    val course_id: String,
    val title: String,
    val body: String,
    val created_at: String,
)

// ─── Lesson Models ────────────────────────────────────────
@Serializable
data class LessonDetail(
    val id: String,
    val course_id: String,
    val section_id: String,
    val title: String,
    val title_ar: String? = null,
    val description: String? = null,
    val type: String,
    val content: String? = null,
    val content_ar: String? = null,
    val duration: Int = 0,
    val video_url: String? = null,
    val bunny_video_guid: String? = null,
    val subtitles: List<Subtitle> = emptyList(),
    val is_preview: Boolean = false,
    val section_title: String? = null,
    val progress: LessonProgress? = null,
    val resources: List<LessonResource> = emptyList(),
    val prev_lesson: AdjacentLesson? = null,
    val next_lesson: AdjacentLesson? = null,
)

@Serializable
data class Subtitle(val lang: String, val url: String)

@Serializable
data class LessonProgress(
    val is_completed: Boolean = false,
    val watch_position: Int = 0,
    val watch_percentage: Double = 0.0,
    val completed_at: String? = null,
)

@Serializable
data class LessonResource(
    val id: String,
    val lesson_id: String,
    val title: String,
    val file_url: String,
    val file_type: String? = null,
    val file_size: Long? = null,
    val sort_order: Int = 0,
)

@Serializable
data class AdjacentLesson(
    val id: String,
    val title: String,
    val type: String,
    val duration: Int,
    val sort_order: Int,
)

@Serializable
data class LessonProgressRequest(
    val position: Int = 0,
)

@Serializable
data class LessonCompletionResponse(
    val progress_percentage: Double,
    val completed_lessons: Int,
    val total_lessons: Int,
    val is_course_complete: Boolean,
    val certificate: Certificate? = null,
)

@Serializable
data class Note(
    val id: String,
    val user_id: String,
    val lesson_id: String,
    val course_id: String,
    val content: String,
    val timestamp: Int = 0,
    val lesson_title: String? = null,
    val course_title: String? = null,
    val created_at: String,
    val updated_at: String,
)

@Serializable
data class CreateNoteRequest(val content: String, val timestamp: Int = 0)

@Serializable
data class TimestampBookmark(
    val id: String,
    val lesson_id: String,
    val user_id: String,
    val timestamp: Int,
    val label: String? = null,
    val created_at: String,
)

@Serializable
data class TimestampBookmarkRequest(val timestamp: Int, val label: String? = null)

// ─── Certificate ──────────────────────────────────────────
@Serializable
data class Certificate(
    val id: String,
    val user_id: String,
    val course_id: String,
    val certificate_number: String,
    val issued_at: String,
    val pdf_url: String? = null,
    val course_title: String? = null,
    val user_name: String? = null,
    val thumbnail_url: String? = null,
)

// ─── Quiz Models ──────────────────────────────────────────
@Serializable
data class QuizDetail(
    val id: String,
    val course_id: String,
    val lesson_id: String? = null,
    val title: String,
    val description: String? = null,
    val time_limit: Int? = null,
    val pass_percentage: Int = 70,
    val max_attempts: Int? = null,
    val shuffle_questions: Boolean = false,
    val shuffle_options: Boolean = false,
    val show_results_immediately: Boolean = true,
    val allow_review: Boolean = true,
    val questions: List<QuizQuestion> = emptyList(),
    val attempt_count: Int = 0,
    val can_attempt: Boolean = true,
)

@Serializable
data class QuizQuestion(
    val id: String,
    val question: String,
    val type: String,
    val explanation: String? = null,
    val points: Int = 1,
    val sort_order: Int,
    val image_url: String? = null,
    val options: List<QuizOption> = emptyList(),
)

@Serializable
data class QuizOption(
    val id: String,
    val question_id: String,
    val option_text: String,
    val sort_order: Int,
)

@Serializable
data class QuizAttemptStart(
    val attempt_id: String,
    val started_at: String,
)

@Serializable
data class QuizSubmitRequest(
    val attempt_id: String,
    val answers: List<QuizAnswer>,
)

@Serializable
data class QuizAnswer(
    val question_id: String,
    val selected_option_ids: List<String> = emptyList(),
    val text_answer: String = "",
)

@Serializable
data class QuizResult(
    val attempt_id: String,
    val score: Double,
    val max_score: Double,
    val percentage: Double,
    val passed: Boolean,
    val pass_percentage: Int,
    val time_taken: Int,
    val graded_answers: List<GradedAnswer>? = null,
)

@Serializable
data class QuizAttempt(
    val id: String,
    val quiz_id: String,
    val score: Double? = null,
    val max_score: Double? = null,
    val percentage: Double? = null,
    val passed: Boolean? = null,
    val time_taken: Int? = null,
    val answers: List<QuizAnswer> = emptyList(),
)

@Serializable
data class GradedAnswer(
    val question_id: String,
    val question: String,
    val selected_option_ids: List<String>,
    val text_answer: String,
    val correct_option_ids: List<String>,
    val is_correct: Boolean,
    val points_earned: Double,
    val points_possible: Double,
    val explanation: String? = null,
)

// ─── Category & Instructor Models ─────────────────────────
@Serializable
data class Category(
    val id: String,
    val name: String,
    val name_ar: String? = null,
    val name_fr: String? = null,
    val slug: String,
    val description: String? = null,
    val icon_url: String? = null,
    val color: String = "#3B82F6",
    val course_count: Int = 0,
    val sort_order: Int = 0,
)

@Serializable
data class InstructorListItem(
    val id: String,
    val slug: String,
    val name: String,
    val name_ar: String? = null,
    val headline: String? = null,
    val avatar_url: String? = null,
    val total_students: Int = 0,
    val total_courses: Int = 0,
    val average_rating: Double = 0.0,
    val is_verified: Boolean = false,
    val is_featured: Boolean = false,
)

@Serializable
data class InstructorDetail(
    val id: String,
    val slug: String,
    val name: String,
    val name_ar: String? = null,
    val bio: String? = null,
    val bio_ar: String? = null,
    val headline: String? = null,
    val avatar_url: String? = null,
    val website: String? = null,
    val twitter: String? = null,
    val linkedin: String? = null,
    val total_students: Int = 0,
    val total_courses: Int = 0,
    val total_reviews: Int = 0,
    val average_rating: Double = 0.0,
    val is_verified: Boolean = false,
    val is_featured: Boolean = false,
    val courses: List<CourseListItem> = emptyList(),
    val reviews: List<Review> = emptyList(),
)

@Serializable
data class FollowResponse(val following: Boolean)

// ─── Blog Models ──────────────────────────────────────────
@Serializable
data class BlogListItem(
    val id: String,
    val slug: String,
    val title: String,
    val title_ar: String? = null,
    val title_fr: String? = null,
    val excerpt: String? = null,
    val excerpt_ar: String? = null,
    val cover_url: String? = null,
    val category: String? = null,
    val tags: List<String> = emptyList(),
    val reading_time: Int = 5,
    val views: Int = 0,
    val likes: Int = 0,
    val comment_count: Int = 0,
    val is_featured: Boolean = false,
    val author_name: String? = null,
    val author_avatar: String? = null,
    val published_at: String? = null,
)

@Serializable
data class BlogDetail(
    val id: String,
    val slug: String,
    val title: String,
    val title_ar: String? = null,
    val excerpt: String? = null,
    val content: String? = null,
    val content_ar: String? = null,
    val cover_url: String? = null,
    val category: String? = null,
    val tags: List<String> = emptyList(),
    val reading_time: Int = 5,
    val views: Int = 0,
    val likes: Int = 0,
    val comment_count: Int = 0,
    val is_liked: Boolean = false,
    val author_name: String? = null,
    val author_avatar: String? = null,
    val author_bio: String? = null,
    val published_at: String? = null,
    val related_posts: List<BlogListItem> = emptyList(),
)

@Serializable
data class BlogCategory(val category: String, val count: Int)

@Serializable
data class BlogComment(
    val id: String,
    val blog_id: String,
    val parent_id: String? = null,
    val user_id: String,
    val body: String,
    val likes: Int = 0,
    val user_name: String? = null,
    val user_avatar: String? = null,
    val replies: List<BlogComment> = emptyList(),
    val created_at: String,
)

@Serializable
data class AddCommentRequest(val body: String, val parent_id: String? = null)

@Serializable
data class LikeResponse(val liked: Boolean)

// ─── Notification Models ──────────────────────────────────
@Serializable
data class Notification(
    val id: String,
    val type: String,
    val title: String,
    val body: String,
    val image_url: String? = null,
    val action_url: String? = null,
    val data: String? = null,
    val is_read: Boolean = false,
    val read_at: String? = null,
    val created_at: String,
)

@Serializable
data class UnreadCountResponse(val count: Int)

@Serializable
data class MarkReadRequest(val notification_id: String? = null)

// ─── Payment Models ───────────────────────────────────────
@Serializable
data class PaymentIntentRequest(
    val course_ids: List<String>,
    val coupon_code: String? = null,
)

@Serializable
data class PaymentIntentResponse(
    val subtotal: Double,
    val discount: Double,
    val total: Double,
    val currency: String,
    val client_secret: String? = null,
    val payment_intent_id: String? = null,
    val courses: List<CourseListItem>,
)

@Serializable
data class ValidateCouponRequest(
    val code: String,
    val subtotal: Double,
    val course_ids: List<String>? = null,
)

@Serializable
data class CouponResponse(
    val valid: Boolean,
    val discount: Double,
    val type: String,
    val value: Double,
    val code: String,
)

@Serializable
data class CreateOrderRequest(
    val course_ids: List<String>,
    val full_name: String,
    val email: String,
    val phone: String? = null,
    val wilaya: String? = null,
    val city: String? = null,
    val address: String? = null,
    val coupon_code: String? = null,
    val payment_method: String = "cash-on-delivery",
)

@Serializable
data class OrderResponse(
    val order_id: String,
    val order_number: String,
    val total: Double,
    val status: String,
)

@Serializable
data class Order(
    val id: String,
    val order_number: String,
    val status: String,
    val total: Double,
    val currency: String,
    val payment_method: String,
    val course_titles: String? = null,
    val created_at: String,
)

@Serializable
data class OrderDetail(
    val id: String,
    val order_number: String,
    val status: String,
    val subtotal: Double,
    val discount: Double,
    val total: Double,
    val currency: String,
    val payment_method: String,
    val full_name: String,
    val email: String,
    val phone: String? = null,
    val wilaya: String? = null,
    val city: String? = null,
    val coupon_code: String? = null,
    val items: List<OrderItem> = emptyList(),
    val created_at: String,
)

@Serializable
data class OrderItem(
    val id: String,
    val course_id: String,
    val title: String,
    val thumbnail_url: String? = null,
    val slug: String,
    val price: Double,
)

@Serializable
data class RedeemCodeRequest(val code: String)

@Serializable
data class RedeemResponse(
    val activated: Boolean,
    val course_id: String? = null,
    val course_title: String? = null,
)

@Serializable
data class RefundRequest(val order_id: String, val reason: String)

// ─── Search Models ────────────────────────────────────────
@Serializable
data class SearchResults(
    val courses: List<CourseListItem> = emptyList(),
    val blogs: List<BlogListItem> = emptyList(),
    val instructors: List<InstructorListItem> = emptyList(),
)

@Serializable
data class SearchSuggestion(val text: String, val type: String)

@Serializable
data class TrendingSearch(val query: String, val count: Int)

@Serializable
data class SearchHistory(val query: String, val last_searched: String)

@Serializable
data class DeleteHistoryRequest(val query: String? = null)

// ─── Discussion Models ────────────────────────────────────
@Serializable
data class Discussion(
    val id: String,
    val course_id: String,
    val lesson_id: String? = null,
    val user_id: String,
    val title: String,
    val body: String,
    val type: String,
    val upvotes: Int = 0,
    val reply_count: Int = 0,
    val is_pinned: Boolean = false,
    val is_answered: Boolean = false,
    val user_name: String? = null,
    val user_avatar: String? = null,
    val user_role: String? = null,
    val created_at: String,
    val updated_at: String,
)

@Serializable
data class DiscussionDetail(
    val id: String,
    val title: String,
    val body: String,
    val type: String,
    val upvotes: Int,
    val reply_count: Int,
    val is_answered: Boolean,
    val best_answer_id: String? = null,
    val user_name: String? = null,
    val user_avatar: String? = null,
    val user_role: String? = null,
    val replies: List<DiscussionReply> = emptyList(),
    val created_at: String,
)

@Serializable
data class DiscussionReply(
    val id: String,
    val discussion_id: String,
    val parent_id: String? = null,
    val user_id: String,
    val body: String,
    val upvotes: Int = 0,
    val is_instructor_reply: Boolean = false,
    val is_best_answer: Boolean = false,
    val user_name: String? = null,
    val user_avatar: String? = null,
    val user_role: String? = null,
    val created_at: String,
)

@Serializable
data class CreateDiscussionRequest(
    val course_id: String,
    val lesson_id: String? = null,
    val title: String,
    val body: String,
    val type: String = "question",
)

@Serializable
data class AddReplyRequest(val body: String, val parent_id: String? = null)

@Serializable
data class VoteRequest(val vote: Int = 1)

// ─── My Learning Models ───────────────────────────────────
@Serializable
data class EnrolledCourse(
    val enrollment_id: String,
    val enrolled_at: String,
    val progress_percentage: Double,
    val completed_lessons: Int,
    val total_lessons: Int,
    val is_completed: Boolean,
    val completed_at: String? = null,
    val last_accessed_at: String? = null,
    val last_lesson_id: String? = null,
    val last_lesson_position: Int = 0,
    val course_id: String,
    val slug: String,
    val title: String,
    val title_ar: String? = null,
    val thumbnail_url: String? = null,
    val total_duration: Int = 0,
    val level: String,
    val language: String,
    val has_certificate: Boolean = false,
    val instructor_name: String? = null,
    val last_lesson_title: String? = null,
)

@Serializable
data class EnrolledCourseDetail(
    val enrollment: EnrollmentInfo,
    val sections: List<CourseSection>,
    val certificate: Certificate? = null,
)

@Serializable
data class WishlistResponse(val wishlisted: Boolean)

// ─── Misc Models ──────────────────────────────────────────
@Serializable
data class AppConfig(
    val config: Map<String, String> = emptyMap(),
    val feature_flags: Map<String, Boolean> = emptyMap(),
)

@Serializable
data class Banner(
    val id: String,
    val title: String,
    val title_ar: String? = null,
    val subtitle: String? = null,
    val subtitle_ar: String? = null,
    val image_url: String? = null,
    val action_url: String? = null,
    val action_label: String? = null,
    val action_label_ar: String? = null,
    val sort_order: Int = 0,
)

@Serializable
data class Testimonial(
    val id: String,
    val name: String,
    val name_ar: String? = null,
    val headline: String? = null,
    val headline_ar: String? = null,
    val body: String,
    val body_ar: String? = null,
    val avatar_url: String? = null,
    val rating: Int = 5,
)

@Serializable
data class Faq(
    val id: String,
    val question: String,
    val question_ar: String? = null,
    val answer: String,
    val answer_ar: String? = null,
    val category: String? = null,
    val sort_order: Int = 0,
)

@Serializable
data class StaticPage(
    val slug: String,
    val title: String,
    val title_ar: String? = null,
    val content: String,
    val content_ar: String? = null,
    val updated_at: String,
)

@Serializable
data class HealthResponse(
    val status: String,
    val db: String,
    val latency_ms: Long,
    val timestamp: String,
)

@Serializable
data class AnalyticsEvent(
    val event_name: String,
    val session_id: String? = null,
    val properties: Map<String, String>? = null,
    val platform: String = "android",
    val app_version: String? = null,
)

@Serializable
data class BatchEventsRequest(val events: List<AnalyticsEvent>)

@Serializable
data class CreateTicketRequest(
    val subject: String,
    val message: String,
    val category: String? = null,
)

@Serializable
data class TicketResponse(
    val ticket_id: String,
    val ticket_number: String,
)

@Serializable
data class SupportTicket(
    val id: String,
    val ticket_number: String,
    val subject: String,
    val status: String,
    val priority: String,
    val created_at: String,
)

@Serializable
data class SupportTicketDetail(
    val id: String,
    val ticket_number: String,
    val subject: String,
    val status: String,
    val messages: List<SupportMessage> = emptyList(),
)

@Serializable
data class SupportMessage(
    val id: String,
    val is_staff: Boolean,
    val body: String,
    val sender_name: String? = null,
    val sender_avatar: String? = null,
    val created_at: String,
)

@Serializable
data class SendMessageRequest(val body: String)
