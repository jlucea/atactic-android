package io.atactic.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class CredentialsCache {

    public static class UserCredentials {
        String userName;
        String password;
        int userId;
        String authToken;

        UserCredentials(String usr, String pwd){
            userName = usr;
            password = pwd;
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

    public static void storeCredentials(Context context, String username, String password, int userId, String token){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .putString(USERNAME_KEY, username)
                .putString(PASSWORD_KEY,password)
                .apply();
    }

    public static void removeCredentials(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .remove(USERNAME_KEY)
                .remove(PASSWORD_KEY)
                .commit();
    }


    public static UserCredentials recoverCredentials(Context context){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String username = prefs.getString(USERNAME_KEY, null);
        String password = prefs.getString(PASSWORD_KEY, null);

        if ((username != null) && (password!=null)) {
            return new UserCredentials(username, password);
        } else {
            return null;
        }
    }


}
