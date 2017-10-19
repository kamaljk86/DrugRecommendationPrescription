package co.in.drugprescription.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DrugSyncAdapter extends AbstractThreadedSyncAdapter {

    Context mContext;
    public static final String TAG = "SyncAdapter";
    private static final String DRUG_TABLE = "drug_prescription";
    public static final Uri AUTHORITY_URI = Uri.parse("content://co.in.drugprescription.sync.drug");
    public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, DRUG_TABLE);
    private final ContentResolver mContentResolver;

    private static final String[] PROJECTION = new String[]{
            "id",
            "DRUGNAME",
            "MANUFACTUREDBY",
            "USEDFOR",
            "RATE",
            "DESCRIPTION"
    };


    public DrugSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mContentResolver = context.getContentResolver();
    }

    public DrugSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        try {
            final URL drugServiceURL = new URL(SyncConstants.DRUG_SERVICE_URL);
            InputStream stream = null;

            try {
                 stream = downloadUrl(drugServiceURL);
                 String feedData = readInput(stream);

                 Log.i(TAG, "onPerformSync result "+feedData);

                Map<String, DrugEntity> drugFeed = processDrugDataFeed(feedData);
                addUpdateDeleteLocalData(drugFeed, syncResult);

            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }
        catch (MalformedURLException e) {
            Log.wtf(TAG, "Feed URL is malformed", e);
            syncResult.stats.numParseExceptions++;
            return;
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            syncResult.stats.numIoExceptions++;
            return;
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error parsing feed: " + e.toString());
            syncResult.stats.numParseExceptions++;
            return;
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing feed: " + e.toString());
            syncResult.stats.numParseExceptions++;
            return;
        } catch (RemoteException e) {
            Log.e(TAG, "Error updating database: " + e.toString());
            syncResult.databaseError = true;
            return;
        } catch (OperationApplicationException e) {
            Log.e(TAG, "Error updating database: " + e.toString());
            syncResult.databaseError = true;
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        finally {
            getContext().getContentResolver().notifyChange(CONTENT_URI, null, false);
            Log.i(TAG, "Network synchronization complete");
        }

    }

    public void addUpdateDeleteLocalData(Map<String, DrugEntity> drugData, final SyncResult syncResult)
            throws Exception {

        final ContentResolver contentResolver = getContext().getContentResolver();
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        Cursor c = contentResolver.query(CONTENT_URI, PROJECTION, null, null, null);

        String id, drugname, manufacturedby, usedfor, rate, description;

        if (c != null) {
            while (c.moveToNext()) {
                syncResult.stats.numEntries++;
                id = c.getString(0);
                drugname = c.getString(1);
                manufacturedby = c.getString(2);
                usedfor = c.getString(3);
                rate = c.getString(4);
                description = c.getString(5);


                DrugEntity drugExist = drugData.get(id);

                if (drugExist != null) {

                    drugData.remove(id);

                    Uri existingUri = CONTENT_URI.buildUpon()
                            .appendPath(id).build();

                    //update record as local data is different
                    if ((drugExist.getDrugName() != null && !drugExist.getDrugName().equals(drugname))) {

                        batch.add(ContentProviderOperation.newUpdate(existingUri)
                                .withValue("DRUGNAME", drugExist.getDrugName())
                                .withValue("MANUFACTUREDBY", drugExist.getManufacturedBy())
                                .withValue("USEDFOR", drugExist.getUsedFor())
                                .withValue("RATE", drugExist.getRate())
                                .withValue("DESCRIPTION", drugExist.getDescription())
                                .build());
                        syncResult.stats.numUpdates++;


                    }
                } else {
                    //delete local record as it does not exist in server
                    Uri deleteUri = CONTENT_URI.buildUpon()
                            .appendPath(id).build();
                    batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                    syncResult.stats.numDeletes++;
                }
            }
            c.close();
        }

        // Add New records
        for (DrugEntity ce : drugData.values()) {

            batch.add(ContentProviderOperation.newInsert(CONTENT_URI)
                    .withValue("id", ce.getId())
                    .withValue("DRUGNAME", ce.getDrugName())
                    .withValue("MANUFACTUREDBY", ce.getManufacturedBy())
                    .withValue("USEDFOR", ce.getUsedFor())
                    .withValue("RATE", ce.getRate())
                    .withValue("DESCRIPTION", ce.getDescription())
                    .build());
            syncResult.stats.numInserts++;

        }

        mContentResolver.applyBatch("co.in.drugprescription.sync.drug", batch);

    }

    private Map<String, DrugEntity> processDrugDataFeed(String feed) {

        Map<String, DrugEntity> cashbackFeed = new HashMap<String, DrugEntity>();

        try {

            JSONArray json = new JSONArray(feed);
            for (int i = 0; i < json.length(); i++) {
                JSONObject jObject = json.getJSONObject(i);

                DrugEntity cbe = new DrugEntity();
                cbe.setId(jObject.getString("id"));
                cbe.setDrugName(jObject.getString("Drugname"));
                cbe.setManufacturedBy(jObject.getString("Manufactured_by"));
                cbe.setUsedFor(jObject.getString("Used_for"));
                cbe.setRate(jObject.getString("Rate"));
                cbe.setDescription(jObject.getString("Description"));

                cashbackFeed.put(cbe.getId(), cbe);
            }
        }
        catch (Exception e) {
            Log.e(TAG, "JSON error "+e.toString());
        }
        return cashbackFeed;
    }

    private InputStream downloadUrl(final URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(20000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        conn.connect();
        return conn.getInputStream();
    }

    private String readInput(InputStream is) {
        String str = "";
        StringBuffer buf = new StringBuffer();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            if (is != null) {
                while ((str = reader.readLine()) != null) {
                    buf.append(str + ",");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "ReadInput url exception "+e.toString());
        } finally {
            try {
                is.close();
            } catch (Throwable ignore) {
            }
        }
        return buf.toString();
    }
}
