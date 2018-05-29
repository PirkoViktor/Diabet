package com.ales.diabetolog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import Data.Aktivnost;
import Data.Obrok;
import Data.Vnos;

class GridViewAdapter extends BaseAdapter
{
    private Context context;
    private Activity activity;
    private ArrayList<Vnos> arrayList;
    private ArrayList<Integer> deleteArrayList;
    private boolean longClick=false;
    GridViewAdapter(Context context, Activity activity, ArrayList<Vnos> arrayList) {
        this.activity=activity;
        this.arrayList = arrayList;
        this.context = context;
        deleteArrayList=new ArrayList<>();
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, final View view, final ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        SimpleDateFormat dateFormatDatum = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        SimpleDateFormat dateFormatUra = new SimpleDateFormat("HH:mm", Locale.getDefault());
        ForegroundColorSpan foregroundColorSpan;
        SpannableStringBuilder spannableStringBuilder;
        if(view==null){
            gridView=layoutInflater.inflate(R.layout.gridviewlayout,null);
        }
        else {
            gridView=view;
        }
        TextView textViewSladkor=(TextView)gridView.findViewById(R.id.textViewSladkor);
        TextView textViewDatum=(TextView)gridView.findViewById(R.id.textViewDatum);
        TextView textViewUra=(TextView)gridView.findViewById(R.id.textViewUra);
        final LinearLayout linearLayoutGridViewLayout=(LinearLayout)gridView.findViewById(R.id.linearLayoutGridViewLayout);
        textViewDatum.setText(dateFormatDatum.format(arrayList.get(i).getDatum().getTime()));
        textViewUra.setText(dateFormatUra.format(arrayList.get(i).getDatum().getTime()));


        Double sladkor=arrayList.get(i).getSladkor();
        if(sladkor!=null) {
            spannableStringBuilder = new SpannableStringBuilder(String.valueOf(sladkor));
            if (sladkor >= 9.5) {
                foregroundColorSpan = new ForegroundColorSpan(Color.RED);
                spannableStringBuilder.setSpan(foregroundColorSpan, 0, String.valueOf(sladkor).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (sladkor <= 4.5) {
                foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#FFA500"));
                spannableStringBuilder.setSpan(foregroundColorSpan, 0, String.valueOf(sladkor).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#33CC33"));
                spannableStringBuilder.setSpan(foregroundColorSpan, 0, String.valueOf(sladkor).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textViewSladkor.setText(spannableStringBuilder);
        }else{
            spannableStringBuilder = new SpannableStringBuilder("--.-");
            foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#000000"));
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textViewSladkor.setText(spannableStringBuilder);
        }
        setBackground(linearLayoutGridViewLayout,i);
        final CheckBox checkBox = (CheckBox)gridView.findViewById(R.id.cb_delete);
        gridView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);
                deleteArrayList.add(i);
                setBackgroundGrey(linearLayoutGridViewLayout,i);
                longClick=true;
                return true;
            }
        });
        gridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(longClick) {
                    if (checkBox.isChecked()) {
                        checkBox.setVisibility(View.INVISIBLE);
                        checkBox.setChecked(false);
                        deleteArrayList.remove((Integer) i);
                        setBackground(linearLayoutGridViewLayout,i);
                        if(deleteArrayList.size()==0){
                            longClick=false;
                        }
                    }else{
                        checkBox.setVisibility(View.VISIBLE);
                        checkBox.setChecked(true);
                        deleteArrayList.add(i);
                        setBackgroundGrey(linearLayoutGridViewLayout,i);
                    }
                }else {
                    ((ActivityMain)activity).infoVnos(activity,i,viewGroup);
                }
            }
        });
        return gridView;
    }

    int getDeleteCount(){
        if(deleteArrayList.size()!=0){
            return deleteArrayList.size();
        }
        return 0;
    }
    void deleteVnosi(){
        Collections.sort(deleteArrayList);
        Collections.reverse(deleteArrayList);
        ((ActivityMain)activity).deleteVnos(deleteArrayList);
        deleteArrayList.clear();
    }
    void setLongClickFalse(){
        longClick=false;
    }
    private void setBackground(LinearLayout linearLayout,int i){
        if (arrayList.get(i).getClass().equals(Obrok.class)) {
            linearLayout.setBackgroundResource(R.drawable.obrok96);
        } else if (arrayList.get(i).getClass().equals(Aktivnost.class)) {
            linearLayout.setBackgroundResource(R.drawable.aktivnost96);
        } else if (arrayList.get(i).getClass().equals(Vnos.class)) {
            linearLayout.setBackgroundResource(R.drawable.vnos96);
        }
        linearLayout.getBackground().setAlpha(40);
    }
    private void setBackgroundGrey(LinearLayout linearLayout,int i){
        if(arrayList.get(i).getClass().equals(Obrok.class)) {
            linearLayout.setBackgroundResource(R.drawable.obrok96_grey);
        } else if(arrayList.get(i).getClass().equals(Aktivnost.class)) {
            linearLayout.setBackgroundResource(R.drawable.aktivnost96_grey);
        } else if(arrayList.get(i).getClass().equals(Vnos.class)){
            linearLayout.setBackgroundResource(R.drawable.vnos96_grey);
        }
        linearLayout.getBackground().setAlpha(40);
    }
}