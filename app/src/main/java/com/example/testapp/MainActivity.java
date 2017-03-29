package com.example.testapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import co.poynt.api.model.EmailType;
import co.poynt.os.Constants;
import co.poynt.os.model.AccessoryProvider;
import co.poynt.os.model.AccessoryProviderFilter;
import co.poynt.os.model.AccessoryType;
import co.poynt.os.model.CapabilityProvider;
import co.poynt.os.model.Intents;
import co.poynt.os.model.Payment;
import co.poynt.os.model.PoyntError;
import co.poynt.os.printing.ReceiptPrintingPref;
import co.poynt.os.services.v1.IPoyntAccessoryManager;
import co.poynt.os.services.v1.IPoyntAccessoryManagerListener;
import co.poynt.os.services.v1.IPoyntPrinterService;
import co.poynt.os.util.AccessoryProviderServiceHelper;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

/*    RequestQueue mRequestQueue;
    ImageLoader mImageLoader;*/

    private String kitchenPrinterName;

    private ServiceConnection printerServiceConnection;
    private IPoyntPrinterService poyntPrinterService;
    private IPoyntAccessoryManager accessoryManagerService;
    private ServiceConnection accessoryManagerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            accessoryManagerService = IPoyntAccessoryManager.Stub.asInterface(service);
            // if you want to use the built-in printer to print your custom receipt
            Log.d(TAG, "accessoryManagerConnection onServiceConnected");
            AccessoryProviderFilter filter = new AccessoryProviderFilter(AccessoryType.PRINTER);
            try {
                accessoryManagerService.getAccessoryProviders(filter, new IPoyntAccessoryManagerListener.Stub() {
                    @Override
                    public void onError(PoyntError poyntError) throws RemoteException {

                    }

                    @Override
                    public void onSuccess(List<AccessoryProvider> list) throws RemoteException {
                        // no reason to do extra work if kitchen printer (item receipt) is not set
                        if (kitchenPrinterName != null) {
                            for (AccessoryProvider printer : list) {
                                if (kitchenPrinterName.equals(printer.getProviderName())){
                                    Log.d(TAG, "found kitchen printer: " + printer.getClassName());

                                    Intent i = new Intent();
                                    i.setClassName(printer.getPackageName(), printer.getClassName());

                                    printerServiceConnection = new ServiceConnection() {
                                        @Override
                                        public void onServiceConnected(ComponentName name, IBinder service) {
                                            poyntPrinterService = IPoyntPrinterService.Stub.asInterface(service);
                                            Log.d(TAG, "connected to Poynt Printer Service");
                                        }

                                        @Override
                                        public void onServiceDisconnected(ComponentName name) {
                                            poyntPrinterService = null;
                                            Log.d(TAG, "disconnected from Poynt Printer Service");
                                        }
                                    };
                                }
                            }
                        }
                    }

                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "accessoryManagerConnection onServiceDisconnected");
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void onDestroy(){
        super.onDestroy();
        if (printerServiceConnection != null){
            unbindService(printerServiceConnection);
        }
        unbindService(accessoryManagerConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*

        Log.d(TAG, "onCreate: " + Locale.getDefault());
        if (Locale.UK.equals(Locale.getDefault())) {
            Log.d(TAG, "onCreate: compare locale works");
        }

        Button button = (Button) findViewById(R.id.zendeskButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
               intent.setAction("poynt.intent.action.VIEW_KNOWLEDGEBASE_ARTICLE");
                intent.putExtra("KNOWLEDGEBASE_ARTICLE_ID", 204637628l);
               // intent.putExtra("KNOWLEDGEBASE_ARTICLE_LABEL", "merchantReleaseNotesJuly2016");
                startActivity(intent);
            }
        });


        // start zendesk activity
        Intent intent = new Intent();
        intent.setAction("poynt.test.zendeskhome");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent);

*/

        Set<String> printerSet = ReceiptPrintingPref.readReceiptPrefsFromDb(this,
                Constants.ReceiptPreference.PREF_ITEM_RECEIPT);
        for (String printer : printerSet){
            Log.d(TAG, "found printer: " + printer);

        }
        if (printerSet != null){
            Iterator<String> iterator = printerSet.iterator();
            if (iterator.hasNext()){
                kitchenPrinterName = iterator.next();
            }
        }

        bindService(Intents.getComponentIntent(new ComponentName("co.poynt.services", "co.poynt.os.services.v1.IPoyntAccessoryManager")),
                accessoryManagerConnection, BIND_AUTO_CREATE);

    }



/*    private void volleyTest() {
        mRequestQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {

            }
        });

        NetworkImageView image1 = (NetworkImageView) findViewById(R.id.image1);
        NetworkImageView image2 = (NetworkImageView) findViewById(R.id.image2);

        String image1Url = "http://www.natochy.com/work/poynt/images/transactionTypes.png";
        String image2Url = "http://www.natochy.com/work/poynt/images/newPaymentTypes.png";
        image1.setImageUrl(image1Url, mImageLoader);
        image2.setImageUrl(image2Url, mImageLoader);
    }*/
}
