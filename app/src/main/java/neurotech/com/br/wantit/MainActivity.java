package neurotech.com.br.wantit;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Region;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import neurotech.com.br.wantit.model.App;
import neurotech.com.br.wantit.model.Device;
import neurotech.com.br.wantit.model.Request;
import neurotech.com.br.wantit.model.RequestLocation;
import neurotech.com.br.wantit.util.Constantes;
import neurotech.com.br.wantit.util.LocaisUtil;

import static android.location.LocationManager.NETWORK_PROVIDER;
import static org.json.JSONObject.wrap;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private PackageManager packageManager;
    private Pubnub pubnub;
    private Device device;

    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final String TAG = "BEACONTESTE";
    private static final com.estimote.sdk.Region ALL_ESTIMOTE_BEACONS = new com.estimote.sdk.Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);

    private static final String mint = "FC:5A:29:87:82:DF";
    private static final String blueberry = "EA:7A:88:67:FE:40";
    private static final String ice = "E1:56:9B:61:8E:CB";
    private BeaconManager beaconManager ;
    private TextView txt_loja;
    private TextView txt_view;
    private TextView txt_location;
    private TextView txt_nome;
    private LocaisUtil locaisUtil;
    private String loja_string_mais_prox;
    private  LocationManager locationManager;
    private String android_id;
    private String localMapName = "";
    private TextView txt_location_street;
    String username = "";

    private Geocoder geocoder;

    String appId = "wantit-kcu";
    String appToken = "c70f1ac22451840d83286bb08c1293d3";

    private static final String[] TAGS = new String[] { "Puma",
            "Nike", "Tenis", "Televisao", "camisa", "Polo" };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_view = (TextView) findViewById(R.id.autoCompleteTextView);
        txt_location = (TextView) findViewById(R.id.txt_location);
        txt_loja = (TextView) findViewById(R.id.txt_loja);
        txt_nome = (TextView) findViewById(R.id.textView2);

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("WANTIT_PREF", Context.MODE_PRIVATE);
        String user_name_default = getResources().getString(R.string.user_name_default);
        username = sharedPref.getString(getString(R.string.saved_user_name), user_name_default);

        txt_nome.setText(username);



        // txt_location_street = (TextView) findViewById(R.id.txt_location_street);
        locaisUtil = new LocaisUtil();

        beaconManager = new BeaconManager(this);

        EstimoteSDK.initialize(this, appId, appToken);
        beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(3), 0);
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        pubnub = new Pubnub("pub-c-ec12ad80-33e7-42ad-ad75-c7ab5aafa655", "sub-c-fa690c0e-7a98-11e5-9720-0619f8945a4f");

        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {


            @Override
            public void onBeaconsDiscovered(com.estimote.sdk.Region region, List<Beacon> beacons) {
                double menorValor = Double.MAX_VALUE;
                for (Beacon beaconTemp : beacons) {
                    String macBeaconMaisProximo = "";

                    double beaconTempDistance = Utils.computeAccuracy(beaconTemp);

//                    Log.i(Constantes.LOG_TAG, "menor valor = " + menorValor);
//                    Log.i(Constantes.LOG_TAG, "Distancia = " + beaconTempDistance);

                    if (beaconTempDistance < menorValor) {
                        macBeaconMaisProximo = beaconTemp.getMacAddress().toString();
                        menorValor = beaconTempDistance;
                        String placeLoja = locaisUtil.getPlace(macBeaconMaisProximo);

                        loja_string_mais_prox = placeLoja;
//                        txt_loja.setText("Ultima loja visitada = " + placeLoja);
                       // Log.i(Constantes.LOG_TAG, loja_string_mais_prox);
                        txt_loja.setText(loja_string_mais_prox);

                        RequestLocation request = new RequestLocation();
                        request.setDeviceId(android_id);
                        request.setLoja(placeLoja);
                        request.setDateTime(null);
                        request.setLocal(localMapName);

                        Gson g = new Gson();
                        String requestJson = g.toJson(request, RequestLocation.class);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(requestJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pubnub.publish("location-tracker", jsonObject, new Callback() { });
                        Log.i(Constantes.LOG_TAG, "location-json= "+requestJson);
                    }
                }
            }
        });

        geocoder = new Geocoder(this, Locale.getDefault());

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.

                List<Address> addresses = null;


                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String local = "Longitude = " + location.getLatitude() + " Latitude = "+location.getLongitude();

                if(addresses != null){
                    String localName = "Local: "+ addresses.get(0).getAddressLine(0).toString() + " Name: "+addresses.get(0).getFeatureName().toString();


                    localMapName = locaisUtil.getPlace(addresses.get(0).getAddressLine(0).toString());
                   // txt_location_street.setText(addresses.get(0).getAddressLine(0).toString());

                    RequestLocation request = new RequestLocation();
                    request.setDeviceId(android_id);
                    request.setLoja("");
                    request.setLocal(localMapName);
                    request.setDateTime(null);

                    Gson g = new Gson();
                    String requestJson = g.toJson(request, RequestLocation.class);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(requestJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pubnub.publish("location-tracker", jsonObject, new Callback() { });
                    txt_location.setText(localMapName);

                   Log.i(Constantes.LOG_TAG, "local-tracker = " + requestJson);
                }

                Log.i(Constantes.LOG_TAG, local);
                // makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(NETWORK_PROVIDER, 0, 0, locationListener);

        try {
            pubnub.subscribe("busca-resposta", new Callback() {
                        @Override
                        public void connectCallback(String channel, Object message) {
                            Log.i("SSSSSUUB =-CONECTADO", "Chanel = "+ channel + " Message="+ message.toString());
                            // pubnub.publish("teste", "Hello from the PubNub Java SDK = ANDROID", new Callback() {});
                        }

                        @Override
                        public void successCallback(String channel, Object message) {
                            Log.i("SSSSSUUB =-SUCESSO", "Chanel = " + channel + " Message=" + message.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, TAGS);
        AutoCompleteTextView textView = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextView);
        textView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                } catch (RemoteException e) {
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

    public void buscaProduto(View v) throws JSONException {

        Request request = new Request();
        request.deviceid = android_id;
        request.acao = "busca";
        String textView = txt_view.getText().toString();

        ArrayList<String> strings = new ArrayList<String>(Arrays.asList(textView.split(" ")));
        request.taglist = strings;

        Gson g = new Gson();
        String requestJson = g.toJson(request, Request.class);
        JSONObject wrap = (JSONObject) wrap(request);

        JSONObject jsonObject = new JSONObject(requestJson);
        pubnub.publish("busca", jsonObject, new Callback() {  });

        Toast.makeText(getApplicationContext(),  username + " sua busca enviada para lojistas", Toast.LENGTH_LONG).show();
        Log.i(Constantes.LOG_TAG, "BUSCA ENVIADA = " + requestJson);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
