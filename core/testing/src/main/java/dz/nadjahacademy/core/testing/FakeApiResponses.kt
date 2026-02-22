package dz.nadjahacademy.core.testing

import dz.nadjahacademy.core.network.model.*

object FakeData {

    fun course(
        id: String = "course-1",
        title: String = "Test Course",
        slug: String = "test-course",
        price: Double = 0.0,
    ) = CourseDetail(
        id = id,
        slug = slug,
        title = title,
        price = price,
        level = "beginner",
        language = "en",
        description = "A test course description",
        total_lessons = 10,
        total_students = 100,
        average_rating = 4.5,
        total_reviews = 20,
        is_enrolled = false,
        sections = emptyList(),
        what_you_will_learn = emptyList(),
        requirements = emptyList(),
    )

    fun enrolledCourse(
        enrollmentId: String = "enroll-1",
        courseId: String = "course-1",
        courseTitle: String = "Test Course",
        progress: Double = 30.0,
    ) = EnrolledCourse(
        enrollment_id = enrollmentId,
        enrolled_at = "2024-01-01",
        progress_percentage = progress,
        completed_lessons = 3,
        total_lessons = 10,
        is_completed = progress >= 100.0,
        completed_at = null,
        last_accessed_at = null,
        last_lesson_id = null,
        course_id = courseId,
        slug = "test-course",
        title = courseTitle,
        thumbnail_url = null,
        level = "beginner",
        language = "en",
    )

    fun quiz(
        id: String = "quiz-1",
        title: String = "Test Quiz",
        timeLimit: Int = 30,
        questionCount: Int = 5,
    ) = QuizDetail(
        id = id,
        course_id = "course-1",
        lesson_id = "lesson-1",
        title = title,
        description = "A test quiz",
        time_limit = timeLimit,
        pass_percentage = 70,
        questions = (1..questionCount).map { i ->
            QuizQuestion(
                id = "q-$i",
                question = "Question $i?",
                type = "multiple_choice",
                sort_order = i,
                options = listOf(
                    QuizOption(id = "opt-a-$i", question_id = "q-$i", option_text = "Option A", sort_order = 1),
                    QuizOption(id = "opt-b-$i", question_id = "q-$i", option_text = "Option B", sort_order = 2),
                    QuizOption(id = "opt-c-$i", question_id = "q-$i", option_text = "Option C", sort_order = 3),
                ),
                points = 1,
                explanation = null,
            )
        },
    )

    fun notification(
        id: String = "notif-1",
        title: String = "Test Notification",
        isRead: Boolean = false,
        type: String = "general",
    ) = Notification(
        id = id,
        type = type,
        title = title,
        body = "Notification body",
        is_read = isRead,
        created_at = "2024-01-01T00:00:00Z",
        action_url = null,
        image_url = null,
    )
}
