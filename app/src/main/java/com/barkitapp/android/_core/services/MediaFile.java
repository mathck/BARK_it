package com.barkitapp.android._core.services;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.barkitapp.android.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MediaFile {

    public static File getOutputMediaFile(Context context) {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                context.getString(R.string.Bark_it_camera));
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(context.getString(R.string.Bark_it_camera), context.getString(R.string.directory_create_failed));
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
}
