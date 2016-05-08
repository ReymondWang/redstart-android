package com.purplelight.redstar.provider.dao.impl;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.purplelight.redstar.provider.dao.ISystemUserDao;
import com.purplelight.redstar.provider.entity.SystemUser;
import com.purplelight.redstar.provider.RedStartProviderMeta.SystemUserMetaData;

/**
 * 用户信息操作类
 * Created by wangyn on 16/5/3.
 */
public class SystemUserDaoImpl extends BaseDaoImpl implements ISystemUserDao {
    private final static String TAG = "SystemUserDaoImpl";

    public SystemUserDaoImpl(Context context) {
        super(context);
    }

    /**
     * 一个手机上记录的登录帐号只能有一个，因此在保存数据的时候
     * 采用的方式为先清除数据库的信息，然后保存新的数据进去。
     * @param user      要保存的登录信息
     */
    @Override
    public void save(SystemUser user) {
        Log.d(TAG, "Save into SystemUser");

        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = SystemUserMetaData.CONTENT_URI;

        int cnt = contentResolver.delete(uri, null, new String[0]);
        Log.d(TAG, "delete count:" + cnt);

        ContentValues cv = new ContentValues();
        cv.put(SystemUserMetaData.USERID, user.getId());
        cv.put(SystemUserMetaData.USERCODE, user.getUserCode());
        cv.put(SystemUserMetaData.USERNAME, user.getUserName());
        cv.put(SystemUserMetaData.PASSWORD, user.getPassword());
        cv.put(SystemUserMetaData.SEX, user.getSex());
        cv.put(SystemUserMetaData.EMAIL, user.getEmail());
        cv.put(SystemUserMetaData.PHONE, user.getPhone());
        cv.put(SystemUserMetaData.ADDRESS, user.getAddress());
        cv.put(SystemUserMetaData.HEADIMGPATH, user.getHeadImgPath());
        cv.put(SystemUserMetaData.TOKEN, user.getToken());

        Uri insertedUri = contentResolver.insert(uri, cv);
        Log.d(TAG, "inserted uri:" + insertedUri);
    }


    /**
     * 从本地数据库中将保存的登录信息加载出来，如果没有则返回NULL
     * @return  SystemUser实体
     */
    @Override
    public SystemUser load() {
        Uri uri = SystemUserMetaData.CONTENT_URI;
        Cursor c = getContext().getContentResolver().query(uri, null, null, null, null);
        if (c != null){
            int iUserId = c.getColumnIndex(SystemUserMetaData.USERID);
            int iUserCode = c.getColumnIndex(SystemUserMetaData.USERCODE);
            int iUserName = c.getColumnIndex(SystemUserMetaData.USERNAME);
            int iSex = c.getColumnIndex(SystemUserMetaData.SEX);
            int iPassword = c.getColumnIndex(SystemUserMetaData.PASSWORD);
            int iEmail = c.getColumnIndex(SystemUserMetaData.EMAIL);
            int iPhone = c.getColumnIndex(SystemUserMetaData.PHONE);
            int iAddress = c.getColumnIndex(SystemUserMetaData.ADDRESS);
            int iHeadImgPath = c.getColumnIndex(SystemUserMetaData.HEADIMGPATH);
            int iToken = c.getColumnIndex(SystemUserMetaData.TOKEN);

            c.moveToFirst();
            if (!c.isAfterLast()){
                SystemUser user = new SystemUser();
                user.setId(c.getString(iUserId));
                user.setUserCode(c.getString(iUserCode));
                user.setUserName(c.getString(iUserName));
                user.setSex(c.getString(iSex));
                user.setPassword(c.getString(iPassword));
                user.setEmail(c.getString(iEmail));
                user.setPhone(c.getString(iPhone));
                user.setAddress(c.getString(iAddress));
                user.setHeadImgPath(c.getString(iHeadImgPath));
                user.setToken(c.getString(iToken));

                Log.d(TAG, "get userinfo:" + user.getUserName());
                c.close();

                return user;
            } else {
                c.close();
            }
        }

        return null;
    }

    /**
     * 清空本地数据库中的保存的登录数据
     */
    @Override
    public void clear() {
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uri = SystemUserMetaData.CONTENT_URI;

        int cnt = contentResolver.delete(uri, null, new String[0]);
        Log.d(TAG, "delete count:" + cnt);
    }
}
