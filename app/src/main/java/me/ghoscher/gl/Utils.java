package me.ghoscher.gl;

import android.content.Context;
import android.content.res.Resources;

import java.io.InputStream;

/**
 * Created by Hisham on 27/12/2014.
 */
public class Utils {
    public static String readRawText(Context context, int resId) {
        try {
            Resources res = context.getResources();
            InputStream in_s = res.openRawResource(resId);

            byte[] b = new byte[in_s.available()];
            in_s.read(b);

            return new String(b);
        } catch (Exception e) {
            // TODO log
        }

        return null;
    }
}
