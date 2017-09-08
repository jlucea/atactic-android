package io.atactic.android.connect;

import java.net.HttpURLConnection;

/**
 * Created by Jaime on 18/4/17.
 */
public class LoginResponse {

    int responseCode;
    String content;
    boolean ok;
    // TODO token

    public LoginResponse(){

    }

    public LoginResponse(int code, String content) {
        this.ok = (code == HttpURLConnection.HTTP_OK);
        this.responseCode = code;
        this.content = content;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getContent() {
        return content;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
