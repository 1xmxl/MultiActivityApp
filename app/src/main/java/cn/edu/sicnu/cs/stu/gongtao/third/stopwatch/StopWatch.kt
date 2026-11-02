package cn.edu.sicnu.cs.stu.gongtao.third.stopwatch

import android.os.SystemClock

/**
 * 秒表的核心计时逻辑（与界面无关，便于单元测试）。
 *
 * 计时原理：记录"本次开始计时的时刻"和"此前已累计的时间"，
 * 任意时刻的真实经过时间 = 累计时间 + (当前时刻 - 本次开始时刻)。
 * 因此秒表不依赖界面刷新次数，即使 Activity 因横竖屏切换而重建，
 * 只要保存并在重建后恢复 Snapshot，时间就能连续、不归零。
 *
 * 构造参数 clock 是可注入的时间源：默认使用 SystemClock.elapsedRealtime()
 * （单调时钟，不受系统时间被修改的影响），单元测试时可以传入假时钟。
 */
class StopWatch(private val clock: () -> Long = { SystemClock.elapsedRealtime() }) {

    /** 用于横竖屏重建时保存 / 恢复的状态快照 */
    data class Snapshot(
        val isRunning: Boolean,
        val accumulatedMs: Long,
        val startedAtElapsed: Long
    )

    var isRunning: Boolean = false
        private set

    private var accumulatedMs: Long = 0L
    private var startedAtElapsed: Long = 0L

    /** 开始（或继续）计时 */
    fun start() {
        if (isRunning) {
            return
        }
        startedAtElapsed = clock()
        isRunning = true
    }

    /** 暂停：把这一段的时间累加进累计值 */
    fun pause() {
        if (!isRunning) {
            return
        }
        accumulatedMs += clock() - startedAtElapsed
        isRunning = false
    }

    /** 重置为 0 */
    fun reset() {
        isRunning = false
        accumulatedMs = 0L
        startedAtElapsed = 0L
    }

    /** 当前已经经过的毫秒数 */
    fun elapsedMs(): Long {
        return if (isRunning) {
            accumulatedMs + (clock() - startedAtElapsed)
        } else {
            accumulatedMs
        }
    }

    fun snapshot(): Snapshot = Snapshot(isRunning, accumulatedMs, startedAtElapsed)

    fun restore(snapshot: Snapshot) {
        isRunning = snapshot.isRunning
        accumulatedMs = snapshot.accumulatedMs
        startedAtElapsed = snapshot.startedAtElapsed
    }
}
