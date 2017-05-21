package cn.wittyneko.live2dsample.utils;

import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
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
                if (mListener != null) {
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
}
