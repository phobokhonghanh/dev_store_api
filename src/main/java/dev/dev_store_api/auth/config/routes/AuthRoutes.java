package dev.dev_store_api.auth.config.routes;

public final class AuthRoutes {
    private AuthRoutes() {}
    public static final String PREFIX = "/auth";
    public static final String REGISTER = PREFIX + "/register";
    public static final String REGISTER_ADMIN = PREFIX + "/register-admin";
    public static final String VERIFY_OTP = PREFIX + "/otp/verify/{username}";
    public static final String REFRESH_OTP = PREFIX + "/otp/refresh";
    public static final String LOGIN = PREFIX + "/login";
    public static final String LOGOUT = PREFIX + "/logout";
    public static final String REFRESH_TOKEN = PREFIX + "/token/refresh";
    public static final String REGISTRATION_STATUS = PREFIX + "/registration-status";
}
