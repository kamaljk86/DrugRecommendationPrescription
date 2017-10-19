package co.in.drugprescription.sync;

public class SyncConstants {
    public static final String CONTENT_URI = "co.in.drugprescription.sync.drug";
    public static final String ACCOUNT_TYPE = "co.in.drugprescription.sync";
    public static final String ACCOUNT_NAME = "drugdata_sync";
    public static final String ACCOUNT = "drugdatasync";
    public static final String AUTHORITY = "co.in.drugprescription.sync.drug";
    public static final int SYNC_INTERVAL = 60;
    public static final String DRUG_SERVICE_URL = "http://192.168.1.6/drug/get_data.php";
}