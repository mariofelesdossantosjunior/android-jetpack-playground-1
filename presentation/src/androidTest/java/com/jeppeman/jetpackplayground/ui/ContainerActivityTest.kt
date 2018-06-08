package com.jeppeman.jetpackplayground.ui

import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.jeppeman.jetpackplayground.R
import com.jeppeman.jetpackplayground.di.FakeVideoRepository
import com.jeppeman.jetpackplayground.espresso.isVisibleToUser
import com.jeppeman.jetpackplayground.ui.base.BaseViewHolder
import com.jeppeman.jetpackplayground.ui.videodetail.VideoDetailFragmentArgs
import kotlinx.android.synthetic.main.activity_container.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@MediumTest
@RunWith(AndroidJUnit4::class)
class ContainerActivityIntegrationTest {

    @Test
    fun clickVideoElement_ShouldNavigateToVideoDetail() {
        val latch = CountDownLatch(2)
        ActivityScenario.launch(ContainerActivity::class.java).onActivity { activity ->
            val destinations = listOf(R.id.videoListFragment, R.id.videoDetailFragment)
            activity.navHostFragment
                    .findNavController()
                    .addOnDestinationChangedListener { _, destination, arguments ->
                        assertThat(destination.id).isIn(destinations)
                        if (destination.id == R.id.videoDetailFragment) {
                            assertThat(
                                    VideoDetailFragmentArgs.fromBundle(arguments)
                                            .videoDetailParameter.videoModel.title
                            ).isEqualTo(FakeVideoRepository.videos[0].title)
                        }
                        latch.countDown()
                    }
        }

        onView(withId(R.id.videoList))
                .check(matches(isVisibleToUser()))
                .perform(scrollToPosition<BaseViewHolder>(0))
        onView(withText(FakeVideoRepository.videos[0].title))
                .check(matches(isVisibleToUser()))
                .perform(click())
        latch.await(5, TimeUnit.SECONDS)
    }
}

@LargeTest
@RunWith(AndroidJUnit4::class)
class ContainerActivityE2ETest {

    @Test
    fun clickVideoElement_ShouldMoveFromVideoListAndDisplayVideoDetail() {
        ActivityScenario.launch(ContainerActivity::class.java)

        onView(withId(R.id.videoList))
                .check(matches(isVisibleToUser()))
                .perform(scrollToPosition<BaseViewHolder>(0))
        onView(withText(FakeVideoRepository.videos[0].title))
                .check(matches(isVisibleToUser()))
                .perform(click())
        onView(withId(R.id.videoList)).check(doesNotExist())
        onView(withId(R.id.mainAction)).check(matches(isVisibleToUser()))
    }
}
