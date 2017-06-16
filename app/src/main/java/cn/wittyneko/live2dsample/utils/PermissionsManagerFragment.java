package cn.wittyneko.live2dsample.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Android M 权限管理
 * Created by wittyneko on 2017/5/20.
 */

public class PermissionsManagerFragment extends Fragment {

    public static final String TAG = "PermissionsManagerFragment";
    public static int REQUEST_CODE = 10;
    private PermissionListener mListener;

    public static PermissionsManagerFragment getInstance(AppCompatActivity activity) {
        FragmentManager manager = activity.getSupportFragmentManager();
        PermissionsManagerFragment fragment = (PermissionsManagerFragment) manager.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new PermissionsManagerFragment();
            manager.beginTransaction()
                    .add(fragment, TAG)
                    .commitAllowingStateLoss();
            manager.executePendingTransactions();
        }
        return fragment;
    }

    public PermissionsManagerFragment request(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 这里忽略是否显示提示
            // shouldShowRequestPermissionRationale(permission);
            if (!check(permission)) {
                requestPermissions(new String[]{permission}, REQUEST_CODE);
            } else {
                if (mListener != null) {
                    mListener.granted(permission);
                }
            }
        } else {
            mListener.granted(permission);
        }
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean check(String permission) {

        return getContext().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissions.length != 0 && mListener != null) {
                    if (check(permissions[0])) {
                        mListener.granted(permissions[0]);
                    } else {
                        mListener.denied(permissions[0]);
                    }
                }
            }
        }
    }

    public interface PermissionListener {
        void granted(String permission);

        void denied(String permission);
    }

    public PermissionListener getListener() {
        return mListener;
    }

    public PermissionsManagerFragment setListener(PermissionListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public void showDialog(String msg) {
        if (TextUtils.isEmpty(msg)) {
            msg = "您未开启相关权限，是否前往设置启用。";
        }

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage(msg)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        switch (Build.BRAND.toUpperCase()) {
                            case "XIAOMI":
                                intent = getMiuiPermission();
                                break;
                            case "HONOR":
                            case "HUAWEI":
                                intent = getHuaweiPermission();
                                break;
                            case "MEIZU":
                                intent = getMeizuPermission();
                                break;
                            case "SAMSUNG":
                                intent = getAppSetting();
                                break;
                            default:
                                intent = getAppSetting();
                        }
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            intent = getAppSetting();
                            startActivity(intent);
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                })
                .show();
    }

    // see: http://blog.csdn.net/vinomvp/article/details/52228377
    /**
     * 应用详情设置
     *
     * @return
     */
    public Intent getAppSetting() {
        Context context = getContext();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        return intent;
    }

    /**
     * Miui 权限管理
     *
     * @return
     */
    public Intent getMiuiPermission() {
        // MIUI 版本对照 3 - v5 ,4 - v6 , 5 - v7 ,6 - v8
        String versionCode = getSystemProperty("ro.miui.ui.version.code");
        String versionName = getSystemProperty("ro.miui.ui.version.name");
        //Log.e("TAG", "-> " + versionCode + ", " + versionName);
        Intent intent;
        intent = new Intent("miui.intent.action.APP_PERM_EDITOR_PRIVATE");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", getContext().getPackageName());
        if (!TextUtils.isEmpty(versionCode)) {
            int code = Integer.valueOf(versionCode);
            if (code < 6) {
                intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
            } else {
                intent.setAction("miui.intent.action.APP_PERM_EDITOR_PRIVATE");
                intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            }
        }
        return intent;
    }

    /**
     * 华为权限管理
     *
     * @return
     */
    public Intent getHuaweiPermission() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
        return intent;
    }

    /**
     * 魅族权限管理
     *
     * @return
     */
    public Intent getMeizuPermission() {

        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", getContext().getPackageName());
        return intent;
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        return line;
    }
}
