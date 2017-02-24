package com.example.androidclient;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import static org.opencv.imgproc.Imgproc.cvtColor;

//import android.graphics.*;

//import org.opencv.android.Utils;

//import org.opencv.imgproc.*;

public class MainActivity extends Activity {

    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;


    private static final String TAG = "MainActivity";

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonClear = (Button) findViewById(R.id.clear);
        textResponse = (TextView) findViewById(R.id.response);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);

        buttonClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                textResponse.setText("");
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    OnClickListener buttonConnectOnClickListener =
            new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    MyClientTask myClientTask = new MyClientTask(
                            editTextAddress.getText().toString(),
                            Integer.parseInt(editTextPort.getText().toString()));

                    //myClientTask.execute();
                    myClientTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }



    public class MyClientTask extends AsyncTask<Void, BitmapDrawable , Void> {
        Bitmap bm;
        byte[]buffertm = new byte[1920000];
        Mat mt= Mat.zeros(400, 400, CvType.CV_8UC4);
        Mat m;
        ImageView iv ;
        String dstAddress;
        int dstPort;
        String response = "";
        GamePlay me;
        BitmapDrawable bmDrawable;
        MyClientTask(String addr, int port) {
            //requestWindowFeature(Window.FEATURE_NO_TITLE);
            me=new GamePlay(getApplicationContext());
            setContentView(me);
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            m= Mat.zeros(400, 400, CvType.CV_8UC4);
            iv = (ImageView) findViewById(R.id.imageView1);
            bmDrawable=new BitmapDrawable( getResources() , bm);
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[640000];

                byte[] buffertmp = new byte[640000];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();

				/*
				 * notice:
				 * inputStream.read() will block if no data return
				 *
				 */
                //BufferedSource mmtsorce = Okio.buffer(Okio.source(socket));

                //inputStream.read(buffer)
                int ptr =0;
                int ptrbr =0;
                int limit = 640000;
                int alpha=0;
                while ((bytesRead = inputStream.read(buffer) ) != -1) {
                    if (ptr < limit)
                    {
                        if (ptr + bytesRead < limit) {
                            for (; ptrbr < bytesRead; ptrbr++)
                                buffertmp[ptr + ptrbr] = buffer[ptrbr];
                            ptr += bytesRead;
                            ptrbr = 0;
                        } else if (ptr + bytesRead >= limit) {
                            for (; ptrbr < limit - ptr; ptrbr++)
                                buffertmp[ptr + ptrbr] = buffer[ptrbr];
                            ptr += (limit - ptr) + 1;

                        }
                    }
                    if (ptr >= limit)
                    {
                            m.put(0, 0, buffertmp);
                            cvtColor(m, m, Imgproc.COLOR_RGB2RGBA);
                            bm = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.RGB_565);
                            Utils.matToBitmap(m, bm);
                            bmDrawable = new BitmapDrawable(getResources(), bm);
                            publishProgress(bmDrawable);

                        ptr = 0;
                        alpha = bytesRead -  ptrbr  ;
                        for (; ptrbr < bytesRead; ptrbr++)
                            buffertmp[ptr + ptrbr] = buffer[ptrbr];
                        ptr += alpha;
                        ptrbr = 0;

                    }

                    /*
                    Log.d(TAG, "bytesRead");
                    Log.d(TAG, Integer.toString(bytesRead));
                    Log.d(TAG, "ptr");
                    Log.d(TAG, Integer.toString(ptr));
                    Log.d(TAG, "ptrbr");
                    Log.d(TAG, Integer.toString(ptrbr));
                     */
                    //
                    //Log.d(TAG, "bm chaned");
                    //publishProgress(bm);
                    //Canvas canvas ;
                     /*
                    byte[] data;
                    data = new byte[3];
                    int ptr = 0;

                    Mat     mRgba;
                    mRgba = new Mat(800, 600, CvType.CV_8UC4);
                    Rect[] faces=new Rect[3];
                    for (Rect r: faces) {
                        Imgproc.rectangle(mRgba, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 3);
                        //Mat m = new Mat();
                        m = mRgba.submat(r);

                        Mat gray = new Mat();
                        Imgproc.cvtColor(m, gray, Imgproc.COLOR_RGB2GRAY,1);

                        Mat gray_f = new Mat();
                        gray.convertTo(gray_f, CvType.CV_32F, 1.0/255, 0);

                        Mat fin = new Mat();
                        gray_f.convertTo(fin, CvType.CV_8U, 255, 0);

                        Imgproc.cvtColor(fin, m, Imgproc.COLOR_GRAY2RGB,0);
                        Imgproc.cvtColor(m, m, Imgproc.COLOR_RGB2RGBA,0);
                        Mat kotak = new Mat();
                        kotak = mRgba.colRange(r.x, r.x+r.width).rowRange(r.y, r.y+r.height);
                        m.copyTo(kotak);
                    }
                    */

                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(BitmapDrawable... values) {

            //iv.setImageDrawable(values[0]);
            me.run();
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            //me.run();
            textResponse.setText(response);
            super.onPostExecute(result);
        }

        public class GamePlay extends SurfaceView implements Runnable {

            private Bitmap myChar;
            private SurfaceHolder holder;
            Thread t=null;
            Canvas canvas;
            public GamePlay(Context context){
                super(context);
                //myChar = Char;
                holder = getHolder();
                    /*
                holder.addCallback(new SurfaceHolder.Callback(){

                    @SuppressLint("WrongCall")
                    @Override
                    public void surfaceCreated(SurfaceHolder surfaceHolder) {
                            Canvas canvas = holder.lockCanvas(null);
                            onDraw(canvas);
                            holder.unlockCanvasAndPost(canvas);
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                    }
                });
                */
                myChar = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
                //myChar = bm ;
            }

            @Override
            protected void onDraw(Canvas canvas){
                canvas.drawColor(Color.BLUE);
                canvas.drawBitmap(bm, 10, 10, null);

            }

            public void resume() {
                t= new Thread(this);
                t.start();
            }
            @Override
            public void run() {
                //while (true) {
                    canvas = holder.lockCanvas();
                    canvas.drawColor(Color.BLACK);
                    canvas.drawBitmap(bm, 150, 100, null);
                    canvas.drawBitmap(bm, 150, 600, null);
                    holder.unlockCanvasAndPost(canvas);

                //}
            }
        }

    }

}
