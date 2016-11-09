package layout.java.com.company.agsl.mglmobapp;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by Santa5 on 5/20/2015.
 */
public class PowerChecker extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){

        Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onDestroy(){
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        PowerBroadcastReceiver receiver = new PowerBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        registerReceiver(receiver, filter);

        return START_STICKY;
    }
}
