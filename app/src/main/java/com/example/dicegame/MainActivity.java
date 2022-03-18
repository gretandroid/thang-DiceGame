package com.example.dicegame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final int MAX_CYCLE = 200;
    public static final int MIN_CYCLE = 100;
    private ImageView diceImage;
    private ImageView diceImage2;
    private int[] diceResIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        diceImage = findViewById(R.id.diceImage);
        diceImage2 = findViewById(R.id.diceImage2);

        // init models dices
        diceResIds = new int[] {
                R.drawable.de1,
                R.drawable.de2,
                R.drawable.de3,
                R.drawable.de4,
                R.drawable.de5,
                R.drawable.de6
        };
    }

    public void rotate(View view) throws InterruptedException {
        Random randomRotateCycle = new Random(System.currentTimeMillis());
        int numberCycle = randomRotateCycle.nextInt(MAX_CYCLE - MIN_CYCLE + 1) + MIN_CYCLE;
        Random random = new Random(System.currentTimeMillis());
        Random random2 = new Random(System.currentTimeMillis());
        int i = 1;
        while ( i < 3) {
            int resultResId = random.nextInt(6);
            int resultResId2 = random2.nextInt(6);
            Thread.sleep(100 * i);
            diceImage.setImageResource(diceResIds[resultResId]);
            diceImage2.setImageResource(diceResIds[resultResId2]);
            i++;
        }
    }
}