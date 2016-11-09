package layout.java.com.company.agsl.mglmobapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Utils {
	public static String getJSONString(String url) {
		String jsonString = null;
		HttpURLConnection linkConnection = null;
		try {
			URL linkurl = new URL(url);
			linkConnection = (HttpURLConnection) linkurl.openConnection();
			int responseCode = linkConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream linkinStream = linkConnection.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int j = 0;
				while ((j = linkinStream.read()) != -1) {
					baos.write(j);
				}
				byte[] data = baos.toByteArray();
				jsonString = new String(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (linkConnection != null) {
				linkConnection.disconnect();
			}
		}
		return jsonString;
	}


    public static String getJSONStringHTTPResponse(String url,String EmpId) {
        String jsonString = null;
        String httpRequest=url;
        System.out.println("Coonstring : " + httpRequest);

    HttpClient httpClient = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost(httpRequest);
    List<NameValuePair> list = new ArrayList<NameValuePair>();
    list.add(new BasicNameValuePair("EmployeeId", EmpId));


    try {
        // Simulate network access.
        // Thread.sleep(2000);
        httpPost.setEntity(new UrlEncodedFormEntity(list));
        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        jsonString = EntityUtils.toString(httpResponse.getEntity());
        //jsonString = new JSONObject(responseStr);
        jsonString = android.text.Html.fromHtml(jsonString).toString();
        // String s = readResponse(httpResponse);
        System.out.println("Response: " + jsonString);

    } catch (Exception e) {
        return "Exception" + e.getMessage();
    }

        return jsonString;
    }

    public static boolean isOnline(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();

    }
	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager connectivity = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
    public static void installApk(Activity activity) {
        System.out.println("in install apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);
		try {
			File sdcard = Environment.getExternalStorageDirectory();
			String filepath=sdcard+"/mglapp.apk";
			System.out.println("filepath "+filepath);
			Uri uri = Uri.fromFile(new File(filepath));
			intent.setDataAndType(uri, "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(intent);
		} catch(Exception e)
		{
			System.out.println("UpdateAPP"+ "Exception " + e);
		}
    }
}

