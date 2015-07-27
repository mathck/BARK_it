package com.barkitapp.android;

public final class ParseKey {

    public static final boolean USING_ALPHA = false;

    private static final String PARSE_APPLICATION_ID = "bPaekY4XHBk0wDHQlhHwXPSZUTIwGI5m4s8vxcxt";
    private static final String PARSE_APPLICATION_ID_TEST = "dnZ82HoAaXdy5gm6yIXMlpf4d8HIDDUhPdWp5Uac"; // testing

    private static final String PARSE_CLIENT_KEY = "d7moOVAxLVbHfd6ybMW4UItPo5nvq7YmhgObNcyd";
    private static final String PARSE_CLIENT_KEY_TEST = "x95ee5lAhfDkqIeedssUQw2Ik4JbwGtCyuYof2yM"; // testing

    public static String getAppId() {
        return USING_ALPHA ? PARSE_APPLICATION_ID_TEST : PARSE_APPLICATION_ID;
    }

    public static String getClientKey() {
        return USING_ALPHA ? PARSE_CLIENT_KEY_TEST : PARSE_CLIENT_KEY;
    }
}
