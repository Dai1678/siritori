package com.example.dai.siritori;

import android.os.AsyncTask;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.StringReader;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AnalysisTask extends AsyncTask<String, Void, String> {

    private AnalysisTaskListener listener;

    @Override
    protected String doInBackground(String... url) {

        Request request = new Request.Builder()
                .url(url[0])
                .get()
                .build();

        OkHttpClient client = new OkHttpClient();

        Response response;
        try {
            response = client.newCall(request).execute();
            String xml = response.body().string();
            Log.d("result", xml);

            return getParseResult(xml);

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("result", "API ERROR");
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (result != null && listener != null){
            Log.d("result", result);
            listener.getAnalysisResult(result);
        }else{
            String errorStr = "Error: API can't used";
            Log.d("result", errorStr);
            listener.getAnalysisResult(errorStr);
        }

    }

    private String getParseResult(String xml){

        String sendText = null;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xml));

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:

                        break;

                    case XmlPullParser.START_TAG:
                        String tagName = xpp.getName();

                        switch (tagName) {
                            case "total_count":
                                eventType = xpp.next();
                                if (!(eventType == XmlPullParser.TEXT && xpp.getText().equals("1"))) {
                                    return "Error: 単語数が一つではありません";
                                }
                                Log.d("analysis", "一つの単語です");
                                break;

                            case "reading":
                                eventType = xpp.next();
                                if (eventType == XmlPullParser.TEXT) {
                                    sendText = xpp.getText();   //ひらがなを返す
                                }
                                break;

                            case "pos":
                                eventType = xpp.next();
                                if (!(eventType == XmlPullParser.TEXT && xpp.getText().equals("名詞"))) {
                                    return "Error: 単語が名詞ではありません";
                                }
                                Log.d("analysis", "名詞です");
                                break;

                        }

                        break;

                    case XmlPullParser.END_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        break;
                }

                eventType = xpp.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sendText;

    }

    void setListener(AnalysisTaskListener listener){
        this.listener = listener;
    }

    interface AnalysisTaskListener{
        void getAnalysisResult(String result);
    }

}
