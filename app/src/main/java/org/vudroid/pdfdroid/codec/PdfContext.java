package org.vudroid.pdfdroid.codec;

import android.content.ContentResolver;

/**
 * PDF上下文
 * Created by wangyn on 15/12/30.
 */
public class PdfContext {
    static
    {
        VuDroidLibraryLoader.load();
    }

    public PdfDocument openDocument(String fileName)
    {
        return PdfDocument.openDocument(fileName, "");
    }

    public void setContentResolver(ContentResolver contentResolver)
    {
    }

    public void recycle() {
    }
}
