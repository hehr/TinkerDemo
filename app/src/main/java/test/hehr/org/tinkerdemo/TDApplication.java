package test.hehr.org.tinkerdemo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.multidex.MultiDex;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by hehr on 16-11-11.
 */
@DefaultLifeCycle(
        application = "tinkerdemo.application",        //配置application
        flags = ShareConstants.TINKER_ENABLE_ALL,
        loadVerifyFlag = false)

public class TDApplication extends DefaultApplicationLike {

    public TDApplication(Application application,
                         int tinkerFlags,
                         boolean tinkerLoadVerifyFlag,
                         long applicationStartElapsedTime,
                         long applicationStartMillisTime,
                         Intent tinkerResultIntent,
                         Resources[] resources,
                         ClassLoader[] classLoader,
                         AssetManager[] assetManager) {

        super(application,
                tinkerFlags,
                tinkerLoadVerifyFlag,
                applicationStartElapsedTime,
                applicationStartMillisTime,
                tinkerResultIntent,
                resources,
                classLoader,
                assetManager);


    }


    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);

        MultiDex.install(base);
        TinkerInstaller.install(this);

    }
}
