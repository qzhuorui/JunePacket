package com.choryan.junepacket.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import com.choryan.junepacket.interf.PermissionInterface;

/**
 * @ProjectName: SevenPlayer
 * @Package: com.qzr.sevenplayer.utils
 * @ClassName: PermissionHelper
 * @Description:
 * @Author: qzhuorui
 * @CreateDate: 2020/7/4 10:06
 */
public class PermissionHelper {
    private Activity mActivity;
    private PermissionInterface mPermissionInterface;

    public PermissionHelper(Activity mActivity, PermissionInterface mPermissionInterface) {
        this.mActivity = mActivity;
        this.mPermissionInterface = mPermissionInterface;
    }

    public static boolean hasPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void requestPermission(String permission, int callBackCode) {
        if (hasPermission(mActivity, permission)) {
            mPermissionInterface.requestPermissionSuccess(callBackCode);
        } else {
            ActivityCompat.requestPermissions(mActivity, new String[]{permission}, callBackCode);
        }
    }

    //接管结果
    public void requestPermissionResult(int requestCode, String[] permission, int[] grantResults) {
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_GRANTED) {
                mPermissionInterface.requestPermissionSuccess(requestCode);
            } else {
                mPermissionInterface.requestPermissionFail(requestCode);
            }
        }
    }

    public static int REQUEST_CAMERA_PERMISSION = 100;
    public static int REQUEST_READ_STORAGE_PERMISSION = 100 + 1;
    public static int REQUEST_WRITE_STORAGE_PERMISSION = 100 + 2;
    public static int REQUEST_RECORD_AUDIO = 100 + 3;

}
