package com.example.virtualdisplay

import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rikka.shizuku.Shizuku

class MainActivity : AppCompatActivity() {

    private var virtualDisplay: VirtualDisplay? = null
    private lateinit var tvStatus: TextView
    private lateinit var btnStart: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvStatus = findViewById(R.id.tvStatus)
        btnStart = findViewById(R.id.btnStartVirtualDisplay)

        // Shizuku权限监听
        Shizuku.addRequestPermissionResultListener { _, grantResult ->
            runOnUiThread {
                if (grantResult == 0) {
                    tvStatus.text = "Shizuku授权成功，可以启动虚拟屏"
                    btnStart.isEnabled = true
                } else {
                    tvStatus.text = "Shizuku授权被拒绝"
                }
            }
        }

        btnStart.setOnClickListener {
            if (Shizuku.isPreV11()) {
                Toast.makeText(this, "Shizuku版本过低", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (Shizuku.checkSelfPermission() == 0) {
                createVirtualDisplay()
            } else {
                Shizuku.requestPermission(0)
            }
        }

        if (Shizuku.isAvailable) {
            tvStatus.text = "Shizuku已连接，点击按钮申请权限"
        } else {
            tvStatus.text = "Shizuku未运行，请先打开Shizuku"
            btnStart.isEnabled = false
        }
    }

    // 创建虚拟显示屏
    private fun createVirtualDisplay() {
        val displayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager
        // 这里设置虚拟屏分辨率，你后面可以调这个来控制拉伸
        val width = 1920
        val height = 1080
        val dpi = 320

        virtualDisplay = displayManager.createVirtualDisplay(
            "GameContainer",
            width, height, dpi,
            null,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            null, null, null
        )
        tvStatus.text = "虚拟显示屏创建成功 ${width}x${height}"
    }

    override fun onDestroy() {
        super.onDestroy()
        virtualDisplay?.release()
        Shizuku.removeRequestPermissionResultListener { }
    }
}