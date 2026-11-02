package cn.edu.sicnu.cs.stu.gongtao.third

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cs.stu.gongtao.third.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 主 Activity：提供 4 个入口，分别演示
 *  1）正向传参启动二级 Activity；
 *  2）二级 Activity 回传结果并在本页面显示；
 *  3）隐式 Intent 调用系统相机；
 *  4）秒表（横竖屏连续计时）。
 *
 * 同时把自身的生命周期回调记录在页面下方，便于观察 Activity 的生命周期。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /** 生命周期记录，最新的排在最上面 */
    private val lifecycleLog = StringBuilder()

    /** 第 2 个二级 Activity 的回调：接收它返回的内容 */
    private val inputReturnLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val returned = result.data
                ?.getStringExtra(getString(R.string.extra_key_return))
                ?: getString(R.string.result_empty)
            binding.tvReturnedValue.text = returned
            appendLog(getString(R.string.log_result_received, returned))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ① 正向传参：把一段文字通过 Intent 传给第 1 个二级 Activity
        binding.btnForward.setOnClickListener {
            val intent = Intent(this, ForwardActivity::class.java)
            intent.putExtra(
                getString(R.string.extra_key_forward),
                getString(R.string.forward_sample_text)
            )
            startActivity(intent)
            appendLog(getString(R.string.log_start_forward))
        }

        // ② 启动第 2 个二级 Activity，并接收它回传的内容
        binding.btnInputReturn.setOnClickListener {
            inputReturnLauncher.launch(Intent(this, InputReturnActivity::class.java))
            appendLog(getString(R.string.log_start_input))
        }

        // ③ 隐式 Intent 调用系统相机
        binding.btnCamera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
            appendLog(getString(R.string.log_start_camera))
        }

        // ④ 秒表
        binding.btnStopWatch.setOnClickListener {
            startActivity(Intent(this, StopWatchActivity::class.java))
            appendLog(getString(R.string.log_start_stopwatch))
        }

        binding.tvReturnedValue.text = getString(R.string.result_empty)
        binding.tvLifecycleLog.text = getString(R.string.lifecycle_log_placeholder)

        appendLog(getString(R.string.lifecycle_on_create))
    }

    override fun onStart() {
        super.onStart()
        appendLog(getString(R.string.lifecycle_on_start))
    }

    override fun onResume() {
        super.onResume()
        appendLog(getString(R.string.lifecycle_on_resume))
    }

    override fun onPause() {
        super.onPause()
        appendLog(getString(R.string.lifecycle_on_pause), refreshUi = false)
    }

    override fun onStop() {
        super.onStop()
        appendLog(getString(R.string.lifecycle_on_stop), refreshUi = false)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 界面即将销毁，只追加记录，不再刷新界面
        appendLog(getString(R.string.lifecycle_on_destroy), refreshUi = false)
    }

    /** 追加一条带时间戳的生命周期 / 操作记录 */
    private fun appendLog(event: String, refreshUi: Boolean = true) {
        val timePattern = getString(R.string.log_time_pattern)
        val time = SimpleDateFormat(timePattern, Locale.getDefault()).format(Date())
        lifecycleLog.insert(0, getString(R.string.log_line_format, time, event) + "\n")
        if (refreshUi) {
            binding.tvLifecycleLog.text = lifecycleLog.toString()
        }
    }
}
