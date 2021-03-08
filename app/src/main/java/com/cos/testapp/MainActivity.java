package com.cos.testapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.widget.AppCompatEditText;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity2";
    private Context mContext = MainActivity.this;
    private RecyclerView phoneList;
    private PhoneAdapter phoneAdapter;
    private AppCompatEditText etAddName, etAddTel;
    private FloatingActionButton fabAdd;
    private PhoneService phoneService;
    private Call<CMRespDto<List<Phone>>> call;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        findAll();
        listener();

    }

    public void init(){
        fabAdd = findViewById(R.id.fab_save);
        phoneService = PhoneService.retrofit.create(PhoneService.class);
        call = phoneService.findAll();

    }

    public void listener(){
        fabAdd.setOnClickListener(v -> {
            View dialog = v.inflate(v.getContext(), R.layout.adddialog_item, null);
            AlertDialog.Builder dlg = new AlertDialog.Builder(v.getContext());

            etAddName = dialog.findViewById(R.id.et_addN);
            etAddTel = dialog.findViewById(R.id.et_addT);
            dlg.setTitle("폰 등록");
            dlg.setView(dialog);
            dlg.setNegativeButton("닫기", null);
            dlg.setPositiveButton("등록",(dialogInterface, i) -> {
                //여기서 save
                Phone phone = new Phone(null, etAddName.getText()+"", etAddTel.getText()+"");
                PhoneService phoneService = PhoneService.retrofit.create(PhoneService.class);
                Call<CMRespDto<Phone>> call = phoneService.save(phone);

                call.enqueue(new Callback<CMRespDto<Phone>>() {
                    @Override
                    public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                        Toast.makeText(MainActivity.this, "한건 추가", Toast.LENGTH_SHORT).show();
                        phoneAdapter.addItem(response.body().getData());
                    }

                    @Override
                    public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                        Log.d(TAG, "onFailure: 저장 실패" + t.getMessage());
                    }
                });
            });
            dlg.show();
        });
    }

    public void findAll(){
        call.enqueue(new Callback<CMRespDto<List<Phone>>>() {
            @Override
            public void onResponse(Call<CMRespDto<List<Phone>>> call, Response<CMRespDto<List<Phone>>> response) {
                CMRespDto<List<Phone>> cmRespDto = response.body();
                List<Phone> phones = cmRespDto.getData();
                //어댑터 넘기기
                LinearLayoutManager manger = new LinearLayoutManager(mContext, RecyclerView.VERTICAL,false);
                phoneList = findViewById(R.id.rv_phone);
                phoneList.setLayoutManager(manger);

                phoneAdapter = new PhoneAdapter(phones);

                phoneList.setAdapter(phoneAdapter);


                Log.d(TAG, "onResponse: phones : " + phones.toString());
            }

            @Override
            public void onFailure(Call<CMRespDto<List<Phone>>> call, Throwable t) {
                Log.d(TAG, "onFailure: findAll() 실패");
            }
        });
    }


}