package cn.edu.sicnu.cs.stu.gongtao.third

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import cn.edu.sicnu.cs.stu.gongtao.third.databinding.ActivityCameraBinding
import java.io.File
import java.io.IOException

/**
 * 二级 Activity 3：采用"隐式 Intent"调用系统相机拍照，拍完在本页面显示照片。
 *
 * 流程：
 *  1）构造 Intent(MediaStore.ACTION_IMAGE_CAPTURE)；
 *  2）用 FileProvider 生成一个 content:// 的 Uri 作为 EXTRA_OUTPUT，把照片写进应用的缓存目录；
 *  3）ActivityResultLauncher 接收拍照结果，从 Uri 解码出 Bitmap 显示到 ImageView。
 */
class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    /** 本次拍照保存的位置，拍照返回后从这里读取照片 */
    private var pendingPhotoUri: Uri? = null

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uri = pendingPhotoUri
        if (result.resultCode == RESULT_OK && uri != null) {
            showPhoto(uri)
            binding.tvCameraStatus.text = getString(R.string.camera_success)
        } else {
            binding.tvCameraStatus.text = getString(R.string.camera_cancelled)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvCameraStatus.text = getString(R.string.camera_hint)
        binding.btnTakePhoto.setOnClickListener { takePhoto() }

        // 横竖屏切换后恢复已经拍好的照片
        savedInstanceState?.getString(STATE_PHOTO_URI)?.let { saved ->
            val uri = Uri.parse(saved)
            pendingPhotoUri = uri
            showPhoto(uri)
            binding.tvCameraStatus.text = getString(R.string.camera_success)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_PHOTO_URI, pendingPhotoUri?.toString())
    }

    /** 用隐式 Intent 启动系统相机 */
    private fun takePhoto() {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // 没有能处理拍照请求的应用时给出提示
        if (captureIntent.resolveActivity(packageManager) == null) {
            binding.tvCameraStatus.text = getString(R.string.camera_not_available)
            return
        }

        try {
            val uri = createPhotoUri()
            pendingPhotoUri = uri
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            captureIntent.addFlags(
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            takePictureLauncher.launch(captureIntent)
        } catch (e: ActivityNotFoundException) {
            binding.tvCameraStatus.text = getString(R.string.camera_not_available)
        }
    }

    /** 在缓存目录里创建一个空文件，并通过 FileProvider 换成 content:// 的 Uri */
    private fun createPhotoUri(): Uri {
        val photoDir = File(cacheDir, getString(R.string.camera_photo_dir))
        if (!photoDir.exists()) {
            photoDir.mkdirs()
        }
        val photoFile = File.createTempFile(
            getString(R.string.camera_photo_prefix),
            getString(R.string.camera_photo_suffix),
            photoDir
        )
        val authority = packageName + getString(R.string.file_provider_suffix)
        return FileProvider.getUriForFile(this, authority, photoFile)
    }

    private fun showPhoto(uri: Uri) {
        val bitmap = decodeSampledBitmap(uri, MAX_PHOTO_SIZE)
        if (bitmap != null) {
            binding.ivPhoto.setImageBitmap(bitmap)
        } else {
            binding.tvCameraStatus.text = getString(R.string.camera_load_failed)
        }
    }

    /** 按需缩放解码，避免大图直接加载导致内存溢出 */
    private fun decodeSampledBitmap(uri: Uri, maxSize: Int): Bitmap? {
        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }

            val options = BitmapFactory.Options().apply {
                inSampleSize = calculateSampleSize(bounds, maxSize)
            }
            contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
        } catch (e: IOException) {
            null
        }
    }

    private fun calculateSampleSize(bounds: BitmapFactory.Options, maxSize: Int): Int {
        var sampleSize = 1
        var width = bounds.outWidth
        var height = bounds.outHeight
        while (width / 2 >= maxSize && height / 2 >= maxSize) {
            width /= 2
            height /= 2
            sampleSize *= 2
        }
        return sampleSize
    }

    private companion object {
        const val STATE_PHOTO_URI = "state_photo_uri"
        const val MAX_PHOTO_SIZE = 1024
    }
}
