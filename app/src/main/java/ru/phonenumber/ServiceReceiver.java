package ru.phonenumber;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import java.io.DataOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;

/**
 * Created by Sergey on 3/26/2015.
 */
public class ServiceReceiver extends BroadcastReceiver {

    private static long prevTime=0;

    @Override
    public void onReceive(final Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, final String incomingNumber) {
                switch (state){
                    case TelephonyManager.CALL_STATE_RINGING:
                        //Log.i("ServiceReceiver", "IncomingNumber:" + incomingNumber);
//                        Log.i("Time", Long.toString(Calendar.getInstance().getTimeInMillis()-prevTime));
                        long now_m = Calendar.getInstance().getTimeInMillis();
                        if(now_m-prevTime>2000 || now_m-prevTime==now_m) {
//                            Log.i("ServiceReceiver", "IncomingNumber:" + incomingNumber);
                            SharedPreferences pref = context.getSharedPreferences("PhoneNumber", context.MODE_MULTI_PROCESS);
                            final String url = pref.getString("host", "");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        sentPost(url, incomingNumber);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                        prevTime = Calendar.getInstance().getTimeInMillis();
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        },PhoneStateListener.LISTEN_CALL_STATE);
    }

    void sentPost(String url, String number) throws Exception{
//        Log.i("ServiceReceiver", "sentPost()");
//        Log.i("ServiceReceiver", "url="+url);
        URL obj = new URL(url);
        URLConnection con;

        if(url.contains("https"))
            con = (HttpsURLConnection) obj.openConnection();
        else
            con = (HttpURLConnection) obj.openConnection();

        if (obj.getUserInfo() != null) {
            String basicAuth = "Basic " + new String(Base64.encode(obj.getUserInfo().getBytes(), Base64.DEFAULT));
            con.setRequestProperty("Authorization", basicAuth);
        }

        //add reuqest header
        if(url.contains("https"))
            ((HttpsURLConnection)con).setRequestMethod("POST");
        else
            ((HttpURLConnection)con).setRequestMethod("POST");
        String urlParameter = "n="+number;

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameter);
        wr.flush();
        wr.close();

        int responseCode;
        if(url.contains("https"))
            responseCode = ((HttpsURLConnection)con).getResponseCode();
        else
            responseCode = ((HttpURLConnection)con).getResponseCode();
//        Log.i("ServiceReceiver", "Sending 'POST' request to URL : " + url);
//        Log.i("ServiceReceiver", "Post parameters : " + urlParameter);
//        Log.i("ServiceReceiver", "ResponceCode: "+responseCode);
    }
}
