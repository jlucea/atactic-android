package io.atactic.android.element;

import android.app.Application;

/**
 * Created by Jaime on 18/4/17.
 */

public class AtacticApplication extends Application {

    private String userName;
    private String password;
    private int userId;
    private String token;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String user) {
        this.userName = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
