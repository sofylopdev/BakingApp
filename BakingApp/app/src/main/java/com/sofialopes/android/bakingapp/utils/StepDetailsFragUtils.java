package com.sofialopes.android.bakingapp.utils;

import android.webkit.MimeTypeMap;

/**
 * Created by Sofia on 5/1/2018.
 */

public class StepDetailsFragUtils {

    public static String getMimeType(String thumbnailUri) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(thumbnailUri);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
