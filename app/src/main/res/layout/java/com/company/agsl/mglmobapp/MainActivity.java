package layout.java.com.company.agsl.mglmobapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class MainActivity extends ActionBarActivity {
    boolean statusOfGPS=false;
    private UserLoginTask mAuthTask = null;
    private getServerLink mServerLink = null;
    private ProgressBar mProgressView;
    ImageButton changeMobNum;
    private ProgressDialog progressBar;
    Boolean gotServerLink=false;
    TextView tv;
    SharedPreferences.Editor edit;
    String number,empcode,Imei;
    String IMEI_numbers;
    String FILENAME ="Mydata.txt";
   // String connString="http://14.141.125.83:82/Mobile_Service_AGSL/login.asmx";
    String connString="";
    String ConstantconnString="http://122.15.117.203/Mobile_Service_AGSL/login.asmx";
   // String ConstantconnString="http://14.141.125.83:82/Mobile_Service_AGSL/login.asmx";
    String mobNumShared;
    //String connString="http://23.253.164.20:8096/login.asmx";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mServerLink = new getServerLink();
        mServerLink.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        connString= ((GlobalVariables) this.getApplication()).getconnString();
        //System.out.println("Version Code:" + getApplicationVersionCode(this));
        Intent intent = new Intent(MainActivity.this, HelloService.class);
        startService(intent);
        SharedPreferences pref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        File yourFile = new File("Mydata.txt");
        if(!yourFile.exists()) {
            try {
                yourFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        changeMobNum= (ImageButton) findViewById(R.id.changeMob);
        // We need an editor object to make changes
        edit = pref.edit();

        tv= (TextView) findViewById(R.id.textView4);
        System.err.println("setContentView: " + "..............");
        mobNumShared = pref.getString("mobile", "");
        String empCodeShared = pref.getString("empcode", "");
        System.out.println("mob  "+mobNumShared);
        //getting imeinumbers
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
        boolean isDualSIM = telephonyInfo.isDualSIM();
        if(isDualSIM) {
            String imeiSIM1 = telephonyInfo.getImsiSIM1();
            String imeiSIM2 = telephonyInfo.getImsiSIM2();
            IMEI_numbers=imeiSIM1+","+imeiSIM2;
            System.out.println(IMEI_numbers);
        }
        else{
            TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
            Imei=tm.getDeviceId();
            System.out.println(Imei);
            IMEI_numbers=Imei;
        }
        try{ FileInputStream fis = openFileInput (FILENAME);
        byte[] reader = new byte[fis.available ()];
            if (fis.read(reader)!=-1)
            {
                String myData = new String(reader);
                System.out.println("abctxt:"+myData);
            }
            fis.close();
        }
        catch(Exception ex)
        {
            Log.e("Exception", ex.toString());
        }
        /* Toast custom */
        if(mobNumShared.equals("") && empCodeShared.equals("")) {
            showMobileDialog(this);
        }
        else
        {
            tv.setText("You are registered with "+mobNumShared);
            if(mServerLink.getStatus() == AsyncTask.Status.PENDING){
                // My AsyncTask has not started yet
                System.out.println("Server Link yet to get");
            }

            if(mServerLink.getStatus() == AsyncTask.Status.RUNNING){
                // My AsyncTask is currently doing work in doInBackground()
                System.out.println("Server Link getting");
            }

            if(mServerLink.getStatus() == AsyncTask.Status.FINISHED){
                // START NEW TASK HERE
                System.out.println("Server Link got");
                attemptLogin(mobNumShared,IMEI_numbers);
            }
           // attemptLogin(mobNumShared, IMEI_numbers);
        }
        mProgressView = (ProgressBar) findViewById(R.id.login_progress);
        changeMobNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMobileDialog(MainActivity.this);
            }
        });
    }


    public void showRedAlreadyToast()
    {
        Context context=getApplicationContext();
        LayoutInflater inflater=getLayoutInflater();
        View customToastroot =inflater.inflate(R.layout.mycustomalready_toast, null);
        Toast customtoast=new Toast(context);
        customtoast.setView(customToastroot);
        //customtoast.setText("You Are Already Registered");
        //customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,0, 0);
        customtoast.setDuration(Toast.LENGTH_LONG);
        customtoast.show();

    }

    public void showRedRegbutnotsuccessToast()
    {
        Context context=getApplicationContext();
        LayoutInflater inflater=getLayoutInflater();
        View customToastroot =inflater.inflate(R.layout.mycustomregbut_toast, null);
        Toast customtoast=new Toast(context);
        customtoast.setView(customToastroot);
       // customtoast.setText("Registered Successfully But Permission Is Not Set");
       // customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,0, 0);
        customtoast.setDuration(Toast.LENGTH_LONG);
        customtoast.show();

    }
    public void showRednotRegisteredToast()
    {
        Context context=getApplicationContext();
        LayoutInflater inflater=getLayoutInflater();
        View customToastroot =inflater.inflate(R.layout.mycustomnotreg_toast, null);
        Toast customtoast=new Toast(context);
        customtoast.setView(customToastroot);
       // customtoast.setGravity(Gravity. | Gravity.CENTER_VERTICAL,0, 0);
        customtoast.setDuration(Toast.LENGTH_LONG);
        customtoast.show();

    }

    public void showRedPermissionToast()
    {
        Context context=getApplicationContext();
        LayoutInflater inflater=getLayoutInflater();
        View customToastroot =inflater.inflate(R.layout.mycustomnopermi_toast, null);
        Toast customtoast=new Toast(context);
        customtoast.setView(customToastroot);
        //customtoast.setText("Permission Is Not Set");
        //customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,0, 0);
        customtoast.setDuration(Toast.LENGTH_LONG);
        customtoast.show();

    }
    public void showRedExceptionToast()
    {
        Context context=getApplicationContext();
        LayoutInflater inflater=getLayoutInflater();
        View customToastroot =inflater.inflate(R.layout.mycustomexception_toast, null);
        Toast customtoast=new Toast(context);
        customtoast.setView(customToastroot);
        //customtoast.setText("Error Occurred Please try again later");
        //customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,0, 0);
        customtoast.setDuration(Toast.LENGTH_LONG);
        customtoast.show();

    }
    private void downloadapk(){
        try {
            URL url = new URL("http://14.141.125.83:82/SmartI/MobileApp/app-debug.apk");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "mglapp.apk");

            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();

            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();
            //this.checkUnknownSourceEnability();
            //this.installApk();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showGreenAuthDoneToast()
    {
        Context context=getApplicationContext();
        LayoutInflater inflater=getLayoutInflater();
        View customToastroot =inflater.inflate(R.layout.mycustom_green_toast, null);
        Toast customtoast=new Toast(context);
        customtoast.setView(customToastroot);
        //customtoast.setText("Authentication Done");
        //customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,0, 0);
        customtoast.setDuration(Toast.LENGTH_LONG);
        customtoast.show();

    }
    public void showMobileDialog(Context context){
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.custom_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.mobilenum);


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text

                                number=userInput.getText().toString();
                                tv.setText(userInput.getText());
                                edit.putString("mobile", number);
                                edit.commit();
                                if(mServerLink.getStatus() == AsyncTask.Status.PENDING){
                                    // My AsyncTask has not started yet
                                    System.out.println("Server Link yet to get");
                                }

                                if(mServerLink.getStatus() == AsyncTask.Status.RUNNING){
                                    // My AsyncTask is currently doing work in doInBackground()
                                    System.out.println("Server Link getting");
                                }

                                if(mServerLink.getStatus() == AsyncTask.Status.FINISHED){
                                    // START NEW TASK HERE
                                    System.out.println("Server Link got");
                                    attemptLogin(number,IMEI_numbers);
                                }



                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }
    public void attemptLogin(String mobNumber,String ImeiNumbers) {

        if (mAuthTask != null) {
            return;
        }

        boolean cancel = false;
        // Check for a valid email address.
        if (!checkInternetConenction()) {
            cancel = true;
        }
        if (cancel) {
            Intent myIntent = new Intent(MainActivity.this , Attendance.class );
            startActivity(myIntent);
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressBar = new ProgressDialog(this);
            progressBar.setTitle("Please Wait");
            progressBar.setMessage("Login Processing");
            progressBar.setCancelable(true);
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.show();
            mAuthTask = new UserLoginTask(mobNumber,IMEI_numbers);
            mAuthTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, String> {


        private final String mNumber;
        private final String IMEInumbers;

        UserLoginTask(String mobnumber,String IMEInumbs) {
            mNumber = mobnumber;
            IMEInumbers=IMEInumbs;
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String responseAsText="";
            String httpRequest=connString+"/validLogin";
            System.out.println("Coonstring Login : " + httpRequest);
            String chkFlag;
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(httpRequest);
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("MobileNo", mNumber));
            list.add(new BasicNameValuePair("IMEI", IMEInumbers));

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
                return "Exception"+e.getMessage();
            }

            if (responseAsText.equals("Permission Is Not Set")) {
                chkFlag=responseAsText;

                return chkFlag;
            } else if(responseAsText.equals("You Are Already Registered")) {
                chkFlag=responseAsText;
                return chkFlag;
            }
            else if(responseAsText.equals("Registered Successfully But Permission Is Not Set")) {
                chkFlag=responseAsText;
                return chkFlag;
            }
            else if(responseAsText.equals("You Are Not Registered On MGL")) {
                chkFlag=responseAsText;
                return chkFlag;
            }
            else{
                chkFlag=responseAsText;
                return chkFlag;
            }

        }


        protected void onPostExecute(final String response) {
            mAuthTask = null;
            progressBar.dismiss();
            System.out.println("bgb" + response);

            if (response.equals("Permission Is Not Set")) {
                //Toast.makeText(getApplicationContext(), response,  Toast.LENGTH_SHORT).show();
                showRedPermissionToast();
            } else if(response.equals("You Are Already Registered")) {
                showRedAlreadyToast();
                //Toast.makeText(getApplicationContext(), response,  Toast.LENGTH_SHORT).show();
            }
            else if(response.equals("Registered Successfully But Permission Is Not Set")){
                showRedRegbutnotsuccessToast();
                //Toast.makeText(getApplicationContext(), response,  Toast.LENGTH_SHORT).show();
            }
            else if(response.equals("You Are Not Registered On MGL")){
                showRednotRegisteredToast();
               // Toast.makeText(getApplicationContext(), response,  Toast.LENGTH_SHORT).show();
            }
            else if(response.equals("Exception")) {
                showRedExceptionToast();
                //Toast.makeText(getApplicationContext(), "Error Occurred Please try again later",  Toast.LENGTH_SHORT).show();
            }
            else{
                showGreenAuthDoneToast();
               // Toast.makeText(getApplicationContext(), "Authentication Done",  Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(MainActivity.this , Attendance.class );
                StringTokenizer tokens = new StringTokenizer(response, "^");
                String first = tokens.nextToken();// this will contain "Fruit"
                String second = tokens.nextToken();
                myIntent.putExtra("EmployeeCode", first);
                myIntent.putExtra("EmployeeId", second);
                startActivity(myIntent);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

            progressBar.dismiss();
        }


    }
    private boolean checkInternetConenction(){
        ConnectivityManager check = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (check != null)
        { System.out.println("check"+"||"+check);
            NetworkInfo[] info = check.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i <info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        System.out.println("|||");
                        return true;
                    }
            System.out.println(true);
            return false;
        }
        else{
            System.out.println(false);
            return false;
        }
    }
    public boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (Exception e) {
                Log.e("MGLApp", e.getMessage(), e);
            }
        } else {
            System.out.println("No network available!");
        }
        return false;
    }
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }
    public class getServerLink extends AsyncTask<Void, Void, String> {
        getServerLink() { }
        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String responseAsText="";
            String httpRequest=ConstantconnString+"/getserverlink";
            System.out.println("Coonstring server : " + httpRequest);
            String chkFlag;
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost(httpRequest);
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                String responseStr = EntityUtils.toString(httpResponse.getEntity());
                responseAsText = android.text.Html.fromHtml(responseStr).toString();
                System.out.println("Response ads: " + responseAsText);
            }
            catch (Exception e) {
                return "Exception"+e.getMessage();
            }
            chkFlag=responseAsText;
            return chkFlag;

        }


        protected void onPostExecute(final String response) {
            mServerLink = null;
            System.out.println("Constant" + response);
            if (response.length() > 0) {
                gotServerLink=true;
               ((GlobalVariables) MainActivity.this.getApplication()).setconnString(response);
                connString=response;
                // updateAppversion="2";
                attemptLogin(mobNumShared,IMEI_numbers);
            }
        }





        @Override
        protected void onCancelled() {
            mServerLink = null;
        }


    }
}
