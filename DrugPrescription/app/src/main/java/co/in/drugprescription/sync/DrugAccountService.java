package co.in.drugprescription.sync;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DrugAccountService extends Service {
    private DrugAuthenticator mAuthenticator;

    public static Account GetAccount() {
        final String accountName = SyncConstants.ACCOUNT_NAME;
        return new Account(accountName, SyncConstants.ACCOUNT_TYPE);
    }

    @Override
    public void onCreate() {
        mAuthenticator = new DrugAuthenticator(this);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
