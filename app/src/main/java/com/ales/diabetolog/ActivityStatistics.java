package com.ales.diabetolog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.text.DecimalFormat;
import java.util.ArrayList;

import Data.Aktivnost;
import Data.Obrok;
import Data.Vnos;

public class ActivityStatistics extends AppCompatActivity implements OnChartValueSelectedListener{
    Integer stevecVnosi=0, stevecObroki=0, stevecAktivnosti=0;
    private ArrayList<Vnos> vnosArrayList;
    private ApplicationDiabetoLog applicationDiabetoLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        applicationDiabetoLog = (ApplicationDiabetoLog) getApplication();
        LocalDate dateTimeOd = LocalDate.parse(getIntent().getStringExtra("datumOd"),
                DateTimeFormat.forPattern("dd.MM.yyyy")
        );
        LocalDate dateTimeDo = LocalDate.parse(getIntent().getStringExtra("datumDo"),
                DateTimeFormat.forPattern("dd.MM.yyyy")
        );
        vnosArrayList = applicationDiabetoLog.getUser().getVnosiFiltered(dateTimeOd, dateTimeDo);
        if(vnosArrayList.size()==0){
            Toast.makeText(getApplicationContext(),"Немає записів для вибраного періоду!",Toast.LENGTH_SHORT).show();
        }else{
            Log.i("Info-Statistika",vnosArrayList.size()+"");
        }
        ((TextView)findViewById(R.id.tv_obdobje)).setText("Статистика за період від "+
                dateTimeOd.toString(DateTimeFormat.forPattern("dd.MM.yyyy"))+
                " do "
                + dateTimeDo.toString(DateTimeFormat.forPattern("dd.MM.yyyy"))
        );
        statistikaVsi();
        statistikaVnosi();
        statistikaObroki();
        statistikaAktivnosti();
        setPieChart();
        setLineChart();
        findViewById(R.id.ll_vnosi).setVisibility(View.INVISIBLE);
        findViewById(R.id.ll_obroki).setVisibility(View.INVISIBLE);
        findViewById(R.id.ll_aktivnosti).setVisibility(View.INVISIBLE);
        findViewById(R.id.ll_vsi).setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        switch (((PieEntry)e).getLabel()){
            case "Записи":
                findViewById(R.id.ll_vnosi).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_obroki).setVisibility(View.INVISIBLE);
                findViewById(R.id.ll_aktivnosti).setVisibility(View.INVISIBLE);
                findViewById(R.id.ll_vsi).setVisibility(View.INVISIBLE);
                break;
            case "Їжа":
                findViewById(R.id.ll_vnosi).setVisibility(View.INVISIBLE);
                findViewById(R.id.ll_obroki).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_aktivnosti).setVisibility(View.INVISIBLE);
                findViewById(R.id.ll_vsi).setVisibility(View.INVISIBLE);
                break;
            case "Діяльність":
                findViewById(R.id.ll_vnosi).setVisibility(View.INVISIBLE);
                findViewById(R.id.ll_obroki).setVisibility(View.INVISIBLE);
                findViewById(R.id.ll_aktivnosti).setVisibility(View.VISIBLE);
                findViewById(R.id.ll_vsi).setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onNothingSelected() {
        findViewById(R.id.ll_vnosi).setVisibility(View.INVISIBLE);
        findViewById(R.id.ll_obroki).setVisibility(View.INVISIBLE);
        findViewById(R.id.ll_aktivnosti).setVisibility(View.INVISIBLE);
        findViewById(R.id.ll_vsi).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        applicationDiabetoLog.save();
    }

    public void statistikaVsi(){
        double min=Double.MAX_VALUE, max=Double.MIN_VALUE, avg=0, stevec=0, vsota=0;
        for(int i=0;i<vnosArrayList.size();i++){
            Double sladkor=vnosArrayList.get(i).getSladkor();
            if(sladkor!=null) {
                vsota += sladkor;
                stevec++;
                if (sladkor < min) {
                    min = sladkor;
                }
                if (sladkor > max) {
                    max = sladkor;
                }
                //If vnos is Aktivnost you need to iterate throught the Sladkorji array and check the values aswell
                //and add them to the full sum
                if (vnosArrayList.get(i).getClass().equals(Aktivnost.class)) {
                    ArrayList<Double> sladkorji = ((Aktivnost) vnosArrayList.get(i)).getSladkorji();
                    for (int j = 0; j < sladkorji.size(); j++) {
                        sladkor = sladkorji.get(j);
                        vsota += sladkor;
                        stevec++;
                        if (sladkor < min) {
                            min = sladkor;
                        }
                        if (sladkor > max) {
                            max = sladkor;
                        }
                    }
                }
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        if(stevec!=0) {
            avg = vsota / stevec;
        }
        if(min==Double.MAX_VALUE){
            ((EditText)findViewById(R.id.et_min_vsi)).setText("");
        } else {
            ((EditText)findViewById(R.id.et_min_vsi)).setText(decimalFormat.format(min));
        }
        if(max==Double.MIN_VALUE){
            ((EditText)findViewById(R.id.et_max_vsi)).setText("");
        } else {
            ((EditText)findViewById(R.id.et_max_vsi)).setText(decimalFormat.format(max));
        }
        if(avg==0){
            ((EditText)findViewById(R.id.et_avg_vsi)).setText("");
        } else {
            ((EditText)findViewById(R.id.et_avg_vsi)).setText(decimalFormat.format(avg));
        }

    }
    public void statistikaVnosi(){
        double min=Double.MAX_VALUE, max=Double.MIN_VALUE, avg=0, stevec=0, vsota=0;
        for(int i=0;i<vnosArrayList.size();i++){
            if(vnosArrayList.get(i).getClass().equals(Vnos.class)){
                double sladkor=vnosArrayList.get(i).getSladkor();
                vsota+=sladkor;
                stevec++;
                if(sladkor<min){
                    min=sladkor;
                }
                if(sladkor>max){
                    max=sladkor;
                }
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        if(stevec!=0) {
            avg = vsota / stevec;
        }
        stevecVnosi=(int)stevec;
        if(min==Double.MAX_VALUE){
            ((EditText)findViewById(R.id.et_min_vnosi)).setText("");
        } else {
            ((EditText)findViewById(R.id.et_min_vnosi)).setText(decimalFormat.format(min));
        }
        if(max==Double.MIN_VALUE){
            ((EditText)findViewById(R.id.et_max_vnosi)).setText("");
        } else {
            ((EditText)findViewById(R.id.et_max_vnosi)).setText(decimalFormat.format(max));
        }
        if(avg==0){
            ((EditText)findViewById(R.id.et_avg_vnosi)).setText("");
        } else {
            ((EditText)findViewById(R.id.et_avg_vnosi)).setText(decimalFormat.format(avg));
        }
    }
    public void statistikaObroki(){
        double min=Double.MAX_VALUE, max=Double.MIN_VALUE, avg=0, stevec=0, vsota=0;
        for(int i=0;i<vnosArrayList.size();i++){
            if(vnosArrayList.get(i).getClass().equals(Obrok.class)){
                Double sladkor=vnosArrayList.get(i).getSladkor();
                if(sladkor!=null) {
                    vsota += sladkor;
                    stevec++;
                    if (sladkor < min) {
                        min = sladkor;
                    }
                    if (sladkor > max) {
                        max = sladkor;
                    }
                }
                stevecObroki++;
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        if(stevec!=0) {
            avg = vsota / stevec;
        }
        if(min==Double.MAX_VALUE){
            ((EditText)findViewById(R.id.et_min_obroki)).setText("");
        } else {
            ((EditText)findViewById(R.id.et_min_obroki)).setText(decimalFormat.format(min));
        }
        if(max==Double.MIN_VALUE){
            ((EditText)findViewById(R.id.et_max_obroki)).setText("");
        } else {
            ((EditText)findViewById(R.id.et_max_obroki)).setText(decimalFormat.format(max));
        }
        if(avg==0){
            ((EditText)findViewById(R.id.et_avg_obroki)).setText("");
        } else {
            ((EditText)findViewById(R.id.et_avg_obroki)).setText(decimalFormat.format(avg));
        }
    }
    public void statistikaAktivnosti(){
        double min=Double.MAX_VALUE, max=Double.MIN_VALUE, avg=0, stevec=0, vsota=0;
        for(int i=0;i<vnosArrayList.size();i++){
            if(vnosArrayList.get(i).getClass().equals(Aktivnost.class)){
                double sladkor=vnosArrayList.get(i).getSladkor();
                vsota+=sladkor;
                stevec++;
                if(sladkor<min){
                    min=sladkor;
                }
                if(sladkor>max){
                    max=sladkor;
                }
                ArrayList<Double> sladkorji=((Aktivnost)vnosArrayList.get(i)).getSladkorji();
                for(int j=0;j<sladkorji.size();j++){
                    sladkor=sladkorji.get(j);
                    vsota+=sladkor;
                    stevec++;
                    if(sladkor<min){
                        min=sladkor;
                    }
                    if(sladkor>max){
                        max=sladkor;
                    }
                }
                stevecAktivnosti++;
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        if(stevec!=0) {
            avg = vsota / stevec;
        }
        if(min==Double.MAX_VALUE){
            ((EditText)findViewById(R.id.et_min_aktivnosti)).setText("");
        } else {
            ((EditText)findViewById(R.id.et_min_aktivnosti)).setText(decimalFormat.format(min));
        }
        if(max==Double.MIN_VALUE){
            ((EditText)findViewById(R.id.et_max_aktivnosti)).setText("");
        } else {
            ((EditText)findViewById(R.id.et_max_aktivnosti)).setText(decimalFormat.format(max));
        }
        if(avg==0){
            ((EditText)findViewById(R.id.et_avg_aktivnosti)).setText("");
        } else {
            ((EditText)findViewById(R.id.et_avg_aktivnosti)).setText(decimalFormat.format(avg));
        }
    }
    public void setPieChart(){
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        if(stevecVnosi!=0) {
            pieEntries.add(new PieEntry(stevecVnosi, "Записи", stevecVnosi));
        }
        if(stevecObroki!=0) {
            pieEntries.add(new PieEntry(stevecObroki, "Їжа", stevecObroki));
        }
        if(stevecAktivnosti!=0) {
            pieEntries.add(new PieEntry(stevecAktivnosti, "Діяльність", stevecAktivnosti));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries,"");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        ((PieChart)findViewById(R.id.pie_chart)).setData(pieData);
        Description description = new Description();
        description.setText("Кількість конкретних типів вводу");
        description.setTextColor(Color.BLACK);
        PieChart pieChart = (PieChart)findViewById(R.id.pie_chart);
        pieChart.setDescription(description);
        pieChart.animateY(1000);
        pieChart.setOnChartValueSelectedListener(this);
    }
    public void setLineChart(){
        LineChart lineChart = (LineChart)findViewById(R.id.line_chart);
        LineChart lineChartAll = (LineChart)findViewById(R.id.line_chart_all);
        ArrayList<Entry> entryListVsi = new ArrayList<>(),
                entryListVnosi = new ArrayList<>(),
                entryListObroki = new ArrayList<>(),
                entryListAktivnosti = new ArrayList<>();
        Entry entry;
        int MAX_STEVILO_GRAF = 30;
        if(vnosArrayList.size()!=0){
            int i=0,j;
            //Get MAX_STEVILO_GRAF or less of Sladkor values from everything: Zadnjih MAX_STEVILO_GRAF sladkorjev
            while(i< MAX_STEVILO_GRAF && i<vnosArrayList.size()){
                Double sladkor=vnosArrayList.get(i).getSladkor();
                if(sladkor!=null) {
                    entry = new Entry((float) i, sladkor.floatValue());
                    entryListVsi.add(entry);
                    if (vnosArrayList.get(i).getClass().equals(Aktivnost.class)) {
                        ArrayList<Double> sladkorji = ((Aktivnost) vnosArrayList.get(i)).getSladkorji();
                        i++;
                        for (j = 0; j < sladkorji.size(); j++) {
                            sladkor = sladkorji.get(j);
                            entry = new Entry((float) i, sladkor.floatValue());
                            entryListVsi.add(entry);
                            i++;
                            if (i == 20) {
                                break;
                            }
                        }
                        i--;
                    }
                }
                i++;
            }
            //Get MAX_STEVILO_GRAF or less of Sladkor values for Vnos only: Zadnjih MAX_STEVILO_GRAF sladkorjev za navadne vnose"
            i=0;
            j=0;
            while(j< MAX_STEVILO_GRAF && i<vnosArrayList.size()){
                if(vnosArrayList.get(i).getClass().equals(Vnos.class)) {
                    Double sladkor = vnosArrayList.get(i).getSladkor();
                    entry = new Entry((float) j, sladkor.floatValue());
                    entryListVnosi.add(entry);
                    j++;
                }
                i++;
            }
            //Get MAX_STEVILO_GRAF or less of Sladkor values for Obrok only
            i=0;
            j=0;
            while(j< MAX_STEVILO_GRAF && i<vnosArrayList.size()){
                if(vnosArrayList.get(i).getClass().equals(Obrok.class)) {
                    Double sladkor = vnosArrayList.get(i).getSladkor();
                    if(sladkor!=null) {
                        entry = new Entry((float) j, sladkor.floatValue());
                        entryListObroki.add(entry);
                        j++;
                    }
                }
                i++;
            }
            //Get MAX_STEVILO_GRAF or less of Sladkor values for Aktivnost only
            i=0;
            j=0;
            while(j< MAX_STEVILO_GRAF && i<vnosArrayList.size()){
                if(vnosArrayList.get(i).getClass().equals(Aktivnost.class)){
                    Double sladkor=vnosArrayList.get(i).getSladkor();
                    entry = new Entry((float)j,sladkor.floatValue());
                    entryListAktivnosti.add(entry);
                    ArrayList<Double> sladkorji=((Aktivnost)vnosArrayList.get(i)).getSladkorji();
                    j++;
                    for(int k=0;k<sladkorji.size();k++){
                        sladkor=sladkorji.get(k);
                        entry = new Entry((float)j,sladkor.floatValue());
                        entryListAktivnosti.add(entry);
                        j++;
                        if(j== MAX_STEVILO_GRAF){
                            break;
                        }
                    }
                }
                i++;
            }
        }

        LineData lineData = new LineData();
        if(entryListVsi.size()!=0) {
            LineDataSet lineDataSet = new LineDataSet(entryListVsi, "Останні цукри");
            lineDataSet.setColor(Color.RED);
            lineDataSet.setCircleColor(Color.RED);
            lineData.addDataSet(lineDataSet);
        }
        Description description = new Description();
        description.setText("Цукор(max "+ MAX_STEVILO_GRAF +")");
        description.setTextColor(Color.BLACK);
        lineChart.setData(lineData);
        lineChart.setDescription(description);
        lineChart.setClickable(false);
        lineChart.getLegend().setWordWrapEnabled(true);
        lineChart.animateX(1500);
        lineChart.animateY(1500);
        lineChart.invalidate();

        lineData = new LineData();
        if(entryListVnosi.size()!=0) {
            LineDataSet lineDataSet2 = new LineDataSet(entryListVnosi, "Останні записи ");
            lineDataSet2.setColor(Color.GREEN);
            lineDataSet2.setCircleColor(Color.GREEN);
            lineData.addDataSet(lineDataSet2);
        }
        if(entryListObroki.size()!=0) {
            LineDataSet lineDataSet3 = new LineDataSet(entryListObroki, "Останні записи їжі");
            lineData.addDataSet(lineDataSet3);
        }
        if(entryListAktivnosti.size()!=0) {
            LineDataSet lineDataSet4 = new LineDataSet(entryListAktivnosti, "Останні записи активностей");
            lineDataSet4.setColor(Color.YELLOW);
            lineDataSet4.setCircleColor(Color.YELLOW);
            lineData.addDataSet(lineDataSet4);
        }
        description = new Description();
        description.setText("Цукор(max "+ MAX_STEVILO_GRAF +")");
        description.setTextColor(Color.BLACK);
        lineChartAll.setData(lineData);
        lineChartAll.setDescription(description);
        lineChartAll.setClickable(false);
        lineChartAll.getLegend().setWordWrapEnabled(true);
        lineChartAll.animateX(1500);
        lineChartAll.animateY(1500);
        lineChartAll.invalidate();

    }
}