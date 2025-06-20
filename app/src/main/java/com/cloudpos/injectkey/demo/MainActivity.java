package com.cloudpos.injectkey.demo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cloudpos.utils.Logger;
import com.cloudpos.utils.UiUtils;
import com.wizarpos.security.injectkey.aidl.IKeyLoaderService;

import org.bouncycastle.util.encoders.Hex;


public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private SSLConnect sslConnect;
    private IKeyLoaderService keyLoaderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sslConnect = new SSLConnect(this);
        bindKeyLoader();
    }
    private boolean bindKeyLoader(){
        ComponentName comp = new ComponentName(
                "com.wizarpos.security.injectkey",
                "com.wizarpos.security.injectkey.service.MainService");
        boolean isSuccess = startConnectService(this,comp, this);
        return isSuccess;
    }
    protected synchronized boolean startConnectService(Context context, ComponentName comp, ServiceConnection connection) {
        Intent intent = new Intent();
        intent.setPackage(comp.getPackageName());
        intent.setComponent(comp);
        boolean isSuccess = context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Logger.debug("(%s)bind service (%s, %s)", isSuccess, comp.getPackageName(), comp.getClassName());
        return isSuccess;
    }

    public void doInjectMasterKey(View view) {
        Logger.debug("Do connect...");
        new Thread(() -> {
            try {
                sslConnect.connect();
                UiUtils.showToastShort(MainActivity.this, "Send auth info to remote server...");
                byte[] authInfo = keyLoaderService.getAuthInfo();
                Result result = sslConnect.writeAndRead((byte) 0x01, authInfo);
                int status = result.getStatus();
                Logger.debug("doInjectMasterKey(%s)", status);
                if (status != Result.SUCCESS) {
                    UiUtils.showToastLong(MainActivity.this, result.getDesc());
                    return;
                }
                for (RKey key : result.getKeys()) {
                    byte[] keyInfo = key.getKeyData();
                    UiUtils.showToastLong(MainActivity.this, "Receive key info from remote server. And prepare to import master key info.");
//                    injector.importKeyInfo(keyInfo);
                    int keyResult = keyLoaderService.importKeyInfo(keyInfo);
                    Logger.debug("doInjectMasterKey(importKeyInfo = %s)", keyResult);
                }
                UiUtils.showDialogInfo(MainActivity.this, "Inject master key success");
            } catch (Exception | Error e) {
                e.printStackTrace();
                UiUtils.showDialogInfo(MainActivity.this, e.getMessage());
            } finally {
                sslConnect.disconnect();
            }
        }).start();
    }

    private void injectKey(byte[] keyInfo) throws Exception{
        int injectResult = keyLoaderService.importKeyInfo(keyInfo);
        Logger.debug("doInjectMasterKey(importKeyInfo = %s)", injectResult);
        if(injectResult < 0){
            throw new Exception(String.format("importKeyInfo failed! reason: %s", injectResult));
        }
    }

    public void doInjectTransportKey(View view) {
        Logger.debug("Do connect...");
        new Thread(() -> {
            try {
                sslConnect.connect();
                UiUtils.showToastLong(MainActivity.this, "Send auth info to remote server...");
                byte[] authInfo = keyLoaderService.getAuthInfo();
                Result result = sslConnect.writeAndRead((byte) 0x03, authInfo);
                int status = result.getStatus();
                if (status != Result.SUCCESS) {
                    UiUtils.showToastLong(MainActivity.this, result.getDesc());
                    return;
                }
                for (RKey key : result.getKeys()) {
                    byte[] keyInfo = key.getKeyData();
                    UiUtils.showToastLong(MainActivity.this, "Receive key info from remote server. And prepare to import transport key info.");
                    injectKey(keyInfo);
                }
                UiUtils.showDialogInfo(MainActivity.this, "Inject transport key success");

            } catch (Exception | Error e) {
                e.printStackTrace();
                UiUtils.showDialogInfo(MainActivity.this, e.getMessage());
            } finally {
                sslConnect.disconnect();
            }
        }).start();
    }
    public void doInjectDukptKey(View view) {
        Logger.debug("Do connect...");
        new Thread(() -> {
            try {
                sslConnect.connect();
                UiUtils.showToastLong(MainActivity.this, "Send auth info to remote server...");
                byte[] authInfo = keyLoaderService.getAuthInfo();
                Result result = sslConnect.writeAndRead((byte) 0x05, authInfo);
                int status = result.getStatus();
                if (status != Result.SUCCESS) {
                    UiUtils.showToastLong(MainActivity.this, result.getDesc());
                    return;
                }
                for (RKey key : result.getKeys()) {
                    byte[] keyInfo = key.getKeyData();
                    UiUtils.showToastLong(MainActivity.this, "Receive key info from remote server. And prepare to import dukpt key info.");
                    int keyResult = keyLoaderService.importKeyInfo(keyInfo);
                    Logger.debug("doInjectMasterKey(importKeyInfo = %s)", keyResult);
                }

                UiUtils.showDialogInfo(MainActivity.this, "Inject dukpt key success");
            } catch (Exception | Error e) {
                e.printStackTrace();
                UiUtils.showDialogInfo(MainActivity.this, e.getMessage());
            } finally {
                sslConnect.disconnect();
            }
        }).start();
    }
    public void doGetAuthInfo(View view) {
        try {
            byte[] authInfo = keyLoaderService.getAuthInfo();
            if(authInfo != null){
                Logger.debug("onServiceConnected(%s)", Hex.toHexString(authInfo));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        try {
            Logger.debug("onServiceConnected(%s)", service.getInterfaceDescriptor());
            keyLoaderService = IKeyLoaderService.Stub.asInterface(service);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
