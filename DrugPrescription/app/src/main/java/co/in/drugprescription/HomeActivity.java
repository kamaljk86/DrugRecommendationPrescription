package co.in.drugprescription;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import co.in.drugprescription.customcomponents.DrugItem;
import co.in.drugprescription.customcomponents.ScreenWakeLock;
import co.in.drugprescription.database.DatabaseHandler;
import co.in.drugprescription.sync.DrugSyncAdapter;
import co.in.drugprescription.sync.SyncConstants;

public class HomeActivity extends Activity implements AdapterView.OnItemClickListener {

    private Account mAccount;
    private ContentResolver mContentResolver;

    //AutoCompleteSearch variables
    AutoCompleteTextView mAutoDrugSearch;
    ArrayList<DrugItem> mDrugList = new ArrayList<DrugItem>();
    ArrayAdapter<DrugItem> mDrugAdapter;
    LinkedHashMap<String,String> drugLinkedHM = new LinkedHashMap<String,String>();

    // Voice recognition variables
    private static final int REQUEST_CODE = 1234;
    Button speakButton;
    LinkedHashMap<String,String> drugVoiceLinkedHM = new LinkedHashMap<String,String>();

    DatabaseHandler dbHandler;
    ContentObserver mObserver;
    ScreenWakeLock mScreenWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Checking network status
        if(!isNetworkConnected()) {
            finish();
        }

        mScreenWakeLock = new ScreenWakeLock(HomeActivity.this);
        mScreenWakeLock.acquirewakeLock();


        mAccount = CreateSyncAccount(this);
        mContentResolver = getContentResolver();
        mContentResolver.setIsSyncable(mAccount, SyncConstants.AUTHORITY, 1);
        mContentResolver.setSyncAutomatically(mAccount, SyncConstants.AUTHORITY, true);
        mContentResolver.addPeriodicSync(
                mAccount,
                SyncConstants.AUTHORITY,
                Bundle.EMPTY,
                SyncConstants.SYNC_INTERVAL);

        // start sync
        runSyncAdapter();

        dbHandler = new DatabaseHandler(this);
        mAutoDrugSearch = (AutoCompleteTextView) findViewById(R.id.autoSearchDrugItems);
        speakButton = (Button) findViewById(R.id.speakButton);

        //invoked if any changes happen in syncing data from server
        mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            public void onChange(boolean selfChange) {
                getDrugListByAutoComplete();
            }
        };

        // register observer to notify once sync done
        getContentResolver().registerContentObserver(DrugSyncAdapter.CONTENT_URI, true, mObserver);

        // enable intent for voice recognizer
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }
    }

    // Request syncing using contentresolver
    public void runSyncAdapter() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(mAccount, SyncConstants.AUTHORITY, bundle);
    }

    public Account CreateSyncAccount(Context context) {
        Account newAccount = new Account(SyncConstants.ACCOUNT, SyncConstants.ACCOUNT_TYPE);
        AccountManager accountManager =
                (AccountManager) context.getSystemService(ACCOUNT_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS}, 31);
        }
        Account accounts[] = accountManager.getAccountsByType(SyncConstants.ACCOUNT_TYPE);
        if (accounts == null || accounts.length < 1) {
            if (accountManager.addAccountExplicitly(newAccount, null, null)) {
                return newAccount;
            }
        } else {
            return accounts[0];
        }
        return null;
    }


    public void getDrugListByAutoComplete() {
        try {

            if(mDrugList.size() > 0)
                mDrugList.clear();
            if (drugLinkedHM.size() > 0)
                drugLinkedHM.clear();

            drugLinkedHM = dbHandler.getDrugDetailsQuickSearch();

            Set set = drugLinkedHM.entrySet();
            Iterator i = set.iterator();
            while (i.hasNext()) {
                Map.Entry me = (Map.Entry) i.next();
                mDrugList.add(new DrugItem(me.getKey().toString(), me.getValue().toString()));
            }
            mDrugAdapter = new ArrayAdapter<DrugItem>(this, android.R.layout.simple_dropdown_item_1line, mDrugList);
            mAutoDrugSearch.setThreshold(1);
            mAutoDrugSearch.setAdapter(mDrugAdapter);
            mAutoDrugSearch.setFocusable(true);
            mAutoDrugSearch.setFocusableInTouchMode(true);
            mAutoDrugSearch.setOnItemClickListener(HomeActivity.this);
        }
        catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        DrugItem drugItem = (DrugItem) adapterView.getAdapter().getItem(position);
        mAutoDrugSearch.setText(drugItem.getName());

        drugInDetail(drugItem.getId());
    }


    /**
     * Handle the action of the voice recognition button being clicked
     */
    public void speakButtonClicked(View v)
    {
        startVoiceRecognitionActivity();
    }

    /**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Try saying Drug name");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            boolean voiceRecognitionDataStatus = false;
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            for(int i=0; i<matches.size(); i++) {

                if(dbHandler.getDrugByName(matches.get(i)).size() > 0) {
                    drugVoiceLinkedHM = dbHandler.getDrugByName(matches.get(i));

                    Set set = drugLinkedHM.entrySet();
                    Iterator ii = set.iterator();
                    while (ii.hasNext()) {
                        Map.Entry me = (Map.Entry) ii.next();
                        if(matches.get(i).equalsIgnoreCase(me.getValue().toString())) {
                            voiceRecognitionDataStatus = true;
                            drugInDetail(me.getKey().toString());
                        }
                    }
                }
            }

            if(!voiceRecognitionDataStatus)
                Toast.makeText(HomeActivity.this,"Records not matching. Please try again!",Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void drugInDetail(String drugID) {

        ArrayList<String> drug = dbHandler.getDrugByID(drugID);

        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_drug_in_detail);

        TextView drugName = (TextView) dialog.findViewById(R.id.drugName);
        drugName.setText(drug.get(0));

        TextView manufacturedBy = (TextView) dialog.findViewById(R.id.manufacturedBy);
        manufacturedBy.setText(drug.get(1));

        TextView usedfor = (TextView) dialog.findViewById(R.id.drugUsedFor);
        usedfor.setText("Used For : "+drug.get(2));

        TextView drugRate = (TextView) dialog.findViewById(R.id.drugRate);
        String currencySymbol = getResources().getString(R.string.currency);
        drugRate.setText(currencySymbol + " " + drug.get(3));

        TextView description = (TextView) dialog.findViewById(R.id.drugDescription);
        description.setText(drug.get(4));

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getContentResolver().unregisterContentObserver(mObserver);
            mScreenWakeLock.releasewakeLock();
        }
        catch (Exception e) { e.printStackTrace(); }
    }


    public boolean isNetworkConnected() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connect = (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connect.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connect.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connect.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connect.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            return true;

        } else if (
                connect.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connect.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            Toast.makeText(HomeActivity.this, " Network is not Connected !", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }
}