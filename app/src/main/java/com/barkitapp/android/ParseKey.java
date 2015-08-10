package com.barkitapp.android;

public final class ParseKey {

    private static final String PARSE_APPLICATION_ID = "bPaekY4XHBk0wDHQlhHwXPSZUTIwGI5m4s8vxcxt";
    private static final String PARSE_APPLICATION_ID_TEST = "dnZ82HoAaXdy5gm6yIXMlpf4d8HIDDUhPdWp5Uac"; // testing

    private static final String PARSE_CLIENT_KEY = "d7moOVAxLVbHfd6ybMW4UItPo5nvq7YmhgObNcyd";
    private static final String PARSE_CLIENT_KEY_TEST = "x95ee5lAhfDkqIeedssUQw2Ik4JbwGtCyuYof2yM"; // testing

    public static String getAppId() {
        return isAlpha() ? PARSE_APPLICATION_ID_TEST : PARSE_APPLICATION_ID;
    }

    public static String getClientKey() {
        return isAlpha() ? PARSE_CLIENT_KEY_TEST : PARSE_CLIENT_KEY;
    }

    private static boolean isAlpha() {
        return BuildConfig.VERSION_NAME.equals("Alpha");
    }
}
