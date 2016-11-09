package layout.java.com.company.agsl.mglmobapp;

import android.app.Application;

/**
 * Created by Santa5 on 5/29/2015.
 */
public class GlobalVariables extends Application {
    private String connString;

    public String getconnString() {
        return connString;
    }

    public void setconnString(String connString) {
        this.connString = connString;
    }
}
