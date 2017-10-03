package io.atactic.android.connect;

public class LoginResponse {

    private int responseCode;
    private String content;
    private boolean ok;
    // TODO token

    public LoginResponse(){

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
