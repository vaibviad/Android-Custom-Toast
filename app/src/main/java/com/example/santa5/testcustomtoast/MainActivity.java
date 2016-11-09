package com.example.santa5.testcustomtoast;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText getTextED;
    Button FirstBut,SecondBut,ThirdBut,FourBut,defaultBut;
    String getText="Vathecoder Android Tutorial";
    Switch duration;
    CheckBox checkImg;
    int dur_int=0;
    Boolean is_image=false;
    int type_gravity=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getTextED= (EditText) findViewById(R.id.editText);
        FirstBut= (Button) findViewById(R.id.firstbut);
        SecondBut= (Button) findViewById(R.id.secondbut);
        ThirdBut= (Button) findViewById(R.id.thirdbut);
        FourBut= (Button) findViewById(R.id.fourthbut);
        defaultBut= (Button) findViewById(R.id.defaultBut);
        checkImg= (CheckBox) findViewById(R.id.checkBox);
        checkImg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    is_image=true;
                }else{
                    is_image=false;
                }
            }
        });
        duration= (Switch) findViewById(R.id.duration);
        duration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dur_int=1;
                } else {
                    dur_int=0;
                }
            }
        });
        defaultBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getText=getTextED.getText().toString();
                defaultToast(getText);
            }
        });
        FirstBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getText=getTextED.getText().toString();
                showcustomAndroidToast(getText,1);
            }
        });
        SecondBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getText=getTextED.getText().toString();
                showcustomAndroidToast(getText, 2);
            }
        });
        ThirdBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getText=getTextED.getText().toString();
                showcustomAndroidToast(getText, 3);
            }
        });
        FourBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getText=getTextED.getText().toString();
                showcustomAndroidToast(getText, 4);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public void showcustomAndroidToast(String tmytext,int type)
    {
        LayoutInflater inflater=getLayoutInflater();
        View customToastroot =inflater.inflate(R.layout.mycustomregbut_toast, null);
        Toast customtoast = Toast.makeText(this, tmytext, Toast.LENGTH_SHORT);
        //customtoast.setText(tmytext);
        TextView tv= (TextView)customToastroot.findViewById(R.id.textViewToast);
        ImageView img= (ImageView)customToastroot.findViewById(R.id.imageView);
        if(is_image){
            img.setVisibility(View.VISIBLE);
        }else{
            img.setVisibility(View.GONE);
        }
        if(tmytext.equals("")){
            tv.setText(getText);
        }else{
            tv.setText(tmytext);
        }
        customtoast.setView(customToastroot);
        if(type==1){
            customtoast.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL, 0, 0);
        }else if(type==2){
            customtoast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        }else if(type==3){
            customtoast.setGravity(Gravity.CENTER | Gravity.TOP, 0, 0);
        }else if(type==4){
            customtoast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
        }

        if(dur_int==0){
            customtoast.setDuration(Toast.LENGTH_LONG);
        }else{
            customtoast.setDuration(Toast.LENGTH_SHORT);
        }

        customtoast.show();

    }
    public void showRednotRegisteredToast(String tmytext)
    {
        LayoutInflater inflater=getLayoutInflater();
        View customToastroot =inflater.inflate(R.layout.mycustomregbut_toast, null);
        Toast customtoast = Toast.makeText(this, tmytext, Toast.LENGTH_SHORT);
        TextView tv= (TextView)customToastroot.findViewById(R.id.textViewToast);
        tv.setText(tmytext);
        if(tmytext.equals("")){
            tv.setText(getText);
        }else{
            tv.setText(tmytext);
        }
        ImageView img= (ImageView)customToastroot.findViewById(R.id.imageView);
        if(is_image){
            img.setVisibility(View.VISIBLE);
        }else{
            img.setVisibility(View.GONE);
        }
        customtoast.setView(customToastroot);
        customtoast.setGravity(Gravity.CENTER | Gravity.TOP, 0, 0);
        if(dur_int==0){
            customtoast.setDuration(Toast.LENGTH_LONG);
        }else{
            customtoast.setDuration(Toast.LENGTH_SHORT);
        }
        customtoast.show();

    }

    public void showRedPermissionToast(String tmytext)
    {
        LayoutInflater inflater=getLayoutInflater();
        View customToastroot =inflater.inflate(R.layout.mycustomregbut_toast, null);
        Toast customtoast = Toast.makeText(this, tmytext, Toast.LENGTH_SHORT);
        TextView tv= (TextView)customToastroot.findViewById(R.id.textViewToast);
        tv.setText(tmytext);
        if(tmytext.equals("")){
            tv.setText(getText);
        }else{
            tv.setText(tmytext);
        }
        ImageView img= (ImageView)customToastroot.findViewById(R.id.imageView);
        if(is_image){
            img.setVisibility(View.VISIBLE);
        }else{
            img.setVisibility(View.GONE);
        }
        customtoast.setView(customToastroot);

        if(dur_int==0){
            customtoast.setDuration(Toast.LENGTH_LONG);
        }else{
            customtoast.setDuration(Toast.LENGTH_SHORT);
        }
        customtoast.show();

    }
    public void defaultToast(String tmytext) {
        if(tmytext.equals("")){
            tmytext=getText;
        }
        Toast customtoast = Toast.makeText(this, tmytext, Toast.LENGTH_SHORT);
        customtoast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);

        customtoast.show();
    }

    public void showRedExceptionToast(String tmytext,String gravity)
    {
        Context context=getApplicationContext();
        LayoutInflater inflater=getLayoutInflater();
        View customToastroot =inflater.inflate(R.layout.mycustomregbut_toast, null);
        Toast customtoast = Toast.makeText(this, tmytext, Toast.LENGTH_SHORT);
        TextView tv= (TextView)customToastroot.findViewById(R.id.textViewToast);
        if(tmytext.equals("")){
            tv.setText(getText);
        }else{
            tv.setText(tmytext);
        }
        ImageView img= (ImageView)customToastroot.findViewById(R.id.imageView);
        if(is_image){
            img.setVisibility(View.VISIBLE);
        }else{
            img.setVisibility(View.GONE);
        }
        customtoast.setView(customToastroot);
        customtoast.setGravity(16,1,0);
        if(dur_int==0){
            customtoast.setDuration(Toast.LENGTH_LONG);
        }else{
            customtoast.setDuration(Toast.LENGTH_SHORT);
        }
        customtoast.show();

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
