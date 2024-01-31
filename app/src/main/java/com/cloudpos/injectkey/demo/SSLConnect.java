package com.cloudpos.injectkey.demo;

import android.content.Context;
import android.content.res.AssetManager;

import com.cloudpos.injectkey.demo.util.CommonUtils;
import com.cloudpos.injectkey.demo.util.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by lizhou on 18-2-26.
 */

public class SSLConnect {
//    private String host = "192.168.200.232";
    private String host = "app.wizarpos.com";
    private int port = 11060;
    private int connectTimeout = 3 * 1000;
    private int readTimeout = 100000 * 1000;

    private Socket socket = null;
    private InputStream inStream = null;
    private OutputStream outStream = null;

    private Context context;

    public SSLConnect(Context context) {
        this.context = context;
    }

    public void connect() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(getKeyManagers(), getTrustManagers(), new SecureRandom());
            SocketFactory socketFactory = sslContext.getSocketFactory();
            socket = socketFactory.createSocket();
            socket.setSoTimeout(readTimeout);

            socket.connect(new InetSocketAddress(host, port), connectTimeout);
            Logger.debug("Connect to server successfully");

            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();
        } catch (Exception e) {
            Logger.error("Connect to Server error", e);
        }
    }

    public void disconnect() {
        CommonUtils.closeQuietly(inStream);
        CommonUtils.closeQuietly(outStream);
        CommonUtils.closeQuietly(socket);
    }

    public void write(byte[] data) {
        try {
            outStream.write(data);
        } catch (IOException e) {
            Logger.error("Write data error", e);
        }
    }

    public byte[] read(int length) throws IOException {
        byte[] bs = new byte[length];
        int len = inStream.read(bs);
        // invalid connection
        if (len <= 0) {
            throw new IOException("Read data error");
        }
        return bs;
    }

    public byte[] readBlock() throws IOException {
        byte[] bs = read(2);
        short length = CommonUtils.byte2ToShort(bs, 0);
        Logger.debug("Read data length: " + length);

        return read(length);
    }

    public byte[] writeAndRead(byte reqType, byte[] data) throws IOException {
        outStream.write(CommonUtils.append(new byte[] {reqType}, data));

        byte[] bs = read(2);
        short length = CommonUtils.byte2ToShort(bs, 0);
        Logger.debug("Read data length: " + length);

        byte[] result = read(length);
        Logger.debug("Read data: " + CommonUtils.toHex(result));
        return result;
    }

    private void readAsync() {
        Logger.debug("Ready to read data...");
        new Thread() {
            @Override
            public void run() {
                try {
                    for (;;) {
                        byte[] bs = read(2);
                        short length = CommonUtils.byte2ToShort(bs, 0);
                        Logger.debug("Read data length: " + length);

                        byte[] data = read(length);
                        Logger.debug("Read data: " + CommonUtils.toHex(data));
                    }
                } catch (IOException e) {
                    Logger.error("Read data error", e);
                } finally {
                    disconnect();
                }
            }
        }.start();
    }

    private KeyManager[] getKeyManagers() throws Exception {
        char[] password = "wizarpos".toCharArray();

        KeyStore keyStore = KeyStore.getInstance("BKS");
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("ks-client.bks");
        keyStore.load(inputStream, password);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
        kmf.init(keyStore, password);
        return kmf.getKeyManagers();
    }

    private TrustManager[] getTrustManagers() throws Exception {
        char[] password = "wizarpos".toCharArray();

        KeyStore trustStore = KeyStore.getInstance("BKS");
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("ts-client.bks");
        trustStore.load(inputStream, password);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
        tmf.init(trustStore);
        return tmf.getTrustManagers();
    }
}
