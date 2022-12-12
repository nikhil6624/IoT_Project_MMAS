package com.eebax.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;
import android.os.Vibrator;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";
    Vibrator v;


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
       // Toast.makeText(context, "Geofence triggered", Toast.LENGTH_SHORT).show();

        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()){
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }


        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }
//        Location location = geofencingEvent.getTriggeringLocation();
        int transitionType = geofencingEvent.getGeofenceTransition();
        v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 1000, 500};

        switch (transitionType) {

            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "You have entered DANGER ZONE!!!", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("DANGER AHEAD!!! :(", "You have entered DANGER ZONE!!! Please Turn-around and walk", MapsActivity.class);

// Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(pattern, 0);
                    //v.vibrate(VibrationEffect.createOneShot(20000, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(pattern, 0);
                }
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "You are still in the Danger Zone!", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("You are still in the Danger Zone!", "", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "You are in SAFE Zone now:)", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("SAFE ZONE", "You are in Safe Zone now. Have a Happy Journey :)", MapsActivity.class);
                v.cancel();
                break;
        }


    }
}