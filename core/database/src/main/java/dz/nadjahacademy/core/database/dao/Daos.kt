package dz.nadjahacademy.core.database.dao

import androidx.room.*
import dz.nadjahacademy.core.database.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY updatedAt DESC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE isFeatured = 1")
    fun getFeaturedCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE isPopular = 1")
    fun getPopularCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourse(courseId: String): CourseEntity?

    @Upsert
    suspend fun upsertCourses(courses: List<CourseEntity>)

    @Upsert
    suspend fun upsertCourse(course: CourseEntity)

    @Query("DELETE FROM courses")
    suspend fun clearAll()
}

@Dao
interface EnrollmentDao {
    @Query("SELECT * FROM enrollments ORDER BY lastAccessedAt DESC")
    fun getEnrollments(): Flow<List<EnrollmentEntity>>

    @Query("SELECT * FROM enrollments WHERE courseId = :courseId LIMIT 1")
    suspend fun getEnrollment(courseId: String): EnrollmentEntity?

    @Upsert
    suspend fun upsertEnrollments(enrollments: List<EnrollmentEntity>)

    @Upsert
    suspend fun upsertEnrollment(enrollment: EnrollmentEntity)

    @Query("UPDATE enrollments SET progressPercentage = :progress, lastAccessedAt = :accessedAt WHERE courseId = :courseId")
    suspend fun updateProgress(courseId: String, progress: Int, accessedAt: Long)
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE courseId = :courseId ORDER BY `order` ASC")
    fun getLessonsForCourse(courseId: String): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :lessonId")
    suspend fun getLesson(lessonId: String): LessonEntity?

    @Upsert
    suspend fun upsertLessons(lessons: List<LessonEntity>)

    @Upsert
    suspend fun upsertLesson(lesson: LessonEntity)

    @Query("UPDATE lessons SET isCompleted = 1 WHERE id = :lessonId")
    suspend fun markCompleted(lessonId: String)

    @Query("UPDATE lessons SET progressSeconds = :seconds WHERE id = :lessonId")
    suspend fun updateProgress(lessonId: String, seconds: Int)

    @Query("SELECT * FROM lessons WHERE isDownloaded = 1")
    fun getDownloadedLessons(): Flow<List<LessonEntity>>
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE lessonId = :lessonId ORDER BY createdAt DESC")
    fun getNotesForLesson(lessonId: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE courseId = :courseId ORDER BY createdAt DESC")
    fun getNotesForCourse(courseId: String): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Upsert
    suspend fun upsertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC LIMIT 20")
    fun getHistory(): Flow<List<SearchHistoryEntity>>

    @Upsert
    suspend fun upsert(entry: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE query = :query")
    suspend fun delete(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearAll()
}
