package layout.java.com.company.agsl.mglmobapp;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Santa5 on 5/20/2015.
 */
public class InternetChecker extends BroadcastReceiver {
    String FILENAME ="Mydata.txt";
    Attendance att;
    Context ctx;
    private OfflineAttendance mAuthTask = null;
    String connString="http://122.15.117.203/Mobile_Service_AGSL/login.asmx";
    @Override
    public void onReceive(Context context, Intent intent) {
        att=new Attendance();
        ctx=context;
        ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (cm == null)
            return;
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
            // Send here
            System.out.println("Internet ayya");


            try{ FileInputStream fis = context.getApplicationContext().openFileInput(FILENAME);
                byte[] reader = new byte[fis.available ()];
                if (fis.read(reader)!=-1)
                {
                    String myData1 = new String(reader);
                    System.out.println("abctxtADASD:"+myData1);
                    mAuthTask = new OfflineAttendance(myData1);
                    mAuthTask.execute((Void) null);

                }
                fis.close();
            }
            catch(Exception ex)
            {
                Log.e("Exception", ex.getMessage());
            }

        } else {
            // Do nothing or notify user somehow
            System.out.println("Internet nhi  ayya");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.mglogoheader)
                    .setContentText("Internet connection is not present")
                    .setContentTitle("Attention");
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //manager.notify(0, builder.build());
        }

    }
    public class OfflineAttendance extends AsyncTask<Void, Void, Boolean> {
        private final String empCode;
        OfflineAttendance(String Data) {
            empCode=Data;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String responseAsText="";
            Boolean chkFlag;
            HttpPost httpPost;
            List<NameValuePair> list;
            HttpClient httpClient=new DefaultHttpClient();

            System.out.println("Text :: " + empCode );
            System.out.println("||| "+connString+"/insert_Offline");

            //httpPost=new HttpPost("http://23.253.164.20:8096/login.asmx/insert_MobileTransaction");
            httpPost=new HttpPost(connString+"/insert_Offline");
            list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("Text", empCode));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                String responseStr = EntityUtils.toString(httpResponse.getEntity());
                responseAsText = android.text.Html.fromHtml(responseStr).toString();

                System.out.println("Response: " + responseAsText);
                if (responseAsText.equals("Inserted Successfully")) {
                    chkFlag=true;
                    return chkFlag;
                } else {
                    chkFlag=false;
                    return chkFlag;
                }
            }

            catch (Exception e) {
                System.out.println("Exception :: "+e.getMessage());
                return false;
            }


        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // progressBar.dismiss();
            mAuthTask = null;
            // showProgress(false);

            System.out.println("bgb" + success);
            if (success) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.mglogoheader)
                        .setContentText("Attendance sent Successfully")
                        .setContentTitle("Congrats");
                NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());
                FileOutputStream fos = null;
                try {
                    fos =  ctx.getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    fos.write(("").getBytes());
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try{ FileInputStream fis = ctx.getApplicationContext().openFileInput(FILENAME);
                    byte[] reader = new byte[fis.available ()];
                    if (fis.read(reader)!=-1)
                    {
                        String myData1 = new String(reader);
                        System.out.println("abctxtADASD:"+myData1);
                        mAuthTask = new OfflineAttendance(myData1);
                        mAuthTask.execute((Void) null);

                    }
                    fis.close();
                }
                catch(Exception ex)
                {
                    Log.e("Exception", ex.getMessage());
                }
            } else {
                /*NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.mglogoheader)
                        .setContentText("Attendance not sent Successfully")
                        .setContentTitle("Warning");
                NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(0, builder.build());*/
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
            // progressBar.dismiss();
        }


    }

}