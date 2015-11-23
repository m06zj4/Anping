package com.example.wang.anping;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by wang on 3/19/15.
 */
public class HttpConnector {
    private final int str = 1;
    private final int img = 2;

    private URL mURL;
    private boolean error;

    public HttpConnector(String URL_address) {
        try {
            mURL = new URL(URL_address);
            error = false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            error = true;
        }
    }

    public Bitmap GetImage(String imageName) {
        if (error) {
            return null;
        }

        if (imageName == null) {
            return null;
        }

        URL imgURL;
        try {
            imgURL = new URL(mURL.toString() + "/" + imageName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return (Bitmap) ConnectionByPost(imgURL, img, null, null);
    }

    public Bitmap GetImage(String queryKey, String queryValue) {
        if (error) {
            return null;
        }

        String Key[] = new String[1];
        String Value[] = new String[1];

        Key[0] = queryKey;
        Value[0] = queryValue;

        return (Bitmap) ConnectionByPost(mURL, img, Key, Value);
    }

    public String SendPost() {
        if (error) {
            return null;
        }

        return (String) ConnectionByPost(mURL, str, null, null);
    }

    public String SendPost(String queryKey, String queryValue) {
        if (error) {
            return null;
        }

        String Key[] = new String[1];
        String Value[] = new String[1];

        Key[0] = queryKey;
        Value[0] = queryValue;

        return (String) ConnectionByPost(mURL, str, Key, Value);
    }

    public String SendPost(String[] queryKey, String[] queryValue) {
        if (error) {
            return null;
        }

        return (String) ConnectionByPost(mURL, str, queryKey, queryValue);
    }

    private Object ConnectionByPost(URL _url, int _resultType, String[] _queryKey, String[] _queryValue) {

        Object result = null;
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) _url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setChunkedStreamingMode(0);

            //參數
            if (_queryKey != null) {
                String send = "";

                for (int i = 0; i < _queryKey.length; i++) {
                    if (i > 0) {
                        send += "&";
                    }
                    send += URLEncoder.encode(_queryKey[i], "UTF-8");
                    send += "=";
                    send += URLEncoder.encode(_queryValue[i], "UTF-8");
                }

                urlConnection.setDoOutput(true);
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(send);
                writer.close();
                out.close();
            }

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                if (_resultType == str) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String line, strung = "";
                    while ((line = br.readLine()) != null) {
                        strung += line;
                    }
                    br.close();
                    result = strung;
                }

                if (_resultType == img) {
                    result = BitmapFactory.decodeStream(in);
                }

                in.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result;
    }
}
