package dev.dev_store_api.auth.config.routes;

public final class AuthRoutes {
    private AuthRoutes() {}
    public static final String PREFIX = "/auth";
    public static final String REGISTER = "/register";
    public static final String REGISTER_ADMIN = "/register-admin";
    public static final String VERIFY_OTP = "/otp/verify/{username}";
    public static final String REFRESH_OTP = "/otp/refresh";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String REFRESH_TOKEN = "/token/refresh";
    public static final String REGISTRATION_STATUS = "/registration-status";
}
