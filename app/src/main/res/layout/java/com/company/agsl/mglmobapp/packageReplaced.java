package layout.java.com.company.agsl.mglmobapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Santa5 on 5/27/2015.
 */
public class packageReplaced extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String msg="intent:"+intent+" action:"+intent.getAction();
        System.out.println("DEBUG"+ msg);
       // Log.d("DEBUG", msg);
       // Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

        /*if (action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_CHANGED) || action.equals(Intent.ACTION_PACKAGE_INSTALL) || action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
            ApplicationInfo info = null;
            try {
                info = context.getPackageManager().getApplicationInfo(intent.getDataString().split(":")[1], PackageManager.GET_META_DATA);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (info != null) {
                    File file = ApplicationMain.INSTALL_APK_INFO.get(info.packageName);
                    if (file != null) {
                        file.delete();
                        ApplicationMain.INSTALL_APK_INFO.remove(info.packageName);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }*/
    }
}
