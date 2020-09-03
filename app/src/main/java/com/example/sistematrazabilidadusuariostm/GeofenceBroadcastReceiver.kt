package com.example.sistematrazabilidadusuariostm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private val TAG = "GeofenceBroadcastReceiver"
    private val channelId  = "GeofenceChannel"
    private val notificationId = 420

    override fun onReceive(context: Context?, intent: Intent?) {


        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            //val errorMessage = GeofenceStatusCodes.getErrorString(geofencingEvent.errorCode)
            Log.e(TAG, "errorMessage")
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
        geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(
                geofenceTransition,
                triggeringGeofences
            )

            // Send notification and log the transition details.
            if (context != null) {
                sendNotification(geofenceTransitionDetails,context)
            }

            Log.i(TAG, geofenceTransitionDetails)
        } else {
            // Log the error.
            Log.e(TAG, "Other geofence transition: $geofenceTransition")
        }
    }


    private fun getGeofenceTransitionDetails(
        geofenceTransition: Int,
        triggeringGeofences: MutableList<Geofence>
    ): String {

        val geofenceTransitionString = getTransitionString(geofenceTransition)

        // Get the Ids of each geofence that was triggered.
        val triggeringGeofencesIdsList: ArrayList<String> = arrayListOf()
        for (geofence in triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.requestId)
        }
        val triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList)

        return "$geofenceTransitionString: $triggeringGeofencesIdsString"
    }

    private fun getTransitionString(transitionType: Int): String {
        return when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "GEOFENCE_TRANSITION_ENTER"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "GEOFENCE_TRANSITION_EXIT"
            else -> "GEOFENCE_TRANSITION_UNKNOWN"
        }
    }

    private fun sendNotification(geofenceTransitionDetails: String, context: Context) {

        //createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, channelId)

            //https://walkiriaapps.com/blog/android/iconos-notificaciones-android-studio/
            .setSmallIcon(R.drawable.geofence_icon)
            .setContentTitle("Activación geovallado")
            .setContentText(geofenceTransitionDetails)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            .setAutoCancel(true)// NO FUNCIONA

        Log.i(TAG,"Send notification")

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }

    }
    /*private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "STUTM"
            val descriptionText = "Channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }*/


}

