package com.barkitapp.android.parse_backend.converter;

public class NotificationConverter {

    public static int getIdFromPostId(String post_id) {
        // post_id -> int
        String t = "";
        for (int i = 0; i < post_id.length(); ++i) {
            char ch = post_id.charAt(i);
            int n = (int)ch - (int)'a' + 1;

            if(n < 0)
                n *= -1;

            t += String.valueOf(n);
        }

        return Integer.parseInt(t.substring(0, t.length() / 3));
    }
}
