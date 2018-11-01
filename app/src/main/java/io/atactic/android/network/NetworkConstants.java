package io.atactic.android.network;

public class NetworkConstants {

    /*
     * The following constant sets the server's URL
     */

    // public static final String API_SERVER = "http://api.atactic.io";                         // Production server

    // public static final String API_SERVER = "http://192.168.1.37:8080";                      // Server IP within local network
    // public static final String API_SERVER = "http://env-6775033.jelastic.cloudhosted.es";    // Jelastic server

    static final String API_SERVER = "http://10.0.2.2:8080";                             // Emulating machine IP

    /*
     * URL for the Mobile API resources
     */
    static final String API_ROOT = "/mobile/rsc";

    /*
     * Service URLs
     */
    // Authentication resource
    public static final String RSC_AUTH = "/auth";

    // Campaing resource
    public static final String RSC_QUESTS = "/campaign";

    // Account resource
    public static final String RSC_ACCOUNTS = "/account/map";       // TODO Use a different service
    public static final String RSC_NEARBY_ACCOUNTS= "/account/nearby";

    // Target account resource
    public static final String RSC_TARGETS= "/account/targets";
    // Campaign targets
    public static final String RSC_QUEST_TARGETS= "/account/targets/campaign";

    // Check-in resource
    public static final String RSC_CHECKIN = "/checkin";

    // User profile resource
    public static final String RSC_PROFILE = "/user/profile";
    public static final String RSC_RANKING = "/game/ranking";

    // Route resource
    public static final String RSC_PATH= "/path/short";

}
