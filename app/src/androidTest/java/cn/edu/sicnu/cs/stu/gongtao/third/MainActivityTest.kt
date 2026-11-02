package cn.edu.sicnu.cs.stu.gongtao.third

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 主界面的 UI 测试（Espresso）。
 * 需要连接真机或模拟器：./gradlew connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @Test
    fun 主界面显示四个入口与结果区域() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnForward)).check(matches(isDisplayed()))
            onView(withId(R.id.btnInputReturn)).check(matches(isDisplayed()))
            onView(withId(R.id.btnCamera)).check(matches(isDisplayed()))
            onView(withId(R.id.btnStopWatch)).check(matches(isDisplayed()))
            onView(withId(R.id.tvReturnedValue)).check(matches(withText(R.string.result_empty)))
        }
    }

    @Test
    fun 点击第一个按钮后二级页面显示传入的参数() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnForward)).perform(click())
            onView(withId(R.id.tvReceived)).check(matches(withText(R.string.forward_sample_text)))
        }
    }

    @Test
    fun 输入内容并确定后主界面显示返回结果() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnInputReturn)).perform(click())
            onView(withId(R.id.etInput)).perform(replaceText(RETURN_TEXT), closeSoftKeyboard())
            onView(withId(R.id.btnConfirm)).perform(click())

            onView(withId(R.id.tvReturnedValue)).check(matches(withText(RETURN_TEXT)))
        }
    }

    @Test
    fun 点击秒表按钮进入秒表页面() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnStopWatch)).perform(click())
            onView(withId(R.id.tvTime)).check(matches(isDisplayed()))
            onView(withId(R.id.btnToggle)).check(matches(withText(R.string.btn_start)))
        }
    }

    @Test
    fun 点击相机按钮进入拍照页面() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnCamera)).perform(click())
            onView(withId(R.id.btnTakePhoto)).check(matches(isDisplayed()))
        }
    }

    private companion object {
        const val RETURN_TEXT = "测试返回内容"
    }
}
