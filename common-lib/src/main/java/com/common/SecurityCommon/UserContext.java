package com.common.SecurityCommon;

public class UserContext {
    private static final ThreadLocal<String> userId = new ThreadLocal<>();

    public static void setUserId(String id) {
        userId.set(id);
    }

    public static String getUserId() {
        return userId.get();
    }

    public static void clear() {
        userId.remove();
    }
}
