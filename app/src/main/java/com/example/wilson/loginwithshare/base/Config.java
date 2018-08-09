package com.example.wilson.loginwithshare.base;


public class Config {


    /**
     * QQ登录的APPID
     */
    public static final String QQ_APP_ID = "1107420227";

    public static final String QQ_SCOPE_ALL = "all";
    public static final String QQ_SCOPE_INFO = "get_simple_userinfo";
    public static final String QQ_SCOPE_ADD_TOPIC = "add_topic";
    /**
     * app_id是从微信官网申请到的合法APPid
     */
    public static final String APP_ID_WX = "wxb363a9ff53731258";

    /**
     * 微信AppSecret值
     */
    public static final String APP_SECRET_WX = "2b0d0325bb7c8383bff52e0900b7f56c";

    /**
     * 新浪微博
     */
    public static final String APP_KEY_SINA = "618877346";

    /**
     * 新浪AppSecret值
     */
    public static final String APP_SECRET_SINA = "cf2742a1eec00f36a6618078615bb63b";

    /**
     * 当前 DEMO 应用的回调页，第三方应用可以使用自己的回调页。
     * 建议使用默认回调页：https://api.weibo.com/oauth2/default.html
     */
    //注意，这里的回调页应该和官网的回调页一致，不然会报21322错误
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     * <p>
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     * <p>
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * <p>
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */

    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    public static final String APP_LOGO_URL = "http://182.254.232.121:8080/commonImg/stop_car_app_logo.png";

}
