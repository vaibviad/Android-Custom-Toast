package layout.java.com.company.agsl.mglmobapp;

/**
 * Created by Santa5 on 5/18/2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransactionReport extends Activity {


    private static final String ARRAY_NAME = "student";
    private static final String EmployeeMasterID = "EmployeeMasterID";
    private static final String Name = "Name";
    private static final String Date = "Date";
    private static final String Time = "Time";
    private static final String Status = "Status";
    private static final String EmployeeCode = "EmployeeCode";
    private static final String ManagerName = "ManagerName";
    private static final String InOutStatus="inout";

    //String connString="http://23.253.164.20:8096/login.asmx";
    String connString="http://14.141.125.83:82/Mobile_Service_AGSL/login.asmx";
    List<Item> arrayOfList;
    ListView listView;
    NewsRowAdapter objAdapter;
    String empId,empCode="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trasactions);
        connString= ((GlobalVariables) this.getApplication()).getconnString();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            empId=    getIntent().getStringExtra("EmployeeId");
            empCode = extras.getString("EmployeeCode");
        }
        System.out.println(empCode);
        listView = (ListView) findViewById(R.id.list);
        //listView.setOnItemClickListener(this);

        arrayOfList = new ArrayList<Item>();

        if (Utils.isOnline(TransactionReport.this)) {
            new MyTask().execute();
        } else {
            showToast("No Network Connection!!!");
        }

    }
    public class MyTask extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(TransactionReport.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            if(Utils.isOnline(TransactionReport.this)) {
                return Utils.getJSONStringHTTPResponse(connString + "/getlastweekattendance", empCode);
            }
            else{
                return "Internet is not Stable";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null != pDialog && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (null == result || result.length() == 0) {
                showToast("No data found from web!!!");
                TransactionReport.this.finish();
            }else if (result.equals("Internet is not Stable")) {
                showToast("Please check your Internet");
                TransactionReport.this.finish();
            } else {

                try {
                    //JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = new JSONArray(result);
                    // JSONArray jsonArray = mainJson.getJSONArray(ARRAY_NAME);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject objJson = jsonArray.getJSONObject(i);

                        Item objItem = new Item();

                        objItem.setEmployeeMasterID(objJson.getInt(EmployeeMasterID));
                        objItem.setName(objJson.getString(Name));
                        objItem.setDate(objJson.getString(Date));
                        objItem.setTime(objJson.getString(Time));
                        objItem.setStatus(objJson.getInt(Status));
                        objItem.setEmployeeCode(objJson.getString(EmployeeCode));
                        objItem.setManagerName(objJson.getString(ManagerName));
                        objItem.setInOut(objJson.getInt(InOutStatus));
                        arrayOfList.add(objItem);
                        //System.out.println(arrayOfList.toString());

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // check data...

				/*
                 * for (int i = 0; i < arrayOfList.size(); i++) { Item item =
				 * arrayOfList.get(i); System.out.println(item.getId());
				 *
				 * System.out.println(item.getId());
				 * System.out.println(item.getName());
				 * System.out.println(item.getCity());
				 * System.out.println(item.getGender());
				 * System.out.println(item.getAge());
				 * System.out.println(item.getBirthdate()); }
				 */

               /* Collections.sort(arrayOfList, new Comparator<Item>() {

                    @Override
                    public int compare(Item lhs, Item rhs) {
                        //return (lhs.getAge() - rhs.getAge());
                    }
                });*/
                setAdapterToListview();

            }

        }
    }
    public void setAdapterToListview() {
        //System.out.println(arrayOfList.isEmpty());
        objAdapter = new NewsRowAdapter(TransactionReport.this, R.layout.row2,
                arrayOfList);
        listView.setAdapter(objAdapter);
    }
    public void showToast(String msg) {
        Toast.makeText(TransactionReport.this, msg, Toast.LENGTH_LONG).show();
    }
}
