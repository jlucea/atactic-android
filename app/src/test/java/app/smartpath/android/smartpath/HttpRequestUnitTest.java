package app.smartpath.android.smartpath;

import android.util.Log;

import org.junit.Test;

import app.smartpath.android.smartpath.connect.HttpRequestHandler;


public class HttpRequestUnitTest {

    @Test
    public void queslistRequestTest() throws Exception {
        String res = HttpRequestHandler.sendQuestListRequest("ivan@email.es","peraltasan");
        Log.v("queslistRequestTest", "Result form sendQuestListRequest: " + res);

    }



}