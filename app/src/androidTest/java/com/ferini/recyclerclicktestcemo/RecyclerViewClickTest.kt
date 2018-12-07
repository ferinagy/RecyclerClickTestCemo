package com.ferini.recyclerclicktestcemo

import android.util.Log
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class RecyclerViewClickTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    val repeatRule = RepeatRule()

    @Before
    fun before() {
        IdlingRegistry.getInstance().register(MainActivity.idlingResource)
    }

    @After
    fun after() {
        IdlingRegistry.getInstance().unregister(MainActivity.idlingResource)
    }

    @Test
    @RepeatTest(150)
    fun clickOnRegularItem() {
        Espresso.onView(ViewMatchers.withText("Item #0")).perform(ViewActions.click())
//        Espresso.onView(ViewMatchers.withId(R.id.recycler)).perform(
//            RecyclerViewActions.actionOnItem<Adapter.RegularHolder>(
//                ViewMatchers.hasDescendant(ViewMatchers.withText("Item #0")),
//                ViewActions.click()
//            )
//        )

        Espresso.onView(ViewMatchers.withId(R.id.checking_text)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("regular_action_0")
            )
        )
    }

    @Test
    @RepeatTest(150)
    fun clickOnSpecialItem() {
        Espresso.onView(ViewMatchers.withText("special item")).perform(ViewActions.click())
//        Espresso.onView(ViewMatchers.withId(R.id.recycler)).perform(
//            RecyclerViewActions.actionOnHolderItem(
//                Matchers.instanceOf(Adapter.SpecialHolder::class.java),
//                ViewActions.click()
//            )
//        )

        Espresso.onView(ViewMatchers.withId(R.id.checking_text)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("special_action")
            )
        )
    }

    @Test
    @RepeatTest(150)
    fun clickOnSpecialItemButton() {
//        Espresso.onView(ViewMatchers.withId(R.id.recycler)).perform(
//            RecyclerViewActions.scrollToHolder(
//                Matchers.instanceOf(Adapter.SpecialHolder::class.java)
//            )
//        )
        Espresso.onView(ViewMatchers.withId(R.id.special_button)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.checking_text)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("special_button_action")
            )
        )
    }
}

class RepeatRule : TestRule {

    private class RepeatStatement(
        private val statement: Statement,
        private val repeat: Int,
        val name: String
    ) : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            for (i in 0 until repeat) {
                Log.i("Testing", "starting $name $i/$repeat")
                statement.evaluate()
                Log.i("Testing", "finished $name $i/$repeat")
            }
        }
    }

    override fun apply(statement: Statement, description: Description): Statement {
        var result = statement
        val repeat = description.getAnnotation(RepeatTest::class.java)
        if (repeat != null) {
            val times = repeat.value
            result = RepeatStatement(statement, times, description.methodName)
        }
        return result
    }
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
annotation class RepeatTest(val value: Int = 1)
