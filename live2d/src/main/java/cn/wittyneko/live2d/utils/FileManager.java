/**
 * You can modify and use this source freely
 * only for the development of application related Live2D.
 * <p>
 * (c) Live2D Inc. All rights reserved.
 */
package cn.wittyneko.live2d.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

/**
 * 文件加载管理
 */
public class FileManager {
    static Context context;


    public static void init(Context c) {
        context = c;
    }


    public static boolean existsAssets(String path) {
        try {
            InputStream afd = context.getAssets().open(path);
            afd.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public static InputStream openAssets(String path) throws IOException {
        return context.getAssets().open(path);
    }

    public static AssetFileDescriptor openAssetsFD(String path) throws IOException {
        return context.getAssets().openFd(path);
    }


    public static boolean existsCache(String path) {
        File f = new File(context.getExternalCacheDir(), path);
        return f.exists();
    }


    public static InputStream openCache(String path) throws FileNotFoundException {
        File f = new File(context.getExternalCacheDir(), path);
        return new FileInputStream(f);
    }


    public static boolean existsFile(String path) {
        File f = new File(context.getExternalFilesDir(null), path);
        return f.exists();
    }


    public static InputStream openFile(String path) throws FileNotFoundException {
        File f = new File(context.getExternalFilesDir(null), path);
        return new FileInputStream(f);
    }


    public static InputStream open(String path, boolean isAssets) throws IOException {
        if (isAssets) {
            return openAssets(path);
        } else {
            return openFile(path);
        }

    }


    public static InputStream open(String path) throws IOException {
        return open(path, false);
    }
}
