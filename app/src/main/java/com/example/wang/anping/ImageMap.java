package com.example.wang.anping;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by wang on 8/3/15.
 */
public class ImageMap {
    public final int User = 1;
    public final int Lock = 2;

    private TouchLocation touchLocation;

    private Bitmap pMap, pUser, pLock, pDirection, drawBitmap;
    private Integer mUser, mLock;
    private ImageView imageView;
    int Minor;

    public ImageMap(ImageView imageView, Bitmap user, Bitmap lock, Bitmap direction) {
        this.imageView = imageView;
        this.pUser = user;
        this.pLock = lock;
        this.pDirection = direction;
        mUser = null;
        mLock = null;
    }
    public void Mm(String Mm){
        Minor=Integer.parseInt(Mm);
    }

    public void setMapImage(Bitmap map) {
        int imageHeight, imageWidth;

        this.pMap = map;
        imageHeight = pMap.getHeight();
        imageWidth = pMap.getWidth();

        imageView.setMaxHeight(imageHeight);
        imageView.setMaxWidth(imageWidth);
        imageView.setImageBitmap(pMap);

        mUser = null;
        mLock = null;

        touchLocation = new TouchLocation(imageHeight, imageWidth);
    }

    public void setMapDot(String jsonString) {
        if (touchLocation != null) {
            touchLocation.setDotWithJson(jsonString);
        }
    }

    public void reloadImage() {
        // 判斷是否為主執行緒進入，否的話就跳出
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            Log.w("ImageMap", "Reload Image Must Running Main Thread!");
            return;
        }

        if (drawBitmap != null) {
            imageView.setImageBitmap(drawBitmap);
            //Log.w("ImageMap", "drawBitmap != null");
            drawBitmap = null;
        } else {
            //Log.w("ImageMap", "drawBitmap == null");
        }
    }

    public Integer getNodeNumber(int select) {
        Integer out = null;

        switch (select) {
            case User:
                out = mUser;
                break;
            case Lock:
                out = mLock;
                break;
        }

        return out;
    }

    public boolean drawUserLocation(int node) {
        mUser = node;
        Log.w("mydebug_node",String.valueOf(mUser));

        Bitmap bitmap = pMap.copy(pMap.getConfig(), true);
        Canvas canvas = new Canvas(bitmap);

        TouchLocation.Dot dot = touchLocation.getDot(node);

        if (dot != null) {
            float X = dot.X - (pUser.getWidth() / 2);
            float Y = dot.Y - (pUser.getHeight() / 2);

            canvas.drawBitmap(pUser, X, Y, null);
        }

        if (mLock != null) {
            dot = touchLocation.getDot(mLock);

            if (dot != null) {
                float X = dot.X - (pLock.getWidth() / 2);
                float Y = dot.Y - (pLock.getHeight() / 2);

                canvas.drawBitmap(pLock, X, Y, null);
            }
        }

        if (bitmap != null) {
            drawBitmap = bitmap;
            return true;
        } else {
            return false;
        }
    }


    public boolean drawUserLocation(float X, float Y) {
        Object mNodeObj = touchLocation.analyseTouchLocation(X, Y);
        if (mNodeObj == null) {
            return false;
        }

        return drawUserLocation(Minor);
//        return drawUserLocation((int) mNodeObj);
    }

    public boolean drawLockLocation(int node) {
        mLock = node;

        Log.w("mydebug_lock",String.valueOf(node));
        Bitmap bitmap = pMap.copy(pMap.getConfig(), true);
        Canvas canvas = new Canvas(bitmap);

        TouchLocation.Dot dot = touchLocation.getDot(node);

        if (dot != null) {
            float X = dot.X - (pLock.getWidth() / 2);
            float Y = dot.Y - (pLock.getHeight() / 2);

            canvas.drawBitmap(pLock, X, Y, null);
        }

        if (mUser != null) {
            dot = touchLocation.getDot(mUser);

            if (dot != null) {
                float X = dot.X - (pUser.getWidth() / 2);
                float Y = dot.Y - (pUser.getHeight() / 2);

                canvas.drawBitmap(pUser, X, Y, null);
            }
        }

        if (bitmap != null) {
            drawBitmap = bitmap;
            return true;
        } else {
            return false;
        }
    }

    public boolean drawLockLocation(float X, float Y) {
        Object mNodeObj = touchLocation.analyseTouchLocation(X, Y);
        if (mNodeObj == null) {
            return false;
        }

        return drawLockLocation((int) mNodeObj);
    }

    public boolean drawPath(int[] path) {
        Bitmap bitmap = pMap.copy(pMap.getConfig(), true);
        Canvas canvas = new Canvas(bitmap);

        for (int i = path.length - 1; i >= 0; i--) {
            TouchLocation.Dot dot = touchLocation.getDot(path[i]);

            if (dot == null) {
                break;
            }

            if (i == 0) {
                float X = dot.X - (pLock.getWidth() / 2);
                float Y = dot.Y - (pLock.getHeight() / 2);

                canvas.drawBitmap(pLock, X, Y, null);
            } else if (i >= (path.length - 1)) {
                float X = dot.X - (pUser.getWidth() / 2);
                float Y = dot.Y - (pUser.getHeight() / 2);

                canvas.drawBitmap(pUser, X, Y, null);
            } else {

                TouchLocation.Dot nDot = touchLocation.getDot(path[i - 1]);
                float mX = dot.X - nDot.X;
                float mY = dot.Y - nDot.Y;
                float mAngle = 0;

                if (Math.abs(mX) < 30) {
                    if (mY > 0)
                        mAngle = 90;
                    else
                        mAngle = 270;
                } else if (Math.abs(mY) < 30) {
                    if (mX > 0)
                        mAngle = 0;
                    else
                        mAngle = 180;
                } else if (mX > 0) {
                    if (mY > 0)
                        mAngle = 45;
                    else
                        mAngle = 315;
                } else {
                    if (mY > 0)
                        mAngle = 135;
                    else
                        mAngle = 225;
                }

                Matrix vMatrix = new Matrix();
                vMatrix.setRotate(mAngle);

                Bitmap mBitmap = Bitmap.createBitmap(pDirection, 0, 0, pDirection.getWidth(), pDirection.getHeight(), vMatrix, false);

                float X = dot.X - (mBitmap.getWidth() / 2);
                float Y = dot.Y - (mBitmap.getHeight() / 2);

                canvas.drawBitmap(mBitmap, X, Y, null);
            }
        }

        if (bitmap != null) {
            drawBitmap = bitmap;
            return true;
        } else {
            return false;
        }
    }
}
