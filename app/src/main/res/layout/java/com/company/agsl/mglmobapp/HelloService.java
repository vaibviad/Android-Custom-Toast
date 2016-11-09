package layout.java.com.company.agsl.mglmobapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HelloService extends Service {

    private static final String TAG = "HelloService";

    private boolean isRunning  = false;

    @Override
    public void onCreate() {
        System.out.println(TAG +" Service onCreate");

        isRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println(TAG +" Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {


                //Your logic that service will perform will be placed here
                //In this example we are just looping and waits for 1000 milliseconds in each loop.
                for (int i = 0; i < 5; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }

                    if(isRunning){
                        System.out.println(TAG +" Service running");
                        Intent intent = new Intent();
                        //intent.setAction("com.company.agsl.mglmobapp.InternetChecker");
                        //sendBroadcast(intent);
                    }
                }

                //Stop service once it finishes its task
                //stopSelf();
                //stopSelf();
            }
        }).start();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        System.out.println(TAG +" Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        System.out.println(TAG + " Service onDestroy");
    }
}