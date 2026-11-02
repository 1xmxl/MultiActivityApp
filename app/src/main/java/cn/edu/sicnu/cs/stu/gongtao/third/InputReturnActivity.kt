package cn.edu.sicnu.cs.stu.gongtao.third

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cs.stu.gongtao.third.databinding.ActivityInputReturnBinding

/**
 * 二级 Activity 2：输入内容后点"确定"，把内容放进 Intent 作为结果返回给主界面。
 */
class InputReturnActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputReturnBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputReturnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirm.setOnClickListener { confirm() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun confirm() {
        val text = binding.etInput.text.toString().trim()
        if (text.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_input_required), Toast.LENGTH_SHORT).show()
            return
        }

        // 用 setResult 把数据带回给调用者（主界面）
        val data = Intent().putExtra(getString(R.string.extra_key_return), text)
        setResult(RESULT_OK, data)
        finish()
    }
}
