package com.cloudpos.injectkey.demo;

import android.content.Context;
import android.content.res.AssetManager;


import com.alibaba.fastjson2.JSON;
import com.cloudpos.utils.CommonUtils;
import com.cloudpos.utils.Logger;

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
private String host = "192.168.200.201";
    //private String host = "app.wizarpos.com";
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

    public Result writeAndRead(byte reqType, byte[] reqData) throws IOException {
        write(reqType, reqData);

        byte[] bs = read(2);
        short length = CommonUtils.byte2ToShort(bs, 0);
        byte[] respData = read(length);
        Logger.debug("Read data: " + CommonUtils.toHex(respData));
        Result result = JSON.parseObject(new String(respData), Result.class);
        Logger.debug("Read data as result: " + result);
        return result;
    }

    private void write(byte reqType, byte[] data) throws IOException {
        byte[] tdata = CommonUtils.append(new byte[] {reqType}, data);
        outStream.write(tdata);
        outStream.flush();
    }

    public byte[] readBlock() throws IOException {
        byte[] bs = read(2);
        short length = CommonUtils.byte2ToShort(bs, 0);
        Logger.debug("Read data length: " + length);

        return read(length);
    }

    public byte[] read(int length) throws IOException {
        byte[] buf = new byte[length];
        int readLen = -1, offset = 0;
        while ((readLen = inStream.read(buf, offset, length - offset)) != -1) {
            if (offset + readLen == length) {
                break;
            }
            offset += readLen;
        }
        return buf;
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
