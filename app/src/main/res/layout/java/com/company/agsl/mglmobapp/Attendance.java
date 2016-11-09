package layout.java.com.company.agsl.mglmobapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by Santa5 on 4/29/2015.
 */
public class Attendance extends Activity {
    TextView dateView,mobNumb,chkInText,chkOutText,locationLatLong;
    Button chkIn,chkOut,myReports,myTransactions;
    private UserLoginTask1 mAuthTask = null;
    private getCheckInOutStatus mChkInStatus=null;
    Location currentLocation;
    String number="";
    String Imei="";
    int index1=0;
    String[] strs;
    ImageButton refreshBut;
    String empCode,empId;
    GPSTracker gps;
    String getChkInTime="",getChkoutTime="";
    String getChkoutDate="",getChkInDate="";
    double latitude,longitude=0;
    String latString,longString,formattedDate;
    String connString="http://14.141.125.83:82/Mobile_Service_AGSL/login.asmx";
    private boolean doubleBackToExitPressedOnce = false;
   // String connString="http://23.253.164.20:8096/login.asmx";
    private ProgressDialog progressBar;
    SimpleDateFormat df;
    SharedPreferences.Editor edit;
    String FILENAME ="Mydata.txt";
    SharedPreferences pref;
    String myData1;
    Boolean checkedIn=false;
    Boolean checkedOut=false;
    String appVersion="";
    String updateAppversion="";
    private getAppVersion mgetVersion = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance);
        connString= ((GlobalVariables) this.getApplication()).getconnString();
        refreshBut= (ImageButton) findViewById(R.id.refreshLatLong);
        myReports= (Button) findViewById(R.id.myReportsbutton);
        myTransactions=(Button)findViewById(R.id.lastTransactionbutton);
        locationLatLong= (TextView) findViewById(R.id.latlong);
        locationLatLong.setText("");
        gps = new GPSTracker(Attendance.this);
        dateView= (TextView) findViewById(R.id.dtText);
        mobNumb=(TextView) findViewById(R.id.mobNumber);
        chkInText=(TextView) findViewById(R.id.dateTimeChkIn);
        chkOutText=(TextView) findViewById(R.id.dateTimeChkOut);
        chkIn=(Button)findViewById(R.id.chkInBut);
        chkOut=(Button)findViewById(R.id.chkOutBut);
        Calendar c = Calendar.getInstance();
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        Imei=tm.getDeviceId();
        mobNumb.setText("Mobile Number : "+number);
        pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        edit = pref.edit();
        number = pref.getString("mobile", "");
        mobNumb.setText(number);
        df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c.getTime());
        final String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        dateView.setText(formattedDate);
        System.out.println("asdasd as "+gps.canGetLocation());
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            empId=    getIntent().getStringExtra("EmployeeId");
            empCode = extras.getString("EmployeeCode");
        }
        if(checkInternetConenction()) {
            mChkInStatus = new getCheckInOutStatus(empCode);
            mChkInStatus.execute((Void) null);
            mgetVersion = new getAppVersion();
            mgetVersion.execute((Void) null);
        }
        //System.out.println("Boolean : "+ checkGPS(this));

        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            latString= String.valueOf(latitude);
            longString= String.valueOf(longitude);
            locationLatLong.setText("Lat: " + latitude + "\nLong: " + longitude);
        }else{
            showSettingsAlert1();
        }
        appVersion= String.valueOf(getApplicationVersionCode(Attendance.this));

        myReports.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {

                                           if(Utils.isOnline(Attendance.this)){
                                             Intent myIntent = new Intent(Attendance.this , Reports.class );
                                             myIntent.putExtra("EmployeeCode", empCode);
                                             startActivity(myIntent);
                                         }else{
                                                Toast.makeText(Attendance.this, "Internet not available.Plz check and try again", Toast.LENGTH_SHORT).show();
                                           }
                                         }
                                     });

        myTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isOnline(Attendance.this)){
                Intent myIntent = new Intent(Attendance.this , TransactionReport.class );
                myIntent.putExtra("EmployeeCode", empCode);
                startActivity(myIntent);
                //Toast.makeText(getApplicationContext(), "Coming Soon",  Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Attendance.this, "Internet not available.Plz check and try again", Toast.LENGTH_SHORT).show();
                }
            }

        });
        if(gps.canGetLocation()) {

                Date date = new Date(gps.getTime());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                String myDate = sdf.format(date);
                String myDate1 = sdf1.format(date);
                String getDate = pref.getString("date", "");
                System.out.println(getDate + "||" + myDate1);
                if (getDate.equals(myDate1)) {
                    checkedIn = pref.getBoolean("chkIn", false);
                    checkedOut = pref.getBoolean("chkOut", false);
                    System.out.println(checkedIn + "||" + checkedOut);
                    if (checkedIn) {
                        chkIn.setEnabled(false);
                        chkIn.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.grey));
                        chkIn.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        String datetime = pref.getString("chkInDatetime", "");
                        chkInText.setText(datetime);
                    }
                    if (checkedOut) {
                        chkOut.setEnabled(false);
                        chkOut.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.grey));
                        chkOut.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        String datetime = pref.getString("chkOutDatetime", "");
                        chkOutText.setText(datetime);
                    }
                } else {
                    if(!myDate1.equals(1970-01-01)) {
                        System.out.println("Commiting");
                        edit.putBoolean("chkIn", false);
                        edit.putBoolean("chkOut", false);
                        edit.putString("date", myDate1);
                        edit.putString("chkInDatetime", "");
                        edit.putString("chkOutDatetime", "");
                        edit.commit();
                    }
                }

        }

        //Intent intent = new Intent(Attendance.this, HelloService.class);
        //startService(intent);

        refreshBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (gps.canGetLocation()) {
                //clear();
                //edit.putBoolean("chkIn",false);
               //edit.putBoolean("chkOut",false);
                //edit.putString("date", "");
               // edit.commit();
                gps = new GPSTracker(Attendance.this);
                //LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if(gps.canGetLocation()) {

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    latString = String.valueOf(latitude);
                    longString = String.valueOf(longitude);
                    locationLatLong.setText("Lat: " + latitude + "\nLong: " + longitude);
                    System.out.println("latSting "+latString+"||"+longString+"llll"+latitude);
                    refreshThings();
                }else{
                   showSettingsAlert1();
                }
            }
        });

        chkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("chkin | " + gps.canGetLocation());
                gps = new GPSTracker(Attendance.this);
                if (gps.canGetLocation()) {


                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        latString = String.valueOf(latitude);
                        longString = String.valueOf(longitude);
                        locationLatLong.setText("Lat: " + latitude + "\nLong: " + longitude);
                    System.out.println("latSting "+latString+"||"+longString);
                    if (!latString.equals("0.0") || (!longString.equals("0.0"))) {
                        dateView.setText(formattedDate);
                        mobNumb.setText(number);
                        String currentDateTimeStringChkIn;
                        String type = "1";

                        currentDateTimeStringChkIn = DateAndTime();
                        chkInText.setText(currentDateTimeStringChkIn);
                        progressBar = new ProgressDialog(Attendance.this);
                        progressBar.setTitle("Please Wait");
                        progressBar.setMessage("Mobile Attendance Punching ");
                        progressBar.setCancelable(true);
                        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressBar.show();
                        if (checkInternetConenction()) {
                            mAuthTask = new UserLoginTask1(empId, empCode, number, Imei, type, latString, longString);
                            mAuthTask.execute((Void) null);
                        } else {
                            if (!latString.equals("0.0") || (!longString.equals("0.0"))) {
                                Date date = new Date(gps.getTime());
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                currentDateTimeStringChkIn = sdf.format(date);
                                String myData = currentDateTimeStringChkIn + "|" + empId + "|" + empCode + "|" + number + "|" + Imei + "|" + type + "|" + latString + "|" + longString + "|" + "2" + "^";
                                disableChkInbutton(myData, currentDateTimeStringChkIn);
                                progressBar.dismiss();
                            }else{
                                Log.e("MGL", "chk in Lat Long 00");

                            }
                        }
                    }
                    else{
                        if(gps.canGetLocation()) {

                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                            latString = String.valueOf(latitude);
                            longString = String.valueOf(longitude);
                            locationLatLong.setText("Lat: " + latitude + "\nLong: " + longitude);
                            System.out.println("latSting "+latString+"||"+longString+"llll"+latitude);
                            refreshThings();
                        }else{
                            showSettingsAlert1();
                        }
                        System.out.println("in Else LOOPP");
                    }
                }else {
                    showSettingsAlert1();
                }
            }
        });
        chkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(gps.canGetLocation());
                if (gps.canGetLocation()) {

                    if (!latString.equals("0.0") || (!longString.equals("0.0"))) {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        latString = String.valueOf(latitude);
                        longString = String.valueOf(longitude);
                        locationLatLong.setText("Lat: " + latitude + "\nLong: " + longitude);
                        dateView.setText(formattedDate);
                        mobNumb.setText(number);
                        String currentDateTimeStringChkOut;
                        String type = "2";
                        currentDateTimeStringChkOut = DateAndTime();
                        chkOutText.setText(currentDateTimeStringChkOut);
                        progressBar = new ProgressDialog(Attendance.this);
                        progressBar.setTitle("Please Wait");
                        progressBar.setMessage("Mobile Attendance Punching ");
                        progressBar.setCancelable(true);
                        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressBar.show();
                        if (checkInternetConenction()) {
                            mAuthTask = new UserLoginTask1(empId, empCode, number, Imei, type, latString, longString);
                            mAuthTask.execute((Void) null);
                        } else {
                            if (!latString.equals("0.0") || (!longString.equals("0.0"))) {
                                Date date = new Date(gps.getTime());
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                currentDateTimeStringChkOut = sdf.format(date);
                                String myData = currentDateTimeStringChkOut + "|" + empId + "|" + empCode + "|" + number + "|" + Imei + "|" + type + "|" + latString + "|" + longString + "|" + "2" + "^";
                                disableChkOutbutton(myData, currentDateTimeStringChkOut);
                                progressBar.dismiss();
                            }
                            else{
                                Log.e("MGL", "Lat Long 00");
                               }
                        }
                        //Toast.makeText(Attendance.this,currentDateTimeStringChkOut,Toast.LENGTH_SHORT);

                    }else{
                        if(gps.canGetLocation()) {

                            latitude = gps.getLatitude();
                            longitude = gps.getLongitude();
                            latString = String.valueOf(latitude);
                            longString = String.valueOf(longitude);
                            locationLatLong.setText("Lat: " + latitude + "\nLong: " + longitude);
                            System.out.println("latSting "+latString+"||"+longString+"llll"+latitude);
                            refreshThings();
                        }else{
                            showSettingsAlert1();
                        }
                    }
                }else {

                    showSettingsAlert1();
                }
            }
        });

       // DateAndTime();
    }

    public void refreshThings(){
        if(gps.canGetLocation()) {

            Date date = new Date(gps.getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            String myDate = sdf.format(date);
            String myDate1 = sdf1.format(date);
            String getDate = pref.getString("date", "");
            System.out.println(getDate + "||" + myDate1);
            if (getDate.equals(myDate1)) {
                checkedIn = pref.getBoolean("chkIn", false);
                checkedOut = pref.getBoolean("chkOut", false);
                System.out.println(checkedIn + "||" + checkedOut);
                if (checkedIn) {
                    chkIn.setEnabled(false);
                    chkIn.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.grey));
                    chkIn.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                    String datetime = pref.getString("chkInDatetime", "");
                    chkInText.setText(datetime);
                }
                if (checkedOut) {
                    chkOut.setEnabled(false);
                    chkOut.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.grey));
                    chkOut.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                    String datetime = pref.getString("chkOutDatetime", "");
                    chkOutText.setText(datetime);
                }
            } else {
                if(!myDate1.equals("1970-01-01")) {
                    System.out.println("Commiting");
                    edit.putBoolean("chkIn", false);
                    edit.putBoolean("chkOut", false);
                    edit.putString("date", myDate1);
                    edit.putString("chkInDatetime", "");
                    edit.putString("chkOutDatetime", "");
                    edit.commit();
                }
            }

        }

    }
    public void clear()
    {
        SharedPreferences prefs=getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }
    public void disableChkInbutton(String myData,String DatechkIN){
        myData1 =readContenthere(FILENAME);
        writeContent(myData1,myData);
        readContenthere(FILENAME);
        checkedIn=true;
        System.out.println("CheckIN");
        chkIn.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.grey));
        chkIn.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        chkIn.setEnabled(false);
        writeChkInstatus(DatechkIN);
    }
    public void disableChkOutbutton(String myData,String DateChkout){
        myData1 =readContenthere(FILENAME);
        writeContent(myData1,myData);
        System.out.println("CheckOUT");
        checkedOut=true;
        chkOut.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.grey));
        chkOut.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        chkOut.setEnabled(false);
        writeChkOutstatus(DateChkout);
    }
    public void enableChkInbutton(){
        chkIn.setBackgroundResource(0);
        chkIn.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
        //chkIn.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.grey));
        //chkIn.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        chkIn.setEnabled(true);

    }

    public void enableChkOutbutton(String myData,String DateChkout){
        myData1 =readContenthere(FILENAME);
        writeContent(myData1,myData);
        System.out.println("CheckOUT");
        checkedOut=true;
        chkOut.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.grey));
        chkOut.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        chkOut.setEnabled(false);
        writeChkOutstatus(DateChkout);
    }
    public void writeChkOutstatus(String DateChkout){
       edit.putBoolean("chkOut",true);
       edit.putString("chkOutDatetime",DateChkout);
       edit.commit();
   }
    public void writeChkInstatus(String DateChkIn){
        edit.putBoolean("chkIn",true);
        edit.putString("chkInDatetime",DateChkIn);
        edit.commit();
    }
    public Boolean readChkinStatus(){
        Boolean checkedIn = pref.getBoolean("chkIn", false);
        return checkedIn;
    }
    public Boolean readChkoutStatus(){
        Boolean checkedOut = pref.getBoolean("chkOut", false);
        return checkedOut;
    }
    public String readChkinDATETIME(){
        String checkedIn = pref.getString("chkInDatetime", "");
        return checkedIn;
    }
    public String readChkoutDATETIME(){
        String checkedOut = pref.getString("chkOutDatetime", "");
        return checkedOut;
    }
    public void resetChkOutstatus(){
        edit.putBoolean("chkOut",false);
        edit.putString("chkOutDatetime","");
        edit.commit();
    }
    public void resetChkInstatus(){
        edit.putBoolean("chkIn",false);
        edit.putString("chkInDatetime","");
        edit.commit();
    } 
    public String getGPSTime()   {
       Date date = new Date(gps.getTime());

       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
       SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
       String myOrganisedDatetime= sdf.format(date);
       String myDate1= sdf1.format(date);
       String getDate=pref.getString("date", "");
       return myOrganisedDatetime;
   }
    public void writeContent(String prev,String next) {
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write((prev + next).getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void clearContent(String file){
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(("").getBytes());
            fos.close();
            readContenthere(FILENAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String readContenthere(String file){
        String myData_="";
        try{ FileInputStream fis = openFileInput (FILENAME);
            byte[] reader = new byte[fis.available ()];
            if (fis.read(reader)!=-1)
            {
                myData_ = new String(reader);
                System.out.println("abctxts:"+myData_);
            }
            fis.close();
        }
        catch(Exception ex)
        {
            System.out.println("Exception ::" + ex.getMessage());
        }
        return myData_;
    }
    public String readContent(String file){
        String myData_="";
        try{ FileInputStream fis = openFileInput (getFilesDir() +"/"+FILENAME);
            byte[] reader = new byte[fis.available ()];
            if (fis.read(reader)!=-1)
            {
                myData_ = new String(reader);
                System.out.println("abctxt:"+myData_);
            }
            fis.close();
        }
        catch(Exception ex)
        {
            System.out.println("Exception ::"+ex.getMessage());
        }
        return myData_;
    }
    private boolean checkInternetConenction(){
        ConnectivityManager check = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (check != null)
        {
            NetworkInfo[] info = check.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i <info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
            return false;
        }
        else{
            return false;
        }
    }
    public String DateAndTime() {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
            String strDate = sdf.format(cal.getTime());
            System.out.println("Current date in String Format: " + strDate);

            SimpleDateFormat sdf1 = new SimpleDateFormat();
            sdf1.applyPattern("MM-dd-yyyy HH:mm");
        Date date = null;
        try {
            date = sdf1.parse(strDate);
        } catch (ParseException e) {
            System.out.println("Exception2 ::  " + e.getMessage());
            e.printStackTrace();
        }
        String string=sdf1.format(date);
            System.out.println("Current date in Date Frmat: " + string);

        return string;
    }
    public void showSettingsAlert1(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                Attendance.this.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    public class UserLoginTask1 extends AsyncTask<Void, Void, Boolean> {

        private final String empId;
        private final String empCode;
        private final String Mobile;
        private final String IMEI;
        private final String workType;
        private final String latLocation;
        private final String longLocation;

        UserLoginTask1(String empIdnum, String empCodenum , String Mobilenum,String IMEInum,String workTypenum,String latitude,String longitude) {
            empId = empIdnum;
            empCode=empCodenum;
            Mobile = Mobilenum;
            IMEI =IMEInum;
            workType=workTypenum;
            latLocation= latitude;
            longLocation=longitude;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String responseAsText="";
            Boolean chkFlag;
            HttpPost httpPost;
            List<NameValuePair> list;
            HttpClient httpClient=new DefaultHttpClient();
            System.out.println("empId :: " + empId);
            System.out.println("EmployeeCode :: " + empCode);
            System.out.println("Mobile :: " + Mobile);
            System.out.println("IMEI :: " + IMEI);
            System.out.println("latLocation :: " + latLocation);
            System.out.println("longLocation :: " + longLocation);
            System.out.println("worktype :: " + workType +"||| "+connString+"/insert_MobileTransaction");

            if(workType.equals(1)){

                //httpPost=new HttpPost("http://23.253.164.20:8096/login.asmx/insert_MobileTransaction");
                httpPost=new HttpPost(connString+"/insert_MobileTransaction");
                list=new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("EmployeeID", empId));
                list.add(new BasicNameValuePair("EmployeeCode", empCode));
                list.add(new BasicNameValuePair("Mobile", Mobile));
                list.add(new BasicNameValuePair("IMEI", IMEI));
                list.add(new BasicNameValuePair("Type",workType));
                list.add(new BasicNameValuePair("Latitude",latLocation));
                list.add(new BasicNameValuePair("Longitude",longLocation));
            }
            else{
                httpPost=new HttpPost(connString+"/insert_MobileTransaction");
                list=new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("EmployeeID", empId));
                list.add(new BasicNameValuePair("EmployeeCode", empCode));
                list.add(new BasicNameValuePair("Mobile", Mobile));
                list.add(new BasicNameValuePair("IMEI", IMEI));
                //list.add(new BasicNameValuePair("Checkin", chkInOutTime));
                list.add(new BasicNameValuePair("Type",workType));
                list.add(new BasicNameValuePair("Latitude",latLocation));
                list.add(new BasicNameValuePair("Longitude",longLocation));
            }




            try {
                // Simulate network access.
                // Thread.sleep(2000);
                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                String responseStr = EntityUtils.toString(httpResponse.getEntity());
                responseAsText = android.text.Html.fromHtml(responseStr).toString();
                // String s = readResponse(httpResponse);
                System.out.println("Response: " + responseAsText);

            }
            catch (Exception e) {
                return false;
            }

           /* for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;*/
            if (responseAsText.equals("Inserted Successfully")) {
                chkFlag=true;
                return chkFlag;
            } else {
                chkFlag=false;
                return chkFlag;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            progressBar.dismiss();
            mAuthTask = null;
           // showProgress(false);

            System.out.println("bgb" + success+"::"+workType);
            if (success) {
                if(workType.equals("1")){
                    /*chkIn.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.grey));
                    chkIn.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                    chkIn.setEnabled(false);
                    checkedIn=true;
                    edit.putBoolean("chkIn",true);
                    edit.commit();*/
                    String getOrganisedDatetime = getGPSTime();
                    String myData = getOrganisedDatetime+"|"+empId+"|"+ empCode+"|"+ number+"|"+ Imei+"|"+ "1"+"|"+latLocation+"|"+longLocation+"|"+"1"+"^";
                    disableChkInbutton(myData,getOrganisedDatetime);
                    Toast.makeText(getApplicationContext(), "CheckIn Attendance Punched Successfully", Toast.LENGTH_SHORT).show();
                }else {
                    String getOrganisedDatetime = getGPSTime();
                    String myData = getOrganisedDatetime+"|"+empId+"|"+ empCode+"|"+ number+"|"+ Imei+"|"+ "1"+"|"+latLocation+"|"+longLocation+"|"+"1"+"^";
                    disableChkOutbutton(myData,getOrganisedDatetime);


                   /* chkOut.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.grey));
                    chkOut.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                    chkOut.setEnabled(false);
                    checkedOut=true;
                    edit.putBoolean("chkOut",true);
                    edit.commit();*/


                    Toast.makeText(getApplicationContext(), "CheckOut Attendance Punched Successfully", Toast.LENGTH_SHORT).show();
                }

            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
               // mPasswordView.requestFocus();
                Toast.makeText(getApplicationContext(), "Not Inserted Successfully.Please check your GPS or Internet", Toast.LENGTH_SHORT).show();
                gps = new GPSTracker(Attendance.this);
                //LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if(gps.canGetLocation()) {

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    latString = String.valueOf(latitude);
                    longString = String.valueOf(longitude);
                    locationLatLong.setText("Lat: " + latitude + "\nLong: " + longitude);
                    refreshThings();
                }else{
                    //showSettingsAlert1();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
            progressBar.dismiss();
        }


    }
    public class getCheckInOutStatus extends AsyncTask<Void, Void, Boolean> {
        private final String empCode;
        getCheckInOutStatus(String empCodenum) {
            empCode=empCodenum;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String responseAsText="";
            Boolean chkFlag;
            HttpPost httpPost;
            List<NameValuePair> list;
            HttpClient httpClient=new DefaultHttpClient();

            System.out.println("EmployeeCode :: " + empCode);
            System.out.println("||| "+connString+"/getcheckinout_time");
                httpPost=new HttpPost(connString+"/getcheckinout_time");
                list=new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("EmployeeCode", empCode));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                String responseStr = EntityUtils.toString(httpResponse.getEntity());
                responseAsText = android.text.Html.fromHtml(responseStr).toString();
                System.out.println("Response: " + responseAsText);
                //1^7 1^7 1^9 2^8 2^7 1^7 1^7
                strs = responseAsText.split("[|| ]");
                System.out.println("Splitting String using split() method in Java");
                    for(index1=0; index1 < strs.length; index1++) {
                        System.out.println("Split : " + strs[index1]);
                        if((strs[index1].equals("1^7")) || (strs[index1].equals("1^8"))) {

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    chkIn.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.grey));
                                    chkIn.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                                    //chkFlag=true;
                                    chkIn.setEnabled(false);
                                }
                            });
                            System.out.println("Split Dat : " + strs[index1 + 1]);
                            getChkInDate=strs[index1+1];
                            getChkInTime=strs[index1+2];
                            String[] parts=getChkInTime.split(":");
                            getChkInTime=parts[0]+":"+parts[1];
                            System.out.println("Split Time: " + strs[index1+2]);
                            writeChkInstatus(getChkInDate + " " + getChkInTime);
                            //return chkFlag;

                        }
                        else if((strs[index1].equals("2^7")) || (strs[index1].equals("2^8"))){


                            runOnUiThread(new Runnable() {
                                public void run() {
                                    chkOut.setBackgroundDrawable(getApplicationContext().getResources().getDrawable(R.drawable.grey));
                                    chkOut.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                                    chkOut.setEnabled(false);

                                }
                            });
                            System.out.println("SplitOut Dat : " + strs[index1 + 1]);
                            getChkoutDate=strs[index1+1];
                            System.out.println("SplitOut Time: " + strs[index1+2]);
                            getChkoutTime=strs[index1+2];
                            String[] parts=getChkoutTime.split(":");
                            getChkoutTime=parts[0]+":"+parts[1];
                            writeChkOutstatus(getChkoutDate+" "+getChkoutTime);
                            //chkFlag=true;

                           // return chkFlag;
                        }
                        else if((strs[index1].equals("1^9")) || (strs[index1].equals("2^9"))){
                            String getstoredDAte=readChkinDATETIME();
                            String getstoredchkoutDAte=readChkoutDATETIME();
                            System.out.println("stored datetime"+getstoredDAte+"||"+getstoredchkoutDAte);
                            System.out.println("SplitOut Dat : " + strs[index1 + 1]);
                            String getChkoutDate=strs[index1+1];
                            System.out.println("SplitOut Time: " + strs[index1+2]);
                            String getChkoutTime=strs[index1+2];
                            String[] parts=getChkoutTime.split(":");
                            getChkoutTime=parts[0]+":"+parts[1];
                            System.out.println("database datetime"+getChkoutDate+" "+getChkoutTime);
                            if(getstoredDAte.equals(getChkoutDate+" "+getChkoutTime)){
                                System.out.println("matched ChkIN");
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        chkIn.setBackgroundResource(R.drawable.mgl_header_darkblue);
                                        chkIn.setEnabled(true);
                                        chkInText.setText("");
                                        resetChkInstatus();
                                        //clearContent(FILENAME);
                                    }
                                });

                            }
                            else if(getstoredchkoutDAte.equals(getChkoutDate+" "+getChkoutTime)){
                                System.out.println("matched ChkOut");
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        chkOut.setBackgroundResource(R.drawable.mgl_header_darkblue);
                                        chkOut.setEnabled(true);
                                        chkOutText.setText("");
                                        resetChkOutstatus();
                                       // clearContent(FILENAME);
                                    }
                                });

                            }


                        }


                    }

            }

            catch (Exception e) {
                System.out.println("Exception :: "+e.getMessage());
                return false;
            }
            runOnUiThread(new Runnable() {
                              public void run() {
                                  checkedIn=pref.getBoolean("chkIn",false);
                                  checkedOut=pref.getBoolean("chkOut",false);
                                  if(getChkInDate.equals("")&& getChkInTime.equals("")){

                                  }else{
                                      chkInText.setText(getChkInDate + " " + getChkInTime);
                                  }
                                  if(getChkoutTime.equals("")&& getChkoutDate.equals("")){

                                  }else{
                                      chkOutText.setText(getChkoutDate + " " + getChkoutTime);
                                  }


                              }
                          });
           /* for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;*/
                chkFlag=false;
                return chkFlag;
            }

            @Override
        protected void onPostExecute(final Boolean success) {
           // progressBar.dismiss();
            mAuthTask = null;
            // showProgress(false);

            System.out.println("bgb" + success);
            if (success) {

              //  Toast.makeText(getApplicationContext(), "You can check ",  Toast.LENGTH_SHORT).show();
                //Intent myIntent = new Intent(LoginActivity.this , MenuActivity.class );
                //myIntent.putExtra("user", user);
                //startActivity(myIntent);
                //finish();
            } else {
                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                // mPasswordView.requestFocus();
               // Toast.makeText(getApplicationContext(), "Not Inserted Successfully",  Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
           // progressBar.dismiss();
        }

        public void showToast(String msg) {
            Toast.makeText(Attendance.this, msg, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Attendance.this.finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    public class getAppVersion extends AsyncTask<Void, Void, String> {
        getAppVersion() { }
        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String responseAsText="";
            String httpRequest=connString+"/Version";
            System.out.println("Coonstring : " + httpRequest);
            String chkFlag;
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(httpRequest);
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                String responseStr = EntityUtils.toString(httpResponse.getEntity());
                responseAsText = android.text.Html.fromHtml(responseStr).toString();
                System.out.println("Response: " + responseAsText);
            }
            catch (Exception e) {
                return "Exception"+e.getMessage();
            }
            chkFlag=responseAsText;
            // return chkFlag;
            updateAppversion=responseAsText;
            System.out.println("APPVERSION:"+appVersion+" && UPDATEVERSION IS "+updateAppversion);

            if(Integer.parseInt(appVersion)>= Integer.parseInt(updateAppversion)){
               // System.out.println("APPVERSION:"+appVersion+" && UPDATEVERSION IS "+updateAppversion);
                return "0";
            }
            else {
                try {
                    URL url = new URL("http://122.15.117.203/SmartI/MobileApp/app-debug.apk");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    //urlConnection.setDoOutput(true);
                    urlConnection.connect();

                    File sdcard = Environment.getExternalStorageDirectory();
                    File file = new File(sdcard, "mglapp.apk");
                    System.out.println(sdcard+"/mglapp.apk");
                    File parent = file.getParentFile();
                    if (parent != null) parent.mkdirs();
                    FileOutputStream fileOutput = new FileOutputStream(file);
                    InputStream inputStream = urlConnection.getInputStream();
                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;
                    while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                        fileOutput.write(buffer, 0, bufferLength);
                    }
                    fileOutput.close();
                    inputStream.close();
                    return "1";
                    //this.checkUnknownSourceEnability();
                    //this.installApk();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("in else APPVERSION:" + appVersion + " && UPDATEVERSION IS " + updateAppversion);
            }


            return chkFlag;

        }


        protected void onPostExecute(final String response) {
            mgetVersion= null;
            System.out.println("Appversion" + response);
            // updateAppversion="2";
            if(response.equals("1")){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Attendance.this);
                // set title for your alert dialog box
                alertDialogBuilder.setTitle("Update is Available..");
                // set alert dialog message
                alertDialogBuilder
                        .setMessage("Click yes to download & update!")  // Set the message to display.
                        .setCancelable(false) //  Sets whether the dialog is cancelable or not.
// Set a listener to be invoked when the positive button of the dialog is pressed.
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if yes button is clicked, close current activity
                                // MainActivity.this.finish();
                                Utils.installApk(Attendance.this);
                            }
                        })
//Set a listener to be invoked when the negative button of the dialog is pressed
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if no button is clicked, just close the dialog box and do nothing
                                dialog.cancel();
                            }
                        });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // Display alert dialog
                alertDialog.show();
            }
            else{
                System.out.println("No Update found");
            }

        }



        @Override
        protected void onCancelled() {
            mgetVersion = null;
        }


    }
    public static int getApplicationVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            PackageInfo info = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            String version = info.versionName;
            System.out.println("Version from "+version);
            System.out.println("Version Code "+packageInfo.versionCode);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {} catch(Exception e){}
        return 0;
    }
}
