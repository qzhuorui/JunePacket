package com.choryan.junepacket

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.choryan.junepacket.activity.ActivityExoPlayerOne
import com.choryan.junepacket.activity.ActivityExoPlayerTwo
import com.choryan.junepacket.interf.PermissionInterface
import com.choryan.junepacket.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main), PermissionInterface {

    private val permissionHelper by lazy {
        PermissionHelper(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClick()
    }

    private fun initClick() {
        tv_exoplayer_one.setOnClickListener {
            startActivity(Intent(this, ActivityExoPlayerOne::class.java))
        }
        tv_exoplayer_two.setOnClickListener {
            startActivity(Intent(this, ActivityExoPlayerTwo::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        permissionGet()
    }

    private fun permissionGet() {
        permissionHelper.requestPermission(
            Manifest.permission.CAMERA,
            PermissionHelper.REQUEST_CAMERA_PERMISSION
        );
        permissionHelper.requestPermission(
            Manifest.permission.RECORD_AUDIO,
            PermissionHelper.REQUEST_RECORD_AUDIO
        );
        permissionHelper.requestPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            PermissionHelper.REQUEST_READ_STORAGE_PERMISSION
        );
        permissionHelper.requestPermission(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            PermissionHelper.REQUEST_WRITE_STORAGE_PERMISSION
        );
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissionHelper.requestPermissionResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun requestPermissionSuccess(callBackCode: Int) {

    }

    override fun requestPermissionFail(callBackCode: Int) {
        Log.d(TAG, "requestPermissionFail: $callBackCode")
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}