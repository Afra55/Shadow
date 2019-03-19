package com.tencent.shadow.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.tencent.shadow.core.pluginmanager.installplugin.InstalledPlugin;
import com.tencent.shadow.demo.pluginmanager.R;
import com.tencent.shadow.dynamic.host.EnterCallback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DemoPluginManager extends FastPluginManager {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Context mCurrentContext;

    public DemoPluginManager(Context context) {
        super(context);
        mCurrentContext = context;
    }

    /**
     * @return PluginManager实现的别名，用于区分不同PluginManager实现的数据存储路径
     */
    @Override
    protected String getName() {
        return "dynamic_demo";
    }

    /**
     * @return demo插件so的abi
     */
    @Override
    public String getAbi() {
        return "";
    }

    /**
     * @return 宿主中注册的PluginProcessService实现的类名
     */
    @Override
    protected String getPluginProcessServiceName() {
        return "com.tencent.shadow.demo.host.DemoPluginProcessService";
    }

    @Override
    public void enter(final Context context, long formId, Bundle bundle, final EnterCallback callback) {
        final String pluginZipPath = bundle.getString("pluginZipPath");

        final View view = LayoutInflater.from(mCurrentContext).inflate(R.layout.activity_load_plugin,null);

        if(callback != null){
            callback.onShowLoadingView(view);
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InstalledPlugin installedPlugin = installPlugin(pluginZipPath, null, true);
                    String partKey = "demo_main";
                    Intent pluginIntent = new Intent();
                    pluginIntent.setClassName(context.getPackageName(), "com.tencent.shadow.demo.gallery.splash.SplashActivity");

                    startPluginActivity(context, installedPlugin, partKey, pluginIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(callback != null){
                    callback.onCloseLoadingView();
                }
            }
        });

    }
}