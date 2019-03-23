package com.farhan.moviepocket

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.farhan.moviepocket.view.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
 class MainActivityUiTests {

    @Rule @JvmField
     var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
     fun testToolBar() {
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.toolbar_title)).check(matches(withText("Movie Pocket")))
        onView(withId(R.id.action_search)).check(matches(isDisplayed()))
    }
}
