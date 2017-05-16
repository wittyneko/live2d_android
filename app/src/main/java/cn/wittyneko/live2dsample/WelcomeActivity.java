package cn.wittyneko.live2dsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;

/**
 * Created by wittytutu on 17-5-16.
 */

public class WelcomeActivity extends AppCompatActivity {
    private String MODEL_SUFFIX = ".model.json";
    private String[] BG_SUFFIX = new String[]{".jpg", ".png", ".gif", ".webp"};

    private LinearLayout scrollList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        searchModelList();
        searchBgList();

        scrollList = (LinearLayout) findViewById(R.id.scroll_list);

        // 遍历搜索资源
        Button btnSearch = new Button(this);
        btnSearch.setText("Search Model");
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchModelList();
                searchBgList();
            }
        });
        scrollList.addView(btnSearch);

        // 切换示例
        Button btnChange =  new Button(this);
        btnChange.setText("Change Model");
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, ChangeActivity.class);
                startActivity(intent);
            }
        });
        scrollList.addView(btnChange);

        // 动作参数手动调节
        Button btnCustom = new Button(this);
        btnCustom.setText("Custom Param");
        btnCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, CustomParamActivity.class);
                startActivity(intent);
            }
        });
        scrollList.addView(btnCustom);

        // 头部关键点识别
        Button btnFace = new Button(this);
        btnFace.setText("Face++");
        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, FaceppActivity.class);
                startActivity(intent);
            }
        });
        scrollList.addView(btnFace);
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
     * 遍历查找模型
     */
    public void searchModelList() {
        ModelInfo.modelPath.clear();
        searchModelList(getExternalFilesDirs("live2d"));
    }

    public void searchModelList(File[] files) {
        for (File file : files) {
            if (file.isFile()) {
                if (file.getName().endsWith(MODEL_SUFFIX)) {
                    // 添加模型
                    String path = file.getPath();
                    String parent = file.getParent();
                    String absPath = file.getAbsolutePath();
                    String name = file.getName();
                    String modelPath = path.substring(getExternalFilesDir(null).getPath().length());
                    ModelInfo.modelPath.add(modelPath);
                    Log.e("path", modelPath);
                }
            } else if (file.isDirectory()) {
                searchModelList(file.listFiles());
            }
        }
    }

    /**
     * 遍历查找背景
     */
    public void searchBgList() {
        ModelInfo.bgPath.clear();
        searchBgList(getExternalFilesDirs("image"));
    }

    public void searchBgList(File[] files) {
        for (File file : files) {
            if (file.isFile()) {
                for (String suffix : BG_SUFFIX) {
                    if (file.getName().endsWith(suffix)) {
                        // 添加背景
                        String path = file.getPath();
                        String parent = file.getParent();
                        String absPath = file.getAbsolutePath();
                        String name = file.getName();
                        String modelPath = path.substring(getExternalFilesDir(null).getPath().length());
                        ModelInfo.bgPath.add(modelPath);
                        Log.e("bgPath", modelPath);
                        break;
                    }
                }
            } else if (file.isDirectory()) {
                searchBgList(file.listFiles());
            }
        }
    }
}
