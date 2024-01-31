package com.cloudpos.injectkey.demo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.cloudpos.injectkey.demo.key.PINPadException;
import com.cloudpos.injectkey.demo.util.ByteConvert;
import com.cloudpos.injectkey.demo.util.Logger;
import com.cloudpos.injectkey.demo.util.UiUtils;
import com.wizarpos.security.injectkey.aidl.IKeyLoaderService;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private SSLConnect sslConnect;
    private IKeyLoaderService service = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sslConnect = new SSLConnect(this);
        startInjectKeyService(this);
    }
    protected synchronized boolean startConnectService(Context context, ComponentName comp, ServiceConnection connection) {
        Intent intent = new Intent();
        intent.setPackage(comp.getPackageName());
        intent.setComponent(comp);
//		try{
//			context.startInjectKeyService(intent);
//		}catch (Exception e){
//			e.printStackTrace();
//		}
        boolean isSuccess = context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Logger.debug("(%s)bind service (%s, %s)", isSuccess, comp.getPackageName(), comp.getClassName());
        return isSuccess;
    }
    private boolean startInjectKeyService(Context context){
        ComponentName comp = new ComponentName(
                "com.wizarpos.security.injectkey",
                "com.wizarpos.security.injectkey.service.MainService");
        boolean isSuccess = startConnectService(this,comp, this);
        return isSuccess;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(this.service != null){
            this.service = null;
            this.unbindService(this);
        }
    }

    public void doInjectMasterKey(View view) {
        Logger.debug("Do connect...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                sslConnect.connect();
                try {
                    UiUtils.showToastShort(MainActivity.this, "Send auth info to remote server...");
                    byte[] cb = sslConnect.writeAndRead((byte) 0x01, service.getAuthInfo());
                    int count = ByteConvert.byte2int2(cb);
                    Logger.debug("Master key count: " + count);
                    if (count < 1) {
                        UiUtils.showToastShort(MainActivity.this, "There is no master key be configured.");
                        return;
                    }
                    for (int i = 0; i < count; i++) {
                        byte[] keyInfo = sslConnect.readBlock();
                        UiUtils.showToastShort(MainActivity.this, "Receive key info from remote server. And prepare to import master key info. No: " + (i + 1));
//                        injector.importKeyInfo(keyInfo);
                        int result = service.importKeyInfo(keyInfo);
                        if(result < 0){
                            throw new PINPadException("importkeyInfo failed! error code is " + result);
                        }
                    }

//                    byte[] validData = sslConnect.writeAndRead((byte) 0x02, new byte[0]);
//                    for (int i = 0; i < count; i++) {
//                        if (i > 0) {
//                            validData = sslConnect.readBlock();
//                        }
//                        boolean result = injector.validateMasterKey(validData);
                    UiUtils.showDialogInfo(MainActivity.this, "Inject master key success");
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    UiUtils.showDialogInfo(MainActivity.this, e.getMessage());
                } finally {
                    sslConnect.disconnect();
                }

            }
        }).start();
    }
    public void doInjectTransportKey(View view) {
        Logger.debug("Do connect...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                sslConnect.connect();
                try {
                    UiUtils.showToastShort(MainActivity.this, "Send auth info to remote server...");
                    byte[] cb = sslConnect.writeAndRead((byte) 0x03, service.getAuthInfo());
                    int count = ByteConvert.byte2int2(cb);
                    Logger.debug("Transport key count: " + count);
                    if (count < 1) {
                        UiUtils.showToastShort(MainActivity.this, "There is no transport key be configured.");
                        return;
                    }
                    for (int i = 0; i < count; i++) {
                        byte[] keyInfo = sslConnect.readBlock();
                        UiUtils.showToastShort(MainActivity.this, "Receive key info from remote server. And prepare to import transport key info. No: " + (i + 1));
                        int result = service.importKeyInfo(keyInfo);
                        if(result < 0){
                            throw new PINPadException("importkeyInfo failed! error code is " + result);
                        }
                    }
                    UiUtils.showDialogInfo(MainActivity.this, "Inject transport key success");

                } catch (Exception e) {
                    e.printStackTrace();
                    UiUtils.showDialogInfo(MainActivity.this, e.getMessage());
                } finally {
                    sslConnect.disconnect();
                }

            }
        }).start();
    }
    public void doInjectDukptKey(View view) {
        Logger.debug("Do connect...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                sslConnect.connect();
                try {
                    UiUtils.showToastShort(MainActivity.this, "Send auth info to remote server...");
                    byte[] cb = sslConnect.writeAndRead((byte) 0x05, service.getAuthInfo());
                    int count = ByteConvert.byte2int2(cb);
                    Logger.debug("Dukpt key count: " + count);
                    if (count < 1) {
                        UiUtils.showToastShort(MainActivity.this, "There is no dukpt key be configured.");
                        return;
                    }
                    for (int i = 0; i < count; i++) {
                        byte[] keyInfo = sslConnect.readBlock();
                        UiUtils.showToastShort(MainActivity.this, "Receive key info from remote server. And prepare to import dukpt key info. No: " + (i + 1));
                        int result = service.importKeyInfo(keyInfo);
                        if(result < 0){
                            throw new PINPadException("importkeyInfo failed! error code is " + result);
                        }
                    }
                    UiUtils.showDialogInfo(MainActivity.this, "Inject dukpt key success");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    sslConnect.disconnect();
                }

            }
        }).start();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder obj) {
        service = IKeyLoaderService.Stub.asInterface(obj);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
