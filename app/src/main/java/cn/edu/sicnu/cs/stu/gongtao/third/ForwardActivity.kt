package cn.edu.sicnu.cs.stu.gongtao.third

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cs.stu.gongtao.third.databinding.ActivityForwardBinding

/**
 * 二级 Activity 1：显示主界面通过 Intent 正向传过来的参数。
 */
class ForwardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForwardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForwardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val received = intent.getStringExtra(getString(R.string.extra_key_forward))
            ?: getString(R.string.forward_no_extra)

        binding.tvReceived.text = received
        binding.tvReceivedLength.text = getString(R.string.forward_length_format, received.length)
        binding.btnBack.setOnClickListener { finish() }
    }
}
