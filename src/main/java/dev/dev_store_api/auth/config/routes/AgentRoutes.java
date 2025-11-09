package dev.dev_store_api.auth.config.routes;

public final class AgentRoutes {
    private AgentRoutes() {}
    public static final String PREFIX = "/agent";
    public static final String GET_LIST = "/list";
    public static final String DELETE = "/{id}";
    public static final String LOG_OUT= "/logout/{id}";
    public static final String LOG_OUT_ALL= "/logout/all";
}
