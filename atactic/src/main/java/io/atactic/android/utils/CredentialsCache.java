package io.atactic.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import io.atactic.android.element.AtacticApplication;

public class CredentialsCache {

    public static class UserCredentials {
        String userName;
        String password;
        int userId;

        UserCredentials(String usr, String pwd, int uid){
            userName = usr;
            password = pwd;
            userId = uid;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }

        public int getUserId() {
            return userId;
        }
    }

    private static final String USERNAME_KEY = "usrname";
    private static final String PASSWORD_KEY = "usrpwd";
    private static final String USERID_KEY = "usrid";
    private static final String TOKEN_KEY = "authtoken";


    /*
    public static void storeCredentials(Context context, String username, String password, int userId){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .putString(USERNAME_KEY, username)
                .putString(PASSWORD_KEY,password)
                .putInt(USERID_KEY, userId)
                .apply();
    } */

    public static void storeCredentials(String username, String password, int userId) {

        Context appContext = AtacticApplication.getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
        prefs.edit()
                .putString(USERNAME_KEY, username)
                .putString(PASSWORD_KEY,password)
                .putInt(USERID_KEY, userId)
                .apply();
    }


    /*
    public static void storeToken(Context context, String token){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .putString(TOKEN_KEY, token)
                .apply();

    } */

    public static void crearAll(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .remove(USERNAME_KEY)
                .remove(PASSWORD_KEY)
                .remove(USERID_KEY)
                .remove(TOKEN_KEY)
                .apply();
    }

    /**
     *
     * @deprecated Use recoverCredentials()
     * @param context Caller's context
     * @return Credentials
     */
    public static UserCredentials recoverCredentials(Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String username = prefs.getString(USERNAME_KEY, null);
        String password = prefs.getString(PASSWORD_KEY, null);
        int userId = prefs.getInt(USERID_KEY, 0);

        return new UserCredentials(username, password, userId);
    }


    public static UserCredentials recoverCredentials(){

        Context appContext = AtacticApplication.getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);

        String username = prefs.getString(USERNAME_KEY, null);
        String password = prefs.getString(PASSWORD_KEY, null);
        int userId = prefs.getInt(USERID_KEY, 0);

        if (username!=null & password != null & userId != 0) {
            return new UserCredentials(username, password, userId);
        } else {
            return null;
        }
    }

}
