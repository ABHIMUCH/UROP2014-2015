package com.example.research;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;


public abstract class WakefulBroadcastReceiver extends BroadcastReceiver {

    private static final String EXTRA_WAKE_LOCK_ID = "android.support.content.wakelockid";
    private static final SparseArray<PowerManager.WakeLock> mActiveWakeLocks
            = new SparseArray<PowerManager.WakeLock>();
    private static int mNextId = 1;
    private static int NORMAL = 1;
    private static int TRAIN = 2;

    public static ComponentName startWakefulService(Context context) {


        synchronized (mActiveWakeLocks) {
            int id = mNextId;
            mNextId++;
            if (mNextId <= 0) {
                mNextId = 1;
            }
            //primary intent (run now)
            Intent intent = new Intent(context, QueryService.class);
            intent.putExtra(EXTRA_WAKE_LOCK_ID, id);
            intent.putExtra("type", TRAIN);
            ComponentName comp = context.startService(intent);
            if (comp == null) {
                return null;
            }

            Intent postIntent = new Intent(context, QueryService.class);
            postIntent.putExtra(EXTRA_WAKE_LOCK_ID, id);
            postIntent.putExtra("type", NORMAL);
            PendingIntent pi = PendingIntent.getService(context, 0, postIntent,
                    0);
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                    300000, pi);

            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "wake:" + comp.flattenToShortString());
            wl.setReferenceCounted(false);
            wl.acquire(60 * 1000);
            mActiveWakeLocks.put(id, wl);
            return comp;
        }
    }


    public static boolean completeWakefulIntent(Intent intent) {
        final int id = intent.getIntExtra(EXTRA_WAKE_LOCK_ID, 0);
        if (id == 0) {
            return false;
        }
        synchronized (mActiveWakeLocks) {
            PowerManager.WakeLock wl = mActiveWakeLocks.get(id);
            if (wl != null) {
                wl.release();
                mActiveWakeLocks.remove(id);
                return true;
            }
            // We return true whether or not we actually found the wake lock
            // the return code is defined to indicate whether the Intent contained
            // an identifier for a wake lock that it was supposed to match.
            // We just log a warning here if there is no wake lock found, which could
            // happen for example if this function is called twice on the same
            // intent or the process is killed and restarted before processing the intent.
            Log.d("Wakeful", "No active wake lock id #" + id);
            return true;
        }
    }
}