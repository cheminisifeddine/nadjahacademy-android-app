package dz.nadjahacademy.core.network.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dz.nadjahacademy.core.network.api.*
import dz.nadjahacademy.core.network.interceptor.AuthInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// Base URL resolved from BuildConfig at runtime, fallback to live worker
private val BASE_URL: String
    get() = try {
        val clazz = Class.forName("dz.nadjahacademy.app.BuildConfig")
        (clazz.getField("BASE_URL").get(null) as? String)?.takeIf { it.isNotBlank() }
            ?: "https://nadjah-academy-api.medsaidkichene.workers.dev/api/v1/"
    } catch (e: Exception) {
        "https://nadjah-academy-api.medsaidkichene.workers.dev/api/v1/"
    }

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (isDebug()) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("X-App-Version", getAppVersion())
                    .addHeader("X-Platform", "android")
                    .build()
            )
        }
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides @Singleton
    fun provideCoursesApiService(retrofit: Retrofit): CoursesApiService =
        retrofit.create(CoursesApiService::class.java)

    @Provides @Singleton
    fun provideLessonsApiService(retrofit: Retrofit): LessonsApiService =
        retrofit.create(LessonsApiService::class.java)

    @Provides @Singleton
    fun provideQuizzesApiService(retrofit: Retrofit): QuizzesApiService =
        retrofit.create(QuizzesApiService::class.java)

    @Provides @Singleton
    fun provideCategoriesApiService(retrofit: Retrofit): CategoriesApiService =
        retrofit.create(CategoriesApiService::class.java)

    @Provides @Singleton
    fun provideInstructorsApiService(retrofit: Retrofit): InstructorsApiService =
        retrofit.create(InstructorsApiService::class.java)

    @Provides @Singleton
    fun provideBlogsApiService(retrofit: Retrofit): BlogsApiService =
        retrofit.create(BlogsApiService::class.java)

    @Provides @Singleton
    fun provideUsersApiService(retrofit: Retrofit): UsersApiService =
        retrofit.create(UsersApiService::class.java)

    @Provides @Singleton
    fun provideNotificationsApiService(retrofit: Retrofit): NotificationsApiService =
        retrofit.create(NotificationsApiService::class.java)

    @Provides @Singleton
    fun providePaymentsApiService(retrofit: Retrofit): PaymentsApiService =
        retrofit.create(PaymentsApiService::class.java)

    @Provides @Singleton
    fun provideSearchApiService(retrofit: Retrofit): SearchApiService =
        retrofit.create(SearchApiService::class.java)

    @Provides @Singleton
    fun provideDiscussionsApiService(retrofit: Retrofit): DiscussionsApiService =
        retrofit.create(DiscussionsApiService::class.java)

    @Provides @Singleton
    fun provideMyLearningApiService(retrofit: Retrofit): MyLearningApiService =
        retrofit.create(MyLearningApiService::class.java)

    @Provides @Singleton
    fun provideMiscApiService(retrofit: Retrofit): MiscApiService =
        retrofit.create(MiscApiService::class.java)

    @Provides @Singleton
    fun provideSupportApiService(retrofit: Retrofit): SupportApiService =
        retrofit.create(SupportApiService::class.java)

    private fun isDebug(): Boolean {
        return try {
            val clazz = Class.forName("dz.nadjahacademy.app.BuildConfig")
            clazz.getField("DEBUG").getBoolean(null)
        } catch (e: Exception) {
            false
        }
    }

    private fun getAppVersion(): String {
        return try {
            val clazz = Class.forName("dz.nadjahacademy.app.BuildConfig")
            clazz.getField("VERSION_NAME").get(null) as String
        } catch (e: Exception) {
            "1.0.0"
        }
    }
}
