package com.example.research;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import net.sf.javaml.core.Instance;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class QueryService extends IntentService {

    private ArrayList<String> bob;

 String urlString;

    public QueryService() {
        super("MongoLabService");
        this.urlString = "https://api.mongolab.com/api/1/databases/jcostik-nightscout/collections/entries?apiKey=CR4PAAj5PmApVtW6XKHTGp8sMkmug76a&s={%22date%22:-1}";
    }

    public QueryService(String URL) {
        super(URL);
    }

    protected void onHandleIntent(Intent intent) {
        //WakefulBroadcastReceiver.completeWakefulIntent(intent);
        String line;
        int type = intent.getIntExtra("type", 0);
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(
                        urlString).openConnection();
                urlConnection.connect();
                Scanner database = new Scanner(new InputStreamReader(
                        urlConnection.getInputStream()));
                database.useDelimiter("\"_id\"");
                line = database.next();
                //TRAIN / RETRAIN
                if (type == 2) {
                    for (int i = 0; database.hasNext(); i++) {
                        line = database.next();
                        GraphActivity.result.add(line);
                    }
                    train();
                    Log.d("TRAIN", line);
                }
                else {
                    for (int i = 0; i < 1 && database.hasNext(); i++) {
                        line = database.next();
                        GraphActivity.result.add(line);
                    }
                    normalRun();
                    Log.d("NORMAL", line);
                }

            } catch (Exception e) {
                Log.d("issue", Log.getStackTraceString(e));

            }
        }

    private void train() {

        for (int i = 11; i >= 0; --i) {
            GraphActivity.last11.add(GraphActivity.result.get(i));

        }
        GraphActivity.holdInfo = GraphActivity.methodObject.produceDataSets();
        GraphActivity.SVMs = GraphActivity.methodObject.trainSVM(GraphActivity.holdInfo.sets.get(0),
                GraphActivity.holdInfo.sets.get(1));

        final double data [] =  GraphActivity.methodObject.getDataSGV(GraphActivity.last11,
                GraphActivity.result.get(0));
        GraphActivity.graph(data, 0);

        Instance toTest = GraphActivity.methodObject.makeInstance(data, GraphActivity.holdInfo);
    }

    private void normalRun() {


        final double data [] =  GraphActivity.methodObject.getDataSGV(GraphActivity.last11,
                        GraphActivity.result.get(0));
        Instance toClassify = GraphActivity.methodObject.makeInstance(data, GraphActivity.holdInfo);

        //DANNY LOOK HEREE
        //HIGH
        if (GraphActivity.methodObject.classify(GraphActivity.SVMs.get(0), toClassify)) {
            Twilio.httpMessage("HIGH");
        //    Yo.sendMessage("OMGITSANJANAA");
            GraphActivity.graph(data, 1);

        }
        //LOW
        else if (GraphActivity.methodObject.classify(GraphActivity.SVMs.get(1), toClassify)) {
        //    Yo.sendMessage("OMGITSANJANAA");
            Twilio.httpMessage("LOW");
            GraphActivity.graph(data, -1);
        }

        else
            GraphActivity.graph(data, 0);

        //increment the last11
        for (int i = 10; i > 0; --i) {
            GraphActivity.last11.set(i, GraphActivity.last11.get(i - 1));
        }
        GraphActivity.last11.set(0, (String) GraphActivity.result.get(0));
    }

}

