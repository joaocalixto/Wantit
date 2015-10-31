package neurotech.com.br.wantit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import neurotech.com.br.wantit.model.App;
import neurotech.com.br.wantit.model.Device;
import neurotech.com.br.wantit.util.Constantes;

public class SplashActivity extends AppCompatActivity {

    private Context context;
    private PackageManager packageManager;
    private String android_id;
    private Pubnub pubnub;
    private Device device;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        new PushDevice().execute();

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
//                finish();
                Context context = getApplicationContext();
                SharedPreferences sharedPref = context.getSharedPreferences("WANTIT_PREF", Context.MODE_PRIVATE);
                String user_name_default = getResources().getString(R.string.user_name_default);
                String username = sharedPref.getString(getString(R.string.saved_user_name), user_name_default);

                Log.i(Constantes.LOG_TAG, "User name = "+username);

                if(username.contains("Gustavo") || username.isEmpty()){

                    Log.i(Constantes.LOG_TAG, "Tela de cadastro");
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, CadastroActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        }, 3500);



    }

    private class PushDevice extends AsyncTask<Void, Integer, Boolean> {

        protected Boolean doInBackground(Void... params) {
            Log.i(Constantes.LOG_TAG, "Do in background");
            pubnub = new Pubnub("pub-c-ec12ad80-33e7-42ad-ad75-c7ab5aafa655", "sub-c-fa690c0e-7a98-11e5-9720-0619f8945a4f");
            device = new Device();
            packageManager = getPackageManager();

            List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

            android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);


            device.setDeviceId(android_id);
            device.setDeviceName(android.os.Build.MODEL);

            List<App> listApp = new ArrayList<>();

            device.setListApp(listApp);
            String vName = "";
            int cont = 0;
            for (ApplicationInfo app : installedApplications) {

                App appTemp = new App();
                appTemp.setName(app.loadLabel(packageManager).toString());
                appTemp.setPackageName(app.packageName.toString());

                vName += app.loadLabel(packageManager) + "\n";

                listApp.add(appTemp);

                if(cont > 100){
                    break;
                }
                cont++;

            }

            Gson g = new Gson();
            String jsonObject = g.toJson(device, Device.class);
            JSONObject jsonObject2 = null;
            try {
                jsonObject2 = new JSONObject(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            pubnub.publish("device-register", jsonObject2, new Callback() {

                public void successCallback(String channel, Object response) {
                    System.out.println(response.toString());
                    Log.i(Constantes.LOG_TAG, "ENVIADO COM SUCESSO");
                }
                public void errorCallback(String channel, PubnubError error) {
                    Log.i(Constantes.LOG_TAG, "ERRO AO ENVIAR");
                    Log.i(Constantes.LOG_TAG, "ERRO code = "+ error.errorCode + "String = "+ error.toString());
                }
            });
            Log.i(Constantes.LOG_TAG, "DEVICE ENVIADO " + jsonObject);
            return true;
        }

        protected void onPostExecute(Long result) {

            Log.i(Constantes.LOG_TAG, "Terminado");
            Intent intent = new Intent();
            intent.setClass(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }


}
