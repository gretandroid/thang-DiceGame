package com.example.dicegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static final int MAX_CYCLE = 30;
    public static final int MIN_CYCLE = 10;
    private static final int ROTATE_START = 1;
    private static final int ROTATE_IN_PROGRESS = 2;
    private static final int ROTATE_END = 3;
    private List<ImageView> diceImages = new ArrayList<>();
    private int[] diceResIds;
    Handler rotateHandler;
    private Button rotateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDiceImages();
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
                    refreshDice(((ImageView) msg.obj), msg.arg1);
                }
                if (msg.what == ROTATE_END) {
                    refreshRotateButtonToRotate();
                }
            }
        };
    }

    private void initDiceImages() {
        LinearLayout diceGroupLayout = findViewById(R.id.diceGroupLayout);
        int numberDice = diceGroupLayout.getChildCount();
        for (int i = 0; i < numberDice; i++) {
            diceImages.add((ImageView) diceGroupLayout.getChildAt(i));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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
                int numberDice = diceImages.size();
                ExecutorService executorService = Executors.newFixedThreadPool(numberDice);
                for (ImageView dice : diceImages) {
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            rotate(dice);
                        }
                    });
                }
                executorService.shutdown();
                try {
                    executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // END
                Message endMsg = new Message();
                endMsg.what = ROTATE_END;
                rotateHandler.sendMessage(endMsg);
            }
        };

        childThread.start();

    }

    private void rotate(ImageView dice) {
        Random randomRotateCycle = new Random(System.currentTimeMillis());
        int numberCycle = ThreadLocalRandom.current().nextInt(MAX_CYCLE - MIN_CYCLE + 1) + MIN_CYCLE;
        Random random = new Random(System.currentTimeMillis());
        int i = 1;
        while ( i < numberCycle) {
            int resultResId = random.nextInt(6);
            try {
                Thread.sleep(10 * i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Message msg = new Message();
            msg.what = ROTATE_IN_PROGRESS;
            msg.obj = dice;
            msg.arg1 = resultResId;
            rotateHandler.sendMessage(msg);
            i++;
        }
    }

    private void refreshRotateButtonToRotating() {
        rotateButton.setText(R.string.rotating);
        rotateButton.setEnabled(false);
    }

    private void refreshRotateButtonToRotate() {
        rotateButton.setText(R.string.rotate);
        rotateButton.setEnabled(true);
    }

    private void refreshDice(ImageView dice, int resultResId) {
//        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotate.setDuration(5000);
//        rotate.setInterpolator(new LinearInterpolator());
        dice.setImageResource(diceResIds[resultResId]);
    }
}