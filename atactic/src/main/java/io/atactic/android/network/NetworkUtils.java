package io.atactic.android.network;

import android.net.Uri;
import android.util.Log;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Utility functions for the Network package
 */
public class NetworkUtils {

    private static final String LOG_TAG = "NetworkUtils";

    /**
     * Creates an URL object from the specified API resource URL String
     *
     * @param resource_uri String representing the URL
     * @return URL object
     */
    public static URL buildUrl(String resource_uri){
        String baseUrl = NetworkConstants.API_SERVER
                .concat(NetworkConstants.API_ROOT)
                .concat(resource_uri);

        Uri uri = Uri.parse(baseUrl).buildUpon().build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error building URL", e);
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Utility function to get text data from a stream.
     *
     * @param in DataStream to read
     * @return Data as String
     */
    public static String readStreamContent(InputStream in){
        Scanner scanner = new Scanner(in);
        scanner.useDelimiter("\\A");

        boolean hasInput = scanner.hasNext();
        if (hasInput) {
            return scanner.next();
        } else {
            return null;
        }
    }


}
