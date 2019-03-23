package com.farhan.moviepocket

import android.support.annotation.NonNull
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.farhan.moviepocket.view.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.support.test.espresso.IdlingResource
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import com.farhan.moviepocket.adapter.MoviesAdapter
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Before
import org.junit.After
import android.support.v7.widget.RecyclerView
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.*
import android.view.View
import org.hamcrest.Matcher
import org.junit.runner.Description


@RunWith(AndroidJUnit4::class)
class MainActivityUnitTests {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    private var mIdlingResource: IdlingResource? = null

    // Registers any resource that needs to be synchronized with Espresso before the test is run.
    @Before
    fun registerIdlingResource() {
        mIdlingResource = mActivityTestRule.activity.getIdlingResource()
        // To prove that the test fails, omit this call:
        //Espresso.registerIdlingResources(mIdlingResource)
        IdlingRegistry.getInstance().register(mIdlingResource)
    }

    @Test
    fun testMovieAdapter(){
        onView(withId(R.id.rc_movie))
            .check(matches(atPosition(0, hasDescendant(withText("2017")))))

        onView(withId(R.id.rc_movie)).check(RecyclerViewItemCountAssertion(25))
    }

    // Remember to unregister resources when not needed to avoid malfunction.
    @After
    fun unregisterIdlingResource() {
        if (mIdlingResource != null) {
            //Espresso.unregisterIdlingResources(mIdlingResource)
            IdlingRegistry.getInstance().unregister(mIdlingResource)
        }
    }

    private fun atPosition(position: Int, @NonNull itemMatcher: Matcher<View>): Matcher<View> {
        checkNotNull(itemMatcher)
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: org.hamcrest.Description?) {
                description?.appendText("has item at position $position:")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(view: RecyclerView): Boolean {
                val viewHolder = view.findViewHolderForAdapterPosition(position)
                    ?: // has no item on such position
                    return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }
}