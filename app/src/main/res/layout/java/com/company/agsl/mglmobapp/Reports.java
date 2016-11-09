package layout.java.com.company.agsl.mglmobapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Santa5 on 5/8/2015.
 */
    public class Reports extends Activity {
    TextView empCodetxtV,empNametxtV,empDesignationtxtV,empDepartmenttxtV,empLocationtxtV,empDOJtxtV,reportsHeading;
    TableLayout Empdetails;
    private String[] state = { "Select Report Type", "Employee Details", "Mobile Emp Visit Report", "Last 10 Transactions",
            "Mobile Leave Balances", "Last Ten Access Transactions",  };
    private getMyDetails mAuthTask = null;
    private ProgressDialog progressBar;
    String empId;
    String connString="http://14.141.125.83:82/Mobile_Service_AGSL/login.asmx";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportslayout);
        System.out.println(state.length);
        connString= ((GlobalVariables) this.getApplication()).getconnString();
        Empdetails= (TableLayout) findViewById(R.id.empDetails);
        reportsHeading=(TextView)findViewById(R.id.myDetails);
       // reportsHeading.setShadowLayer(10, 0, 0, Color.RED);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            empId = extras.getString("EmployeeCode");
        }

        //emp details textview initialization

        empCodetxtV = (TextView) findViewById(R.id.empCodeTextView);
        empNametxtV = (TextView) findViewById(R.id.empNameTextView);
        empDesignationtxtV = (TextView) findViewById(R.id.empDesignationTextView);
        empDepartmenttxtV = (TextView) findViewById(R.id.empDepartmentTextView);
        empLocationtxtV = (TextView) findViewById(R.id.empLocationTextView);
        empDOJtxtV = (TextView) findViewById(R.id.empDOJTextView);

        if (Utils.isOnline(Reports.this)) {

        progressBar = new ProgressDialog(this);
        progressBar.setTitle("Please Wait");
        progressBar.setMessage("Getting your Details ");
        progressBar.setCancelable(true);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.show();

        mAuthTask = new getMyDetails(empId);
        mAuthTask.execute((Void) null);
        //emp details textview initialization

        } else {
            showToast("No Network Connection!!!");
        }

    }
    public void showRedRegbutnotsuccessToast()
    {
        Context context=getApplicationContext();
        LayoutInflater inflater=getLayoutInflater();
        View customToastroot =inflater.inflate(R.layout.mydetails_toast, null);
        Toast customtoast=new Toast(context);
        customtoast.setView(customToastroot);
        // customtoast.setText("Registered Successfully But Permission Is Not Set");
        // customtoast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL,0, 0);
        customtoast.setDuration(Toast.LENGTH_LONG);
        customtoast.show();

    }
    public class getMyDetails extends AsyncTask<Void, Void, String> {


        private final String employeeID;


        getMyDetails(String empID) {
            employeeID = empID;

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String responseAsText="";
            String chkFlag;
            HttpClient httpClient=new DefaultHttpClient();
            //HttpPost httpPost=new HttpPost("http://23.253.164.20:8096/login.asmx/getEmpDetails");
            HttpPost httpPost=new HttpPost(connString+"/getEmpDetails");
            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("EmployeeId", empId));


            try {
                // Simulate network access.
                // Thread.sleep(2000);
                httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                String responseStr = EntityUtils.toString(httpResponse.getEntity());
                responseAsText = android.text.Html.fromHtml(responseStr).toString();
                System.out.println("Response: " + responseAsText);

            }
            catch (Exception e) {
                return "Exception";
            }

            if (responseAsText.equals("1")) {
                chkFlag = responseAsText;

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
            System.err.print("bgb" + response);

            if (response.equals(1)) {
                Toast.makeText(getApplicationContext(), "Please Try Again after some time", Toast.LENGTH_SHORT).show();
            }

            else{

                //321560011^Owaiz^ Technician - O & M^O & M^Mahape CGS^1/1/2015 12:00:00 AM
                String[] separated = response.split("^");
                System.out.println("splits.size: " + separated.length);
                StringTokenizer tokens = new StringTokenizer(response, "^");
                String first = tokens.nextToken();// this will contain "Fruit"
                String second = tokens.nextToken();
                System.out.println(separated);
                empCodetxtV.setText(empId);
                empNametxtV.setText(second);
                empDesignationtxtV.setText(tokens.nextToken());
                empDepartmenttxtV.setText(tokens.nextToken());
                empLocationtxtV.setText(tokens.nextToken());
                empDOJtxtV.setText(tokens.nextToken());

                Empdetails.setVisibility(View.VISIBLE);
                showRedRegbutnotsuccessToast();
               // Toast.makeText(getApplicationContext(), "Your Details are here",  Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

            progressBar.dismiss();
        }


    }
    public void showToast(String msg) {
        Toast.makeText(Reports.this, msg, Toast.LENGTH_LONG).show();
    }
}
