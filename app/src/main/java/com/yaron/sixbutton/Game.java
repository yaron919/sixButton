package com.yaron.sixbutton;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.os.CountDownTimer;
import android.content.Intent;
import java.util.ArrayList;
import android.content.res.TypedArray;
import java.util.Random;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Game extends AppCompatActivity {
    String name;
    int size;
    int timer;
    TextView timerTV;
    TextView nameTV;
    AlertDialog.Builder alert;
    CountDownTimer countDownTimer;
    ImageView [] imageViews;
    int buttonId1 = -1;
    int winCounter = 0;
    private ArrayList imagesIds1 = new ArrayList();
    private ArrayList imagesIds2= new ArrayList();
    ImageView v1,v2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Bundle extras = getIntent().getExtras();
        name = extras.getString("NAME");
        size = extras.getInt("SIZE");
        timer =extras.getInt("TIMER");
        imageViews = new ImageView[size*size];
        nameTV = findViewById(R.id.name_tv_ga);
        timerTV = findViewById(R.id.timer_tv_ga);
        nameTV.setText(name);
        timer = timer * 1000;
        alert = new AlertDialog.Builder(this);
        countDownTimer = new CountDownTimer(timer, 1000) {

            public void onTick(long millisUntilFinished) {
                timerTV.setText("Timer: "+millisUntilFinished / 1000);
            }

            public void onFinish() {
                timerTV.setText("Timer: 0");
                alert.setMessage("You are lost !!")
                        .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .create();
                alert.show();
            }
        }.start();
        createButtons();
        lockImageViewsSizes();
        //setStartImage();





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
    private void createButtons(){

        TableLayout table = (TableLayout) findViewById(R.id.table_act_game);
        for (int i = 0; i < size;i++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            ));
            table.addView(tableRow);
            for (int j=0; j<size;j++){
                final ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        1.0f
                ));
                imageView.setAdjustViewBounds(true);
                int id = setPictureForButton((size*size)/2);
                imageView.setId(id);
                imageView.setImageResource(R.drawable.question_mark);
                imageView.setPadding(6,6,6,6);
                //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setIvActivity(imageView, imageView.getId());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isSame(imageView);
                            }
                        },500);
                    }
                });
                imageViews[i*size+j]=imageView;
                tableRow.addView(imageViews[i*size+j]);
            }
        }
    }
    private int setPictureForButton(int size){
        int id = getRandomImage(size);
        // add 2 images of the same picture
        while( imagesIds1.contains(id) && imagesIds2.contains(id)){
            id = getRandomImage(size);
        }
        if(imagesIds1.contains(id)){
            imagesIds2.add(id);
        }else
            imagesIds1.add(id);
        return id;
    }
    private int getRandomImage(int size) {
        TypedArray imgs = getResources().obtainTypedArray(R.array.random_imgs);
        int id = imgs.getResourceId(new Random().nextInt(size), -1); //-1 is default if nothing is found (we don't care)
        imgs.recycle();
        return id;
    }
    private void setIvActivity(ImageView iv,int pictureId){
        //set background with scaling
        int newWidth = iv.getWidth();
        int newHeight = iv.getHeight();
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(),pictureId);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap,newWidth,newHeight,true);
        iv.setImageBitmap(scaledBitmap);
        iv.setClickable(false);
    }
    private void lockImageViewsSizes(){
        for(int i = 0; i<size;i++){
            for(int j =0;j<size;j++){
                ImageView iv = imageViews[i*size+j];
                int width = iv.getWidth();
                int height = iv.getHeight();
                iv.setMinimumWidth(width);
                iv.setMaxWidth(width);
                iv.setMinimumHeight(height);
                iv.setMaxHeight(height);
            }
        }
    }
    private void isSame(ImageView ivPressed){
        if(buttonId1 == -1)
        {
            v1=ivPressed;
            buttonId1 = ivPressed.getId();
        }
        else if (buttonId1 == ivPressed.getId()){ //match!
            //setButtonActivity(size,ivPressed,R.drawable.match);
            v2 = ivPressed;
            winChecker();
            buttonId1 = -1;
        }else{ // no match
            v2 = ivPressed;
            int newWidth =  ivPressed.getWidth();
            int newHeight = ivPressed.getHeight();
            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.question_mark);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap,newWidth,newHeight,true);
            v1.setImageBitmap(scaledBitmap);
            v1.setClickable(true);
            v2.setImageBitmap(scaledBitmap);
            v2.setClickable(true);
            buttonId1 = -1;
        }

    }
    private void winChecker() {
        winCounter++;
        if (winCounter == (size * size) / 2) {
            countDownTimer.cancel();
            alert.setMessage("You Win !!")
                    .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .create();
            alert.show();
        }
    }
    private void setStartImage(){
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                int newWidth =  imageViews[i*size+j].getWidth();
                int newHeight =  imageViews[i*size+j].getHeight();
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.question_mark);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap,newWidth,newHeight,true);
                imageViews[i*size+j].setImageBitmap(scaledBitmap);
            }
        }
    }

}
