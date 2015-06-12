package com.example.research;

import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Twilio {
    //Twilio password = Urop2015

    private static final String ACCOUNT_SID = "AC781d1a15b57b265850465913c830bfa5";
    private static final String AUTH_TOKEN = "6060950865885af435f07f6d8e451e86";

    public static void httpMessage(String alert){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(
                "https://api.twilio.com/2010-04-01/Accounts/AC781d1a15b57b265850465913c830bfa5/" +
                        "SMS/Messages");
        String base64EncodedCredentials = "Basic "
                + Base64.encodeToString(
                (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(),
                Base64.NO_WRAP);

        httppost.setHeader("Authorization",
                base64EncodedCredentials);

        String timeStamp = new SimpleDateFormat("dd.HH.mm.ss").format(new Date());

        try {

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("From",
                    "+12484308708"));
            nameValuePairs.add(new BasicNameValuePair("To",
                    "+16133191760"));
            nameValuePairs.add(new BasicNameValuePair("Body",
                    GraphActivity.PATIENTNAME + "'s Blood Glucose level is "+ alert));

            httppost.setEntity(new UrlEncodedFormEntity(
                    nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            Log.d("Twilio ",
                    EntityUtils.toString(entity));

        }
        catch (Exception e){
            Log.d("Twilio: ", "" + e.getStackTrace());
        }

    }
}