package steinbacher.georg.storj_hoststats_app;

import android.content.Context;

/**
 * Created by georg on 13.09.17.
 */

public class Application extends android.app.Application{
    private static Context context;

    public void onCreate() {
        super.onCreate();
        Application.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Application.context;
    }
}
