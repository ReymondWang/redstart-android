package com.purplelight.redstar.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5StringUtil
{
    private static final MessageDigest digest;

    static
    {
        try
        {
            digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String md5StringFor(String s)
    {
        final byte[] hash = digest.digest(s.getBytes());
        final StringBuilder builder = new StringBuilder();
        for (byte b : hash)
        {
            String encodeB = Integer.toString(b & 0xFF, 16);
            if (encodeB.length() == 1){
                encodeB = "0" + encodeB;
            }
            builder.append(encodeB);
        }
        return builder.toString();
    }
}
