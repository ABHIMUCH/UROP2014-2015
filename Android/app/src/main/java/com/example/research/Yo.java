package com.example.research;

        import android.util.Log;

        import java.io.BufferedReader;
        import java.io.DataOutputStream;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.URL;

public class Yo {
    public static void sendMessage(String name)
    {
        try {
            String url = "http://api.justyo.co/yo/";
            java.net.URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("POST");

            String urlParameters = "api_token=b2efb84f-7bac-4ed7-8006-1f753f14283a&username="+name;

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            Log.d("YO", "responseCode " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            Log.d("YO", response.toString());

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("YO", Log.getStackTraceString(e));
        }
    }
}