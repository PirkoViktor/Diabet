package com.ales.diabetolog;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import Data.Nastavitev;

class NastavitveRecyclerViewAdapter extends RecyclerView.Adapter<NastavitveRecyclerViewAdapter.ViewHolder>{

    private ArrayList<Nastavitev> nastavitevArrayList;
    private ApplicationDiabetoLog applicationDiabetoLog;
    private Activity activity;
    private int type;
    class ViewHolder extends RecyclerView.ViewHolder
    {
        EditText editTextOd,editTextDo,editTextValue;
        ImageButton btnSave, btnEdit, btnTrash;
        ViewHolder(View v)
        {
            super(v);
            editTextOd =(EditText)v.findViewById(R.id.et_od);
            editTextDo =(EditText)v.findViewById(R.id.et_do);
            editTextValue = (EditText)v.findViewById(R.id.et_value);
            btnSave=(ImageButton)v.findViewById(R.id.btn_save);
            btnEdit=(ImageButton)v.findViewById(R.id.btn_edit);
            btnTrash=(ImageButton)v.findViewById(R.id.btn_trash);
        }
    }

    NastavitveRecyclerViewAdapter(ArrayList<Nastavitev> nastavitevArrayList, Activity ac, int type) {
        this.activity=ac;
        this.nastavitevArrayList = nastavitevArrayList;
        this.applicationDiabetoLog=(ApplicationDiabetoLog)ac.getApplication();
        //0=bazalni, 1=razmerja, 2=obcutljivost
        this.type=type;
    }

    @Override
    public NastavitveRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_settings,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NastavitveRecyclerViewAdapter.ViewHolder holder, int position) {
        Nastavitev nastavitev = nastavitevArrayList.get(position);
        final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("HH:mm");
        if(nastavitev.getFrom()!=null){
            holder.editTextOd.setText(nastavitev.getFrom().toString(dateFormat));
            holder.editTextOd.setEnabled(false);
        } else {
            Nastavitev tempNastavitev=null;
            switch (type){
                //0=bazalni, 1=razmerja, 2=obcutljivost
                case 0:
                    tempNastavitev = applicationDiabetoLog.getUser().getLastBazalniOdmerek();
                    break;
                case 1:
                    tempNastavitev = applicationDiabetoLog.getUser().getLastRazmerjeOH();
                    break;
                case 2:
                    tempNastavitev = applicationDiabetoLog.getUser().getLastObcutljivost();
                    break;
            }
            if(tempNastavitev!=null) {
                holder.editTextOd.setText(tempNastavitev.getTo().toString(dateFormat));
            } else {
                holder.editTextOd.setText("");
            }
            holder.editTextOd.setEnabled(true);
        }
        if(nastavitev.getTo()!=null){
            holder.editTextDo.setText(nastavitev.getTo().toString(dateFormat));
            holder.editTextDo.setEnabled(false);
        }else{
            holder.editTextDo.setText("");
            holder.editTextDo.setEnabled(true);
        }
        if(nastavitev.getValue()!=null){
            holder.editTextValue.setText(nastavitev.getValue());
            holder.editTextValue.setEnabled(false);
        } else {
            holder.editTextValue.setText("");
            holder.editTextValue.setEnabled(true);
        }
        if(nastavitev.getTo()!=null && nastavitev.getValue()!=null){
            holder.btnSave.setVisibility(View.INVISIBLE);
            holder.btnEdit.setVisibility(View.VISIBLE);
        } else {
            holder.btnSave.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.INVISIBLE);
        }
        holder.editTextOd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditTextTime(activity,"Od:",holder.editTextOd,dateFormat);
            }
        });
        holder.editTextDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditTextTime(activity, "Do:", holder.editTextDo, dateFormat);
            }
        });
        holder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from =  holder.editTextOd.getText().toString();
                String to =  holder.editTextDo.getText().toString();
                String value = holder.editTextValue.getText().toString();
                if(!from.equals("") && !to.equals("") && !value.equals("")){
                    holder.btnSave.setVisibility(View.INVISIBLE);
                    holder.btnEdit.setVisibility(View.VISIBLE);
                    Nastavitev novaNastavitev = new Nastavitev(
                            new LocalTime(Integer.valueOf(from.split(":")[0]), Integer.valueOf(from.split(":")[1])),
                            new LocalTime(Integer.valueOf(to.split(":")[0]), Integer.valueOf(to.split(":")[1])),
                            Double.valueOf(value)
                    );
                    switch (type){
                        //0=bazalni, 1=razmerja, 2=obcutljivost
                        case 0:
                            applicationDiabetoLog.getUser().getBazalniOdmerki().set(holder.getAdapterPosition(),novaNastavitev);
                            break;
                        case 1:
                            applicationDiabetoLog.getUser().getRazmerjaOH().set(holder.getAdapterPosition(),novaNastavitev);
                            break;
                        case 2:
                            applicationDiabetoLog.getUser().getObcutljivost().set(holder.getAdapterPosition(),novaNastavitev);
                            break;
                    }
                    nastavitevArrayList.set(holder.getAdapterPosition(),novaNastavitev);
                    holder.editTextOd.setEnabled(false);
                    holder.editTextDo.setEnabled(false);
                    holder.editTextValue.setEnabled(false);
                } else {
                    Toast.makeText(activity.getApplicationContext(),"Prosim izpolnite vsa polja!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnSave.setVisibility(View.VISIBLE);
                holder.btnEdit.setVisibility(View.INVISIBLE);
                holder.editTextOd.setEnabled(true);
                holder.editTextDo.setEnabled(true);
                holder.editTextValue.setEnabled(true);
            }
        });
        holder.btnTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                alertbox.setMessage("Ste pripračani, da želite izbrisati vnos?");
                alertbox.setTitle("Brisanje");
                alertbox.setIcon(android.R.drawable.ic_dialog_alert);
                alertbox.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch (type){
                            //0=bazalni, 1=razmerja, 2=obcutljivost
                            case 0:
                                applicationDiabetoLog.getUser().getBazalniOdmerki().remove(holder.getAdapterPosition());
                                break;
                            case 1:
                                applicationDiabetoLog.getUser().getRazmerjaOH().remove(holder.getAdapterPosition());
                                break;
                            case 2:
                                applicationDiabetoLog.getUser().getObcutljivost().remove(holder.getAdapterPosition());
                                break;
                        }
                        notifyDataSetChanged();
                    }
                });
                alertbox.setNegativeButton("Ne",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                });
                alertbox.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return nastavitevArrayList.size();
    }

    private void setEditTextTime(Activity activity, String title, final EditText editText, final DateTimeFormatter dateFormat){
        int hours=0, minute=0;
            if(!editText.getText().toString().equals("")){
                String string = editText.getText().toString();
                hours= Integer.valueOf(string.split(":")[0]);
                minute= Integer.valueOf(string.split(":")[1]);
            }
            TimePickerDialog timePickerDialog = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Log.i("Info-Nastavitev",hourOfDay+" "+minute);
                    editText.setText(new LocalTime(hourOfDay,minute).toString(dateFormat));
                }
            }, hours, minute, true);
            timePickerDialog.setTitle(title);
            timePickerDialog.show();
    }
}