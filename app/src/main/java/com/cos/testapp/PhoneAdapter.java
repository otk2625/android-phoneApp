package com.cos.testapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.MyViewHolder> {

    private final List<Phone> phones;
    private static final String TAG = "PhoneAdapter";

    public PhoneAdapter(List<Phone> phones) {
        this.phones = phones;
    }

    // 5. addItem, removeItem
    public  void addItem(Phone phone){
        phones.add(phone);
        notifyDataSetChanged();
    }
    public  void removeItem(int position){
        phones.remove(position);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.phone_item, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setItem(phones.get(position));

    }

    @Override
    public int getItemCount() {
        return phones.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private TextView tel;
        private PhoneService phoneService;
        private Phone phone;
        private EditText etName,etTel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            tel = itemView.findViewById(R.id.tel);
            phoneService = PhoneService.retrofit.create(PhoneService.class);

            // ????????? ?????? ????????? ??????.
            itemView.setOnClickListener(v -> {
                View dialog = v.inflate(v.getContext(), R.layout.updatedialog_item, null);
                int pos = getAdapterPosition();
                phone = phones.get(pos);
                AlertDialog.Builder dlg = new AlertDialog.Builder(v.getContext());

                etName = dialog.findViewById(R.id.et_name);
                etTel = dialog.findViewById(R.id.et_tel);
                etName.setText(name.getText());
                etTel.setText(tel.getText());

                dlg.setTitle("??? ??????");
                dlg.setView(dialog);
                dlg.setNegativeButton("??????", null);
                dlg.setPositiveButton("??????",(dialogInterface, i) -> {
                   //????????? save
                    phone.setName(etName.getText()+"");
                    phone.setTel(etTel.getText()+"");
                    Call<CMRespDto<Phone>> call = phoneService.update(phone.getId(),phone);

                    call.enqueue(new Callback<CMRespDto<Phone>>() {
                        @Override
                        public void onResponse(Call<CMRespDto<Phone>> call, Response<CMRespDto<Phone>> response) {
                            Log.d(TAG, "onResponse: ?????? ?????? : " + response.body());
                            Toast.makeText(itemView.getContext(), "????????????", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<CMRespDto<Phone>> call, Throwable t) {
                            Log.d(TAG, "onFailure: ?????? ??????" + t.getMessage());
                        }
                    });
                });
                dlg.setNegativeButton("??????", ((dialogInterface, i) -> {
                    //????????? ??????
                    Call<CMRespDto<String>> call = phoneService.delete(phone.getId());
                    call.enqueue(new Callback<CMRespDto<String>>() {
                        @Override
                        public void onResponse(Call<CMRespDto<String>> call, Response<CMRespDto<String>> response) {
                            Toast.makeText(itemView.getContext(), "????????????", Toast.LENGTH_SHORT).show();
                            removeItem(pos);
                        }

                        @Override
                        public void onFailure(Call<CMRespDto<String>> call, Throwable t) {

                        }
                    });

                }));
                dlg.show();

            });

        }

        public void setItem(Phone phone){
            name.setText(phone.getName());
            tel.setText(phone.getTel());

        }
    }
}
