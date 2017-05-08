package cn.wittyneko.live2d.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * 声音播放管理
 */
public class SoundManager {
    static private SoundPool soundPool;
    static private Context context;
    static private Map<String, Integer> soundList;
    private static MediaPlayer player;

    public static boolean playable = true;

    public static void init(Context c) {
        context = c;

        soundPool = new SoundPool(50, AudioManager.STREAM_MUSIC, 0);
        soundList = new HashMap<String, Integer>();
    }


    // 加载声音 文件
    public static void load(String path) {
        if (soundList.containsKey(path)) return;

        try {
            AssetFileDescriptor assetFileDescritorArticle = context.getAssets().openFd(path);
            int soundID = soundPool.load(assetFileDescritorArticle, 0);
            soundList.put(path, soundID);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    // 播放声音
    public static void play(String name) {
        try {
            AssetFileDescriptor descriptor = context.getAssets().openFd(name);
            File file = new File("");
            if (player != null){
                //player.stop();
                player.release();
            }

            // 是否开启声音
            if (playable){
                player = new MediaPlayer();
                player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                player.prepare();
                player.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (!soundList.containsKey(name)) return;
//
//		soundPool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
//        soundPool.play(soundList.get(name), 1f, 1f, 1, 0, 1);
    }

    public static void release() {
        soundList.clear();
        soundPool.release();
        if (player != null){
            player.release();
            player = null;
        }
    }

    // 获取声音列表
    public static String[] getSoundList(String path) {
        AssetManager assetManager = context.getAssets();
        ArrayList<String> list = new ArrayList<String>();
        getList(assetManager, list, path);
        String[] soundPaths = list.toArray(new String[list.size()]);

//        Log.e("soundPaths", "-> " + soundPaths.length);
//        for (int i = 0; i < soundPaths.length; i++) {
//            Log.e("sound", "-> " + path + "/" + soundPaths[i]);
//        }
        return soundPaths;
    }

    // 遍历Assets目录
    public static void getList(AssetManager assetManager, ArrayList<String> list, String path) {

        // TODO: 17-3-15 待优化，遍历速度慢
        try {
            String[] soundPaths = assetManager.list(path);
            if (soundPaths.length > 0) {
                //Log.e("sound", "-> " + soundPaths.length);
                for (int i = 0; i < soundPaths.length; i++) {
                    //Log.e("sound", "-> " + path + "/" + soundPaths[i]);
                    getList(assetManager, list, path + "/" + soundPaths[i]);
                }
            } else {
                // 过滤掉嘴型文件
                if (!path.endsWith(".vol")) {
                    list.add(path);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 读取嘴型文件
    public static String[] loadMouthOpen(String path) {
        String[] mouthArray = new String[0];
        try {
            InputStream is = context.getAssets().open(path);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader bfr = new BufferedReader(isr);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bfr.readLine()) != null) {
                stringBuilder.append(line);
            }
            String mouth = stringBuilder.toString();
            mouthArray = mouth.split(" ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mouthArray;
    }
}
