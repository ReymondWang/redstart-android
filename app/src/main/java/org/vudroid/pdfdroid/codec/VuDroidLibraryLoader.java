package org.vudroid.pdfdroid.codec;

/**
 * 加载PDF相关的JNI类
 * Created by wangyn on 15/12/30.
 */
public class VuDroidLibraryLoader {
    private static boolean alreadyLoaded = false;

    public static void load()
    {
        if (alreadyLoaded)
        {
            return;
        }
        System.loadLibrary("vudroid");
        alreadyLoaded = true;
    }
}
