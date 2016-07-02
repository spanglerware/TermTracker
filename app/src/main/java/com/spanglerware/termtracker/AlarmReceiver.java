package com.spanglerware.termtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    private static Context mContext;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //sending application context to this receiver instead of activity context to prevent memory leaks
        if (mContext == null) mContext = context;

        String message = intent.getStringExtra("message");

        //PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        //PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK| PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        //wakeLock.acquire();

        showNotification(mContext, message);

        //wakeLock.release();
    }

    private void showNotification(Context context, String message) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View myView = mInflater.inflate(R.layout.notification_popup, null);

        TextView text = (TextView) myView.findViewById(R.id.notification_text);
        text.setTextSize(24);
        text.setText("Alert: \n" + message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(myView);
        toast.show();
    }


} //end of AlarmReceiver

