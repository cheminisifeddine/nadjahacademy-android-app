package dz.nadjahacademy.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dz.nadjahacademy.core.database.dao.*
import dz.nadjahacademy.core.database.entity.*

@Database(
    entities = [
        CourseEntity::class,
        EnrollmentEntity::class,
        LessonEntity::class,
        NoteEntity::class,
        NotificationEntity::class,
        SearchHistoryEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class NadjahDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun enrollmentDao(): EnrollmentDao
    abstract fun lessonDao(): LessonDao
    abstract fun noteDao(): NoteDao
    abstract fun notificationDao(): NotificationDao
    abstract fun searchHistoryDao(): SearchHistoryDao
}
