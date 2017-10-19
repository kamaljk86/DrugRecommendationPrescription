package co.in.drugprescription.customcomponents;

import android.content.Context;
import android.os.PowerManager;

public class ScreenWakeLock
{
    Context mContext;
    PowerManager.WakeLock wakeLock;

    public ScreenWakeLock(Context context)
    {
        mContext = context;
    }

    @SuppressWarnings("deprecation")
    public void acquirewakeLock()
    {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
        wakeLock.acquire();
    }

    public void releasewakeLock()
    {
        if(wakeLock.isHeld()){
            wakeLock.release();
        }
    }

}
