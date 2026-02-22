package dz.nadjahacademy.core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dz.nadjahacademy.core.database.NadjahDatabase
import dz.nadjahacademy.core.database.dao.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NadjahDatabase =
        Room.databaseBuilder(context, NadjahDatabase::class.java, "nadjah_academy.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideCourseDao(db: NadjahDatabase): CourseDao = db.courseDao()
    @Provides fun provideEnrollmentDao(db: NadjahDatabase): EnrollmentDao = db.enrollmentDao()
    @Provides fun provideLessonDao(db: NadjahDatabase): LessonDao = db.lessonDao()
    @Provides fun provideNoteDao(db: NadjahDatabase): NoteDao = db.noteDao()
    @Provides fun provideNotificationDao(db: NadjahDatabase): NotificationDao = db.notificationDao()
    @Provides fun provideSearchHistoryDao(db: NadjahDatabase): SearchHistoryDao = db.searchHistoryDao()
}
