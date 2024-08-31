package com.nowni.racetracker

import com.nowni.racetracker.ui.RaceParticipant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RaceParticipantTest {
    private val raceParticipant = RaceParticipant(
        name = "Test",
        maxProgress = 100,
        progressDelayMillis = 500L,
        initialProgress = 0,
        progressIncrement = 1
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun raceParticipant_RaceStarted_ProgressUpdated() = runTest {
        val expectedProgress = 1
        launch {
            raceParticipant.run()
        }
        advanceTimeBy(raceParticipant.progressDelayMillis)
        runCurrent()
        assertEquals(expectedProgress, raceParticipant.currentProgress)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun raceParticipant_RaceFinished_ProgressUpdated() = runTest {
        launch {
            raceParticipant.run()
        }
        advanceTimeBy(raceParticipant.maxProgress * raceParticipant.progressDelayMillis)
        runCurrent()
        assertEquals(100, raceParticipant.currentProgress)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun raceParticipant_RacePaused_ProgressUpdated() = runTest {
        val expectedProgress = 5
        val racerJob = launch {
            raceParticipant.run()
        }
        advanceTimeBy(expectedProgress * raceParticipant.progressDelayMillis)
        runCurrent()
        racerJob.cancelAndJoin()
        assertEquals(expectedProgress, raceParticipant.currentProgress)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun raceParticipant_RacePausedAndResumed_ProgressUpdated() = runTest {
        val expectedProgress = 5
        repeat(2) {
            val raceJob = launch {
                raceParticipant.run()
            }
            advanceTimeBy(expectedProgress * raceParticipant.progressDelayMillis)
            runCurrent()
            raceJob.cancelAndJoin()
        }
        assertEquals(expectedProgress * 2, raceParticipant.currentProgress)
    }

    @Test(expected = IllegalArgumentException::class)
    fun raceParticipant_ProgressIncrementZero_ExceptionThrown() {
        RaceParticipant(name = "Progress Test", progressIncrement = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun raceParticipant_MaxProgressZero_ExceptionThrown() {
        RaceParticipant(name = "Progress Test", maxProgress = 0)
    }

}