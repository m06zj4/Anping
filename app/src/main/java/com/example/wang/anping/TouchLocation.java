package com.example.wang.anping;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wang on 7/28/15.
 */
public class TouchLocation {
    private Dot[] dots;
    private float imageHeight, imageWidth;
    private float circleWidth, circleHeight;

    public TouchLocation(float imageHeight, float imageWidth) {
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
    }

    public void setDotWithJson(String json) {
        float widthRatio, heightRatio;
        int node, width, height, radius;
        int[][] temp;

        if (json == null) {
            return;
        }

        try {
            JSONObject jsonData = new JSONObject(json);
            JSONObject information = jsonData.getJSONObject("information");

            node = information.getInt("node");
            temp = new int[node][3]; // node number is [0] , X is [1] , Y is [2]

            JSONObject photo = information.getJSONObject("photo");

            width = photo.getInt("width");
            height = photo.getInt("height");
            radius = photo.getInt("radius");

            JSONArray algorithm = jsonData.getJSONArray("algorithm");

            for (int i = 0; i < algorithm.length(); i++) {
                JSONObject nowNode = algorithm.getJSONObject(i);
                JSONObject touch = nowNode.getJSONObject("touch");

                temp[i][0] = nowNode.getInt("this");
                temp[i][1] = touch.getInt("X");
                temp[i][2] = touch.getInt("Y");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        widthRatio = imageWidth / width;
        heightRatio = imageHeight / height;

        circleWidth = widthRatio * radius;
        circleHeight = heightRatio * radius;

        dots = new Dot[node];

        int n;
        for (int i = 0; i < dots.length; i++) {
            n = temp[i][0];
            dots[n] = new Dot();
            dots[n].X = temp[i][1] * widthRatio;
            dots[n].Y = temp[i][2] * heightRatio;
        }
    }

    public Object getX(int nodeNumber) {
        if (dots == null) {
            return null;
        }

        if (nodeNumber < dots.length) {
            return dots[nodeNumber].X;
        }
        return null;
    }

    public Object getY(int nodeNumber) {
        if (dots == null) {
            return null;
        }

        if (nodeNumber < dots.length) {
            return dots[nodeNumber].Y;
        }
        return null;
    }

    public Dot getDot(int nodeNumber) {
        if (dots == null) {
            return null;
        }

        if (nodeNumber < dots.length) {
            Log.w("mydebug_dot","123");
            return dots[nodeNumber];
        }
        return null;
    }

    public Object analyseTouchLocation(float X, float Y) {
        if (dots == null) {
            return null;
        }

        for (int i = 0; i < dots.length; i++) {
            double a = Math.abs(X - dots[i].X);
            double b = Math.abs(Y - dots[i].Y);
            double c = (circleWidth + circleHeight) / 2;

            if ((a * a) + (b * b) <= (c * c))
                return i;
        }
        return null;
    }

    public class Dot {
        public float X = 0;
        public float Y = 0;
    }
}
