package com.purplelight.redstar.provider.dao.impl;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.purplelight.redstar.provider.dao.IConfigurationDao;
import com.purplelight.redstar.provider.entity.Configuration;
import com.purplelight.redstar.provider.RedStarProviderMeta.ConfigurationMetaData;

/**
 * 配置类实现
 * Created by wangyn on 16/6/2.
 */
public class ConfigurationDaoImpl extends BaseDaoImpl implements IConfigurationDao {
    private static final String TAG = "ConfigurationDaoImpl";

    public ConfigurationDaoImpl(Context context){
        super(context);
    }

    @Override
    public void save(Configuration configuration) {
        Log.d(TAG, "Save into EstimateReport");

        Uri uri = ConfigurationMetaData.CONTENT_URI;

        // 先清除，然后再添加。只保存最后一次配置的数据。
        getContext().getContentResolver().delete(uri, null, null);

        ContentResolver contentResolver = getContext().getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(ConfigurationMetaData.SERVER, configuration.getServer());
        cv.put(ConfigurationMetaData.IMAGE_SERVER, configuration.getImageServer());

        Uri insertedUri = contentResolver.insert(uri, cv);
        Log.d(TAG, "inserted uri:" + insertedUri);
    }

    @Override
    public Configuration load() {
        Uri uri = ConfigurationMetaData.CONTENT_URI;

        Cursor c = getContext().getContentResolver().query(uri, null, null, null, null);
        if (c != null){
            int iServer = c.getColumnIndex(ConfigurationMetaData.SERVER);
            int iImageServer = c.getColumnIndex(ConfigurationMetaData.IMAGE_SERVER);

            c.moveToFirst();
            if (!c.isAfterLast()){
                Configuration item = new Configuration();
                item.setServer(c.getString(iServer));
                item.setImageServer(c.getString(iImageServer));

                c.close();
                return item;
            }
        }

        return null;
    }
}
