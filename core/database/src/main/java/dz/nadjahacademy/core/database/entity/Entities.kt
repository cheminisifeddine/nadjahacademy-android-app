package dz.nadjahacademy.core.database.entity

import androidx.room.*

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val title: String,
    val shortDescription: String?,
    val thumbnail: String?,
    val price: Double?,
    val rating: Double?,
    val enrollmentCount: Int?,
    val instructorName: String?,
    val instructorAvatar: String?,
    val level: String?,
    val durationHours: Int?,
    val categoryId: String?,
    val isFeatured: Boolean = false,
    val isPopular: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "enrollments")
data class EnrollmentEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val courseTitle: String?,
    val courseThumbnail: String?,
    val progressPercentage: Int?,
    val lastAccessedAt: Long?,
    val completedAt: Long?,
    val enrolledAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val sectionId: String?,
    val title: String,
    val type: String?,
    val videoUrl: String?,
    val duration: Int?,
    val order: Int?,
    val isCompleted: Boolean = false,
    val progressSeconds: Int = 0,
    val isDownloaded: Boolean = false,
    val localPath: String? = null,
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val courseId: String,
    val content: String,
    val timestampSeconds: Int?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String?,
    val isRead: Boolean = false,
    val data: String?,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String,
    val searchedAt: Long = System.currentTimeMillis(),
)
