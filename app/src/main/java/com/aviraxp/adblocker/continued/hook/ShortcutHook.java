package com.aviraxp.adblocker.continued.hook;

import android.content.Intent;

import com.aviraxp.adblocker.continued.helper.PreferencesHelper;
import com.aviraxp.adblocker.continued.util.ContextUtils;
import com.aviraxp.adblocker.continued.util.LogUtils;
import com.aviraxp.adblocker.continued.util.NotificationUtils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

class ShortcutHook {

    public void hook(final XC_LoadPackage.LoadPackageParam lpparam) {

        XC_MethodHook shortcutHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                String packageName = (String) param.args[1];
                Intent intent = (Intent) param.args[2];
                if (intent != null && intent.getAction() != null && intent.getAction().equals("com.android.launcher.action.INSTALL_SHORTCUT") && PreferencesHelper.isShortcutHookEnabled()) {
                    param.setResult(0);
                    LogUtils.logRecord("Shortcut Block Success:" + packageName);
                    NotificationUtils.setNotify(ContextUtils.getOwnContext());
                }
            }
        };

        if (lpparam.packageName.equals("android")) {
            Class<?> clazz = XposedHelpers.findClass("com.android.server.am.ActivityManagerService", lpparam.classLoader);
            XposedBridge.hookAllMethods(clazz, "broadcastIntentLocked", shortcutHook);
        }
    }
}
