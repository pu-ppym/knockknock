package com.example.knockknock.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.knockknock.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView ShowDate;
    private TextView ShowTime;

//
    private ImageButton BusImgBtn;
    private EditText BusEdit;
    private TextView BusTest;
//


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 날짜/시간 표기
        ShowDate = (TextView) findViewById(R.id.textView4);
        ShowTime = (TextView) findViewById(R.id.textView5);

        ShowTimeMethod();
        // 끝


        // 버스
        BusImgBtn = (ImageButton) findViewById(R.id.busButton);
        BusEdit = (EditText)findViewById(R.id.editTextText);
        BusTest = (TextView)findViewById(R.id.busTest);

        // 준비물
        ImageButton ckListBtn = findViewById(R.id.imageButton2);
        List<String> checkedItems = new ArrayList<>();

        ckListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                builder.setTitle("준비물");
                builder.setMessage("무엇을 가지고 나가야 할까요?");

                builder.setMultiChoiceItems(R.array.checklist, null, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        String[] items = getResources().getStringArray(R.array.checklist);

                        if (isChecked){
                            checkedItems.add(items[which]);
                        }else if (checkedItems.contains(items[which])){
                            checkedItems.remove(items[which]);
                        }
                    }
                });

                builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String final_selection = "";

                        for (String item : checkedItems){
                            final_selection = final_selection + "\n" + item;
                        }
                        Toast.makeText(getApplicationContext(),"저장되었습니다", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });


    }

    // 날짜/시간 불러오기
    public void ShowTimeMethod(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                Calendar cal = Calendar.getInstance();

                SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd(E)");
                String strDate = date.format(cal.getTime());
                SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
                String strTime = time.format(cal.getTime());
                ShowDate.setText(strDate);
                ShowTime.setText(strTime);
            }
        };
        Runnable task = new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){}
                    handler.sendEmptyMessage(1);
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }
    // 끝

    //



} //test
//test2
// hi cindy nice to meet you:)
//test123