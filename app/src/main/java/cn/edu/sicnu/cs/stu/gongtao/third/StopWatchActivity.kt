package cn.edu.sicnu.cs.stu.gongtao.third

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cs.stu.gongtao.third.databinding.ActivityStopwatchBinding
import cn.edu.sicnu.cs.stu.gongtao.third.stopwatch.StopWatch

/**
 * 二级 Activity 4：秒表。
 *
 * 计时逻辑放在 StopWatch（纯 Kotlin，可单元测试）里，本类只负责
 *  - 用 Handler 定时刷新显示；
 *  - 在 onSaveInstanceState 里保存快照、在 onCreate 里恢复快照，
 *    从而保证横竖屏切换（Activity 重建）时计时连续、不归零。
 */
class StopWatchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStopwatchBinding

    private val handler = Handler(Looper.getMainLooper())
    private var stopWatch = StopWatch()

    private val ticker = object : Runnable {
        override fun run() {
            renderTime(stopWatch.elapsedMs())
            if (stopWatch.isRunning) {
                handler.postDelayed(this, TICK_INTERVAL_MS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStopwatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 横竖屏切换导致 Activity 重建：用保存的快照恢复计时状态
        savedInstanceState?.let { state ->
            stopWatch = StopWatch().apply {
                restore(
                    StopWatch.Snapshot(
                        isRunning = state.getBoolean(STATE_RUNNING),
                        accumulatedMs = state.getLong(STATE_ACCUMULATED),
                        startedAtElapsed = state.getLong(STATE_STARTED_AT)
                    )
                )
            }
        }

        binding.btnToggle.setOnClickListener { toggle() }
        binding.btnReset.setOnClickListener { reset() }

        renderTime(stopWatch.elapsedMs())
        renderToggleButton()
        binding.tvStatus.text = getString(R.string.stopwatch_hint)
    }

    override fun onResume() {
        super.onResume()
        if (stopWatch.isRunning) {
            startTicker()
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(ticker)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val snapshot = stopWatch.snapshot()
        outState.putBoolean(STATE_RUNNING, snapshot.isRunning)
        outState.putLong(STATE_ACCUMULATED, snapshot.accumulatedMs)
        outState.putLong(STATE_STARTED_AT, snapshot.startedAtElapsed)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(ticker)
    }

    private fun startTicker() {
        handler.removeCallbacks(ticker)
        handler.post(ticker)
    }

    private fun toggle() {
        if (stopWatch.isRunning) {
            stopWatch.pause()
            handler.removeCallbacks(ticker)
        } else {
            stopWatch.start()
            startTicker()
        }
        renderToggleButton()
        renderTime(stopWatch.elapsedMs())
    }

    private fun reset() {
        handler.removeCallbacks(ticker)
        stopWatch.reset()
        renderTime(0L)
        renderToggleButton()
    }

    /** 把毫秒渲染成 分:秒.厘秒 */
    private fun renderTime(elapsedMs: Long) {
        val totalSeconds = elapsedMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val centiseconds = (elapsedMs % 1000) / 10
        binding.tvTime.text = getString(
            R.string.stopwatch_time_format,
            minutes,
            seconds,
            centiseconds
        )
    }

    private fun renderToggleButton() {
        binding.btnToggle.text = getString(
            if (stopWatch.isRunning) R.string.btn_pause else R.string.btn_start
        )
    }

    private companion object {
        const val STATE_RUNNING = "state_running"
        const val STATE_ACCUMULATED = "state_accumulated"
        const val STATE_STARTED_AT = "state_started_at"
        const val TICK_INTERVAL_MS = 31L
    }
}
