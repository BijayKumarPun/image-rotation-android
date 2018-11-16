package com.example.homay.kanvasanimate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button buttonTrace, buttonRelease;
    Board board;
    TextView textViewCoordinates;
Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aSwitch = findViewById(R.id.switch_tail);
        aSwitch.setChecked(false);

        textViewCoordinates = findViewById(R.id.textview_coordinate);
        buttonTrace = findViewById(R.id.button_trace);
        buttonRelease = findViewById(R.id.button_release);
        board = findViewById(R.id.board_container_main);
        buttonTrace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                board.reset();

            }
        });

        buttonRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                board.releasePath();
            }
        });


        board.setCustomCanvasTouchListener(new CustomCanvasTouchListener() {
            @Override
            public void onCanvasTouch(Float x, Float y) {
                textViewCoordinates.setText(x + " " + y);
            }


        });
aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        board.setTailStat(isChecked,false);
    }
});

    }
}
