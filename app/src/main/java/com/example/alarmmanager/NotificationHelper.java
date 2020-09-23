package com.example.alarmmanager;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private NotificationManager mManager;
    public static String posTests = "";

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    //Creates channel to send out notification
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {

        //Creates channel object
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        //Location where we can can set different activities for notification being delivered

        RequestQueue queue = Volley.newRequestQueue(this);

        //Saves url as string to be searched on the web
        String url = "https://api.covidtracking.com/v1/states/va/20200918.json";

        //Object request gets the JSON object from the internet
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject data = response;

                        //Saves the positive case number from JSON file to string in application
                        try{
                            posTests = data.getString("Positive");
                        }catch (JSONException e){
                            posTests = "0";
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
        getManager().createNotificationChannel(channel);
    }

    //Method to manage different notification channels if they exist
    public NotificationManager getManager() {
        //creates manager if one is not already set
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    //Gets channel filled with information we want to present the user
    public NotificationCompat.Builder getChannelNotification() {
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setContentTitle("Number of cases in your area:")
                .setContentText(posTests)
                .setSmallIcon(R.drawable.ic_android);
    }
}
