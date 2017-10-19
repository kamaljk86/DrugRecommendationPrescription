package co.in.drugprescription.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DrugSyncAdapterService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static DrugSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();

        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new DrugSyncAdapter(getApplicationContext(), true);
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
