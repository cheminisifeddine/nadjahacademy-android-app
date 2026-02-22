package dz.nadjahacademy.core.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsTracker @Inject constructor() {

    private val analytics: FirebaseAnalytics = Firebase.analytics

    fun trackScreenView(screenName: String, screenClass: String? = null) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            screenClass?.let { putString(FirebaseAnalytics.Param.SCREEN_CLASS, it) }
        })
    }

    fun trackCourseView(courseId: String, courseName: String) {
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, courseId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, courseName)
        })
    }

    fun trackEnrollment(courseId: String, courseName: String, price: Double) {
        analytics.logEvent(FirebaseAnalytics.Event.PURCHASE, Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, courseId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, courseName)
            putDouble(FirebaseAnalytics.Param.VALUE, price)
            putString(FirebaseAnalytics.Param.CURRENCY, "DZD")
        })
    }

    fun trackLessonComplete(lessonId: String, courseId: String) {
        analytics.logEvent("lesson_complete", Bundle().apply {
            putString("lesson_id", lessonId)
            putString("course_id", courseId)
        })
    }

    fun trackSearch(query: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SEARCH, Bundle().apply {
            putString(FirebaseAnalytics.Param.SEARCH_TERM, query)
        })
    }

    fun trackLogin(method: String) {
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN, Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        })
    }
}
