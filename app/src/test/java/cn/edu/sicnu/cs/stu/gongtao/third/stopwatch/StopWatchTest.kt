package cn.edu.sicnu.cs.stu.gongtao.third.stopwatch

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * StopWatch 的本地单元测试（不需要设备，执行 ./gradlew testDebugUnitTest）。
 * 用"假时钟"精确控制时间流逝，验证计时、暂停、继续、重置以及快照恢复。
 */
class StopWatchTest {

    /** 可控的假时钟 */
    private var nowMs = 1_000L
    private val clock: () -> Long = { nowMs }

    private fun advance(ms: Long) {
        nowMs += ms
    }

    @Test
    fun 初始状态为零且未运行() {
        val stopWatch = StopWatch(clock)
        assertFalse(stopWatch.isRunning)
        assertEquals(0L, stopWatch.elapsedMs())
    }

    @Test
    fun 开始后时间随时钟前进() {
        val stopWatch = StopWatch(clock)
        stopWatch.start()

        assertTrue(stopWatch.isRunning)
        advance(1_500)
        assertEquals(1_500L, stopWatch.elapsedMs())
    }

    @Test
    fun 暂停后时间冻结() {
        val stopWatch = StopWatch(clock)
        stopWatch.start()
        advance(2_000)
        stopWatch.pause()

        assertFalse(stopWatch.isRunning)
        val frozen = stopWatch.elapsedMs()
        advance(5_000)
        assertEquals(frozen, stopWatch.elapsedMs())
        assertEquals(2_000L, frozen)
    }

    @Test
    fun 继续计时会把两段时间累加() {
        val stopWatch = StopWatch(clock)
        stopWatch.start()
        advance(1_000)
        stopWatch.pause()

        advance(3_000)          // 暂停期间不计数
        stopWatch.start()
        advance(2_000)

        assertEquals(3_000L, stopWatch.elapsedMs())
    }

    @Test
    fun 重置后回到零且停止() {
        val stopWatch = StopWatch(clock)
        stopWatch.start()
        advance(4_000)
        stopWatch.reset()

        assertFalse(stopWatch.isRunning)
        assertEquals(0L, stopWatch.elapsedMs())
    }

    @Test
    fun 快照恢复后可以接着计时_模拟横竖屏重建() {
        val stopWatch = StopWatch(clock)
        stopWatch.start()
        advance(2_500)

        // 旋转屏幕：Activity 保存快照后重建，用新实例恢复
        val snapshot = stopWatch.snapshot()
        advance(1_000)          // 重建过程消耗的时间
        val recreated = StopWatch(clock).apply { restore(snapshot) }

        assertTrue(recreated.isRunning)
        // 时间连续：2.5 秒 + 重建期间 1 秒
        assertEquals(3_500L, recreated.elapsedMs())

        advance(500)
        assertEquals(4_000L, recreated.elapsedMs())
    }

    @Test
    fun 暂停状态下重建不会自己跑起来() {
        val stopWatch = StopWatch(clock)
        stopWatch.start()
        advance(1_200)
        stopWatch.pause()

        val recreated = StopWatch(clock).apply { restore(stopWatch.snapshot()) }
        advance(10_000)

        assertFalse(recreated.isRunning)
        assertEquals(1_200L, recreated.elapsedMs())
    }
}
