package cn.wittyneko.live2dsample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facepp.library.util.ConUtil;
import com.megvii.facepp.sdk.Facepp;

/**
 * Created by wittyneko on 2017/5/14.
 */
public class FaceppActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 授权认证
     */
    private void authFacepp() {
        if (Facepp.getSDKAuthType(ConUtil.getFileContent(this, R.raw
                .megviifacepp_0_4_7_model)) == 2) {// 非联网授权
            Toast.makeText(this, "Face++授权成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Face++授权失败", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
