package cn.edu.sicnu.cs.stu.gongtao.third

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 秒表页面的 UI 测试（Espresso）。
 * 需要连接真机或模拟器：./gradlew connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class StopWatchActivityTest {

    @Test
    fun 初始时间为零且按钮显示开始() {
        ActivityScenario.launch(StopWatchActivity::class.java).use {
            onView(withId(R.id.tvTime)).check(matches(withText(ZERO_TIME)))
            onView(withId(R.id.btnToggle)).check(matches(withText(R.string.btn_start)))
        }
    }

    @Test
    fun 点击开始后按钮变成暂停() {
        ActivityScenario.launch(StopWatchActivity::class.java).use {
            onView(withId(R.id.btnToggle)).perform(click())
            onView(withId(R.id.btnToggle)).check(matches(withText(R.string.btn_pause)))
        }
    }

    @Test
    fun 暂停后按钮恢复为开始() {
        ActivityScenario.launch(StopWatchActivity::class.java).use {
            onView(withId(R.id.btnToggle)).perform(click())
            onView(withId(R.id.btnToggle)).perform(click())
            onView(withId(R.id.btnToggle)).check(matches(withText(R.string.btn_start)))
        }
    }

    @Test
    fun 重置后时间回到零() {
        ActivityScenario.launch(StopWatchActivity::class.java).use {
            onView(withId(R.id.btnToggle)).perform(click())
            onView(withId(R.id.btnReset)).perform(click())

            onView(withId(R.id.tvTime)).check(matches(withText(ZERO_TIME)))
            onView(withId(R.id.btnToggle)).check(matches(withText(R.string.btn_start)))
        }
    }

    private companion object {
        const val ZERO_TIME = "00:00.00"
    }
}
