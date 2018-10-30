package io.atactic.android.network;

public class NetworkConstants {

    /*
     * The following constant sets the server's URL
     */
    // public static final String API_SERVER = "http://192.168.1.37:8080";                     // Local server IP within WiFi network
    // public static final String API_SERVER = "http://api.atactic.io";                           // Jelastic server
    // public static final String API_SERVER = "http://env-6775033.jelastic.cloudhosted.es";   // Jelastic server
    public static final String API_SERVER = "http://10.0.2.2:8080";                         // Emulating machine IP

    /*
     * URL for the Mobile API resources
     */
    public static final String API_ROOT = "/mobile/rsc";

    /*
     * Service URLs
     */
    // Authentication resource
    public static final String RSC_AUTH = "/auth";

    // Campaing resource
    public static final String RSC_QUESTS = "/quest";

    // Account resource
    public static final String RSC_ACCOUNTS = "/account";
    public static final String RSC_NEARBY_ACCOUNTS= "/account/nearby";

    // Check-in resource
    public static final String RSC_CHECKIN = "/checkin";

    // Target account resource
    public static final String RSC_TARGETS= "/target/u";
    public static final String RSC_QUEST_TARGETS= "/target/p";

    // User profile resource
    public static final String RSC_PROFILE = "/profile";
    public static final String RSC_RANKING = "/game/ranking";

    // Route resource
    public static final String RSC_PATH= "/path/short";

}
