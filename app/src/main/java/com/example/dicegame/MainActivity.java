package com.example.dicegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final int MAX_CYCLE = 30;
    public static final int MIN_CYCLE = 10;
    private static final int ROTATE_START = 1;
    private static final int ROTATE_IN_PROGRESS = 2;
    private static final int ROTATE_END = 3;
    private ImageView diceImage;
    private ImageView diceImage2;
    private int[] diceResIds;
    Handler rotateHandler;
    int resultResId;
    int resultResId2;
    private Button rotateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        diceImage = findViewById(R.id.diceImage);
        diceImage2 = findViewById(R.id.diceImage2);
        rotateButton = findViewById(R.id.rotateButton);

        // init models dices
        diceResIds = new int[] {
                R.drawable.de1,
                R.drawable.de2,
                R.drawable.de3,
                R.drawable.de4,
                R.drawable.de5,
                R.drawable.de6
        };

        // init a Handler
        rotateHandler = new Handler() {


            @Override
            public void handleMessage(@NonNull Message msg) {

                if (msg.what == ROTATE_START) {
                    refreshRotateButtonToRotating();
                }
                if (msg.what == ROTATE_IN_PROGRESS) {
                    refreshImages();
                }
                if (msg.what == ROTATE_END) {
                    refreshRotateButtonToRotate();
                }
            }
        };
    }

    public void rotate(View view) throws InterruptedException {
        Thread childThread = new Thread() {
            @Override
            public void run() {
                // BEGIN
                Message beginMsg = new Message();
                beginMsg.what = ROTATE_START;
                rotateHandler.sendMessage(beginMsg);

                // ROTATING
                Random randomRotateCycle = new Random(System.currentTimeMillis());
                Random randomRotateCycle2 = new Random(System.currentTimeMillis() * 2);
                int numberCycle = randomRotateCycle.nextInt(MAX_CYCLE - MIN_CYCLE + 1) + MIN_CYCLE;
                int numberCycle2 = randomRotateCycle2.nextInt(MAX_CYCLE - MIN_CYCLE + 1) + MIN_CYCLE;
                int max = java.lang.Math.max(numberCycle, numberCycle2);
                Random random = new Random(System.currentTimeMillis());
                Random random2 = new Random(System.currentTimeMillis() * 2);
                int i = 1;
                while ( i < max) {
                    resultResId = i < numberCycle ? random.nextInt(6) : resultResId;
                    resultResId2 = i < numberCycle2 ? random2.nextInt(6) : resultResId2;
                    try {
                        Thread.sleep(10 * i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Message msg = new Message();
                    msg.what = ROTATE_IN_PROGRESS;
                    rotateHandler.sendMessage(msg);
                    i++;
                }

                // END
                Message endMsg = new Message();
                endMsg.what = ROTATE_END;
                rotateHandler.sendMessage(endMsg);
            }
        };

        childThread.start();

    }

    private void refreshRotateButtonToRotating() {
        rotateButton.setText(R.string.rotating);
        rotateButton.setEnabled(false);
    }

    private void refreshRotateButtonToRotate() {
        rotateButton.setText(R.string.rotate);
        rotateButton.setEnabled(true);
    }

    private void refreshImages() {
        diceImage.setImageResource(diceResIds[resultResId]);
        diceImage2.setImageResource(diceResIds[resultResId2]);
    }
}