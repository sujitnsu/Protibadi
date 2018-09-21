package com.example.ibrahimchowdhury.protibadi;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {



    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    public static String PREF_NAME = "preference_data";
    public static String PREF_LANG = "current_language";
    String languageString;
    Locale myLocale;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    //LoginButton loginButton;
    TextView textView;
    //CallbackManager callbackManager;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavView;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> ContactArray;
    String address;

    TrackGPS gps;
    double longitude;
    double latitude;
    private RequestQueue requestQueue;

    //for database
    SQLiteDatabase mydatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        _context = getApplicationContext();

        pref = _context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = pref.edit();
        editor.apply();

        gps = new TrackGPS(MainActivity.this);





        /*gps = new TrackGPS(MainActivity.this);
        if(gps.canGetLocation()){
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();
            //Toast.makeText(this,"Viewd",Toast.LENGTH_SHORT).show();
        }*/

        listView = (ListView)findViewById(R.id.contacts);
        textView = (TextView)findViewById(R.id.textview);
        ContactArray =  new ArrayList<String>();
        adapter = new ArrayAdapter<String>(MainActivity.this,R.layout.listcontact,ContactArray);

        requestQueue = Volley.newRequestQueue(this);



        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



       //mPlanetTitles = getResources().getStringArray();
        mNavView = (NavigationView) findViewById(R.id.nav_view);
        if(mNavView != null){
            mNavView.setNavigationItemSelectedListener(this);
        }

    }

    public void deviceConnection(View view){
        Intent intent = new Intent(MainActivity.this,Bluetooth.class);
        intent.putExtra("address",address);
        startActivity(intent);

    }
    public void callem(View view){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:999"));

        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return mToggle.onOptionsItemSelected(item)|| super.onOptionsItemSelected(item);

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        switch (item.getItemId()) {
            case R.id.nav_location:
                getLocation();
                textView.setText(address);
                return true;
            case R.id.nav_message:
                /*SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("01689443975", null, "Nova fuck you", null, null);
                Toast.makeText(this,"sendSMS visit", Toast.LENGTH_SHORT).show();*/
                sendSMS();
                return true;
            case R.id.nav_number:
                ContactArray.clear();
                Intent number = new Intent(MainActivity.this, SaveNumber.class);
                startActivity(number);
                return true;
            case R.id.nav_show_number:
                ContactArray.clear();
                mydatabase = openOrCreateDatabase("protibadi",MODE_PRIVATE,null);
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Contact(Name VARCHAR,Number VARCHAR);");
                Cursor cursor = mydatabase.rawQuery("Select * from Contact",null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()){
                    ContactArray.add(cursor.getString(cursor.getColumnIndexOrThrow("Name"))+" ---- "+cursor.getString(cursor.getColumnIndexOrThrow("Number")));
                    cursor.moveToNext();

                }
                mydatabase.close();
                cursor.close();

                listView.setAdapter(adapter);

                return true;
            case R.id.nav_del_data:
                ContactArray.clear();
                mydatabase = openOrCreateDatabase("protibadi",MODE_PRIVATE,null);
                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Contact(Name VARCHAR,Number VARCHAR);");
                mydatabase.execSQL("DROP table Contact;");
                mydatabase.close();
                return true;
            case R.id.setting:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.settings)
                        .setItems(R.array.language_array, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int position) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch(position) {
                                    case 0:
                                        languageString = "bn";
                                        setLocal("bn");
                                        break;
                                    case 1:
                                        languageString = "en";
                                        setLocal("en");
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;


            default:
                ContactArray.clear();
                return true;
        }


    }

    protected void sendSMS(){
        ContactArray.clear();

        //getLocation();
        SmsManager smsManager = SmsManager.getDefault();
        mydatabase = openOrCreateDatabase("protibadi",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS Contact(Name VARCHAR,Number VARCHAR);");
        Cursor cursor = mydatabase.rawQuery("Select * from Contact",null);
        cursor.moveToFirst();
        while (cursor.isAfterLast()==false){
            Toast.makeText(this,"sendSMS visit", Toast.LENGTH_SHORT).show();
            String M = getResources().getString(R.string.message);
            //smsManager.sendTextMessage(cursor.getString(cursor.getColumnIndexOrThrow("Number")),null,message,null,null);
            //smsManager.sendTextMessage(cursor.getString(cursor.getColumnIndexOrThrow("Number")),null,"I am in Danger. My location is "+address+".",null,null);
            smsManager.sendTextMessage(cursor.getString(cursor.getColumnIndexOrThrow("Number")),null,M.concat(address),null,null);

            ContactArray.add("sent "+cursor.getString(cursor.getColumnIndexOrThrow("Number")));
            cursor.moveToNext();


        }
        listView.setAdapter(adapter);
        mydatabase.close();

    }
    public void getLocation(){
        TrackGPS gps = new TrackGPS(MainActivity.this);
        if(gps.canGetLocation()){
            longitude = gps.getLongitude();
            latitude = gps.getLatitude();

        }
        JsonObjectRequest request = new JsonObjectRequest("https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&key=AIzaSyCR-CzacLYdl6ClRCzIRRfPICPT3SY9cFs", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    address = response.getJSONArray("results").getJSONObject(0).getString
                            ("formatted_address");
                    //Toast.makeText(MainActivity.this,address,Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(request);

    }

    private void setLocal(String language) {

        myLocale = new Locale(language);
        Locale.setDefault(myLocale);
        Resources res = getResources();

        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf,dm);
        editor.putString(PREF_LANG, language);
        editor.commit();
        languageString = language;


        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putString(PREF_LANG, languageString);
        editor.commit();
    }


}
