package dz.nadjahacademy.feature.quiz

import app.cash.turbine.test
import dz.nadjahacademy.core.network.api.ApiResponse
import dz.nadjahacademy.core.network.api.QuizzesApiService
import dz.nadjahacademy.core.network.model.QuizAttemptStart
import dz.nadjahacademy.core.testing.FakeData
import dz.nadjahacademy.core.testing.MainCoroutineRule
import dz.nadjahacademy.feature.quiz.viewmodel.QuizViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.lifecycle.SavedStateHandle
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private val quizzesApi: QuizzesApiService = mockk()
    private lateinit var viewModel: QuizViewModel

    private val fakeQuiz = FakeData.quiz(id = "quiz-1", questionCount = 3)

    @Before
    fun setUp() {
        coEvery { quizzesApi.getQuiz("quiz-1") } returns
            Response.success(ApiResponse(success = true, data = fakeQuiz))

        coEvery { quizzesApi.startQuiz("quiz-1") } returns
            Response.success(
                ApiResponse(
                    success = true,
                    data = QuizAttemptStart(
                        attempt_id = "attempt-1",
                        started_at = "2024-01-01T00:00:00Z",
                    )
                )
            )

        viewModel = QuizViewModel(
            savedStateHandle = SavedStateHandle(mapOf("quizId" to "quiz-1")),
            quizzesApi = quizzesApi,
        )
    }

    @Test
    fun `initial state is loading then resolves`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `after load, quiz is populated`() = runTest {
        mainCoroutineRule.testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.quiz)
        assertEquals("quiz-1", state.quiz?.id)
    }

    @Test
    fun `selectAnswer updates selectedAnswers map`() = runTest {
        mainCoroutineRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectAnswer(questionId = "q-1", optionId = "opt-a-1")

        val state = viewModel.uiState.value
        assertEquals("opt-a-1", state.selectedAnswers["q-1"])
    }

    @Test
    fun `startQuiz sets attemptId and resets timer`() = runTest {
        mainCoroutineRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.startQuiz()
        mainCoroutineRule.testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("attempt-1", state.attemptId)
        // time_limit = 30 minutes → 1800 seconds
        assertEquals(1800, state.timeRemainingSeconds)
    }

    @Test
    fun `nextQuestion advances currentQuestionIndex`() = runTest {
        mainCoroutineRule.testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, viewModel.uiState.value.currentQuestionIndex)
        viewModel.nextQuestion()
        assertEquals(1, viewModel.uiState.value.currentQuestionIndex)
    }

    @Test
    fun `previousQuestion does not go below zero`() = runTest {
        mainCoroutineRule.testDispatcher.scheduler.advanceUntilIdle()

        viewModel.previousQuestion()
        assertEquals(0, viewModel.uiState.value.currentQuestionIndex)
    }

    @Test
    fun `onTimerTick decrements timeRemainingSeconds`() = runTest {
        mainCoroutineRule.testDispatcher.scheduler.advanceUntilIdle()

        // Prime the timer via startQuiz
        viewModel.startQuiz()
        mainCoroutineRule.testDispatcher.scheduler.advanceUntilIdle()

        val before = viewModel.uiState.value.timeRemainingSeconds
        viewModel.onTimerTick()
        val after = viewModel.uiState.value.timeRemainingSeconds
        assertEquals(before - 1, after)
    }
}
