package ysn.com.kview.app;

import android.app.Application;

import com.lazy.library.logging.Builder;
import com.lazy.library.logging.Logcat;

import ysn.com.kview.BuildConfig;
import ysn.com.kview.util.ResUtil;

public class MyApplication extends Application {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initialize();
    }

    private void initialize() {
        ResUtil.inject(mInstance);
        initLogCat();
    }

    private void initLogCat() {
        Builder builder = Logcat.newBuilder();
        builder.topLevelTag("test");
        if (BuildConfig.DEBUG) {
            builder.logCatLogLevel(Logcat.SHOW_ALL_LOG);
        } else {
            builder.logCatLogLevel(Logcat.SHOW_INFO_LOG | Logcat.SHOW_WARN_LOG | Logcat.SHOW_ERROR_LOG);
        }
        Logcat.initialize(this, builder.build());
    }

    public static MyApplication getInstance() {
        return mInstance;
    }
}
