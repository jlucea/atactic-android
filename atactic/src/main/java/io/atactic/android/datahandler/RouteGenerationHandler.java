package io.atactic.android.datahandler;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import io.atactic.android.network.request.RecommendedRouteRequest;

public class RouteGenerationHandler {

    private RoutePresenter presenter;

    public RouteGenerationHandler(RoutePresenter routePresenter){
        this.presenter = routePresenter;
    }

    public void generateRoute(int userId, float userPositionLatitude, float userPositionLongitude,
                              int numberOfWayPoints){

        RequestParams params = new RequestParams();
        params.userId = userId;
        params.userLat = userPositionLatitude;
        params.userLon = userPositionLongitude;
        params.waypoints = numberOfWayPoints;

        new GeneratePathAsyncHttpRequest(this).execute(params);
    }

    private void presentRoute(JSONArray route){
        presenter.displayRoute(route);
    }

    private void presentErrorMessage(String message){
        presenter.displayRouteError(message);
    }


    private class RequestParams {
        int userId;
        float userLat;
        float userLon;
        int waypoints;
    }


    private static class GeneratePathAsyncHttpRequest extends AsyncTask<RequestParams, Void, JSONArray> {

        private RouteGenerationHandler handler;

        GeneratePathAsyncHttpRequest(RouteGenerationHandler routeHandler){
            this.handler = routeHandler;
        }

        @Override
        protected JSONArray doInBackground(RequestParams... params) {

            Log.d("GeneratePathAsync", "User ID: " + params[0].userId);

            // Send Http request and receive JSON response
            String response = RecommendedRouteRequest.send(params[0].userId,
                    params[0].userLat, params[0].userLon, params[0].waypoints);

            // Log.d("GeneratePathAsync", "JSON Response: " + response);

            // Return JSON array containing the data to show in the view
            try {
                return new JSONArray(response);

            } catch (JSONException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONArray JSONResponse) {

            if (JSONResponse == null){
                handler.presentErrorMessage("No se ha podido generar la ruta");
            }
            handler.presentRoute(JSONResponse);
        }

    }

    public interface RoutePresenter {

        void displayRoute(JSONArray waypointsJSON);

        void displayRouteError(String message);
    }

}
