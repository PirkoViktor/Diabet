package com.ales.diabetolog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.widget.LinearLayout;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import Data.DecimalDigitsFilter;
import org.joda.time.LocalDateTime;

import android.widget.AutoCompleteTextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import Data.Aktivnost;
import Data.Obrok;
import Data.Vnos;


public class ActivityMain extends AppCompatActivity{
   public  List<products> mList;
    public AddAdapter adapter;
    private Activity activity;
    private ApplicationDiabetoLog applicationDiabetoLog;
    private ViewGroup parentPanel;
    private GridView gridView;
    private GridViewAdapter gridViewAdapter;
    private boolean isMenuOpen = false, firstTimeOpening=false;
    public static List<products> retrievePeople() {
        List<products> list = new ArrayList<products>();
        list.add(new products("Яблуко",  56));
        list.add(new products("Груша солодка",  44));
        list.add(new products("Груша звичайна",  67));
        list.add(new products("Помідор",  16));
        list.add(new products("Гречка",  78));
        list.add(new products("Яловичина",  28));
        list.add(new products("Сир",  30));
        list.add(new products("Молоко",  67));
        list.add(new products("Вареники з сиром",  78));
        list.add(new products("Сухарі",  88));
        list.add(new products("Рис",  77));
        return list;
    }
    private List<View> allEds;
    //счетчик чисто декоративный для визуального отображения edittext'ov
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main_window);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity=this;
        applicationDiabetoLog =(ApplicationDiabetoLog) getApplication();

        parentPanel =(ViewGroup)findViewById(R.id.parentPanel);

        gridView = (GridView)findViewById(R.id.gridViewGlavnoOkno);


        if(applicationDiabetoLog.wasLoadSuccesfull()){
            Log.i("Info-MainWindow","Loading was successfull");
            gridViewAdapter = new GridViewAdapter(
                    getApplicationContext(),
                    this,
                    applicationDiabetoLog.getUser().getVnosi()
            );
            gridView.setAdapter(gridViewAdapter);
        } else {
            Log.e("ERROR-MainWindow","Loading wasn't successfull");
            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            View alertView = layoutInflater.inflate(R.layout.layout_load_from_other_source,parentPanel,false);
            alertDialogBuilder.setView(alertView);
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertView.findViewById(R.id.btn_negative).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                    applicationDiabetoLog.setUser();
                    System.out.println("Negative!-Loading was cancelled");
                    gridViewAdapter = new GridViewAdapter(
                            getApplicationContext(),
                            activity,
                            applicationDiabetoLog.getUser().getVnosi()
                    );
                    gridView.setAdapter(gridViewAdapter);
                }
            });
            alertView.findViewById(R.id.btn_positive).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Positive!-Getting data!");
                    alertDialog.cancel();
                    firstTimeOpening=true;
                    Intent intent = new Intent(getApplication(), ActivityGoogleDrive.class);
                    intent.putExtra("FirstTimeOpening","true");
                    startActivity(intent);
//                    applicationDiabetoLog.setUporabnikTestScenario();
//                    gridViewAdapter = new GridViewAdapter(
//                            getApplicationContext(),
//                            activity,
//                            applicationDiabetoLog.getUser().getVnosi()
//                    );
//                    gridView.setAdapter(gridViewAdapter);
                }
            });
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
            }
        });
        findViewById(R.id.rl_vnos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addVnos(0, parentPanel);
                gridView.setAdapter(gridViewAdapter);
            }
        });
        findViewById(R.id.rl_obrok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addVnos(1, parentPanel);
                gridView.setAdapter(gridViewAdapter);
            }
        });
        findViewById(R.id.rl_aktivnost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addVnos(2, parentPanel);
                gridView.setAdapter(gridViewAdapter);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        applicationDiabetoLog.save();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("Info-MainWindow","Resuming MainWindow");
        if(firstTimeOpening){
            firstTimeOpening=false;
            Log.i("Info-Main","First time opening!");
        }
        if (applicationDiabetoLog.IMPORT) {
            Toast.makeText(getApplicationContext(), "Завантаження з Google Диска було успішним!", Toast.LENGTH_SHORT).show();
            applicationDiabetoLog.IMPORT = false;
        } else if(applicationDiabetoLog.IMPORT_FAILED) {
            applicationDiabetoLog.IMPORT_FAILED = false;
            if(applicationDiabetoLog.NO_FILES_FOUND){
                Toast.makeText(getApplicationContext(), "Не знайдено файлів імпорту на Google Диску!", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "Завантажити з Google Диска не вдалося!", Toast.LENGTH_SHORT).show();
            }
        }
        if (applicationDiabetoLog.EXPORT) {
            Toast.makeText(getApplicationContext(), "Завантаження на Google Диск було успішним!", Toast.LENGTH_SHORT).show();
            applicationDiabetoLog.EXPORT = false;
        } else if(applicationDiabetoLog.EXPORT_FAILED) {
            applicationDiabetoLog.EXPORT_FAILED = false;
            Toast.makeText(getApplicationContext(), "Завантаження на Google Диск не було успішним!", Toast.LENGTH_SHORT).show();
        }
        gridViewAdapter = new GridViewAdapter(
                getApplicationContext(),
                activity,
                applicationDiabetoLog.getUser().getVnosi()
        );
        gridView.setAdapter(gridViewAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main_window, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch(id){
            case R.id.action_settings:
                intent = new Intent(this,ActivitySettings.class);
                startActivity(intent);
                break;
            case R.id.action_google_drive:
                intent = new Intent(this, ActivityGoogleDrive.class);
                intent.putExtra("FirstTimeOpening","false");
                startActivity(intent);
                break;
            case R.id.action_statistika:
                filterDatePicker(this,parentPanel);
                break;
            case R.id.action_delete:
                if(gridViewAdapter.getDeleteCount()!=0){
                    AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
                    alertbox.setMessage("Готові видалити записи?");
                    alertbox.setTitle("Видалити");
                    alertbox.setIcon(android.R.drawable.ic_dialog_alert);
                    alertbox.setPositiveButton("Так", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            gridViewAdapter.deleteVnosi();
                        }
                    });
                    alertbox.setNegativeButton("Ні",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which)
                        {
                        }
                    });
                    alertbox.show();
                } else {
                    Toast.makeText(getApplicationContext(),"Немає виділених записів для видалення!",Toast.LENGTH_SHORT).show();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addVnos(final int type, ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        View alertView = null;
        //Vnos fields
        final EditText etSladkor, etKolicinaOh, etEnotInzulina;
        //Obrok extra fields
        final EditText etOpis;
        final Spinner spinnerTip;

        final Date datum = new Date();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(datum);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());
        switch(type){
            case 0:
                alertView = layoutInflater.inflate(R.layout.layout_vnos,parent,false);
                alertView.getBackground().setAlpha(40);
                etSladkor=(EditText)alertView.findViewById(R.id.et_sladkor);
                etKolicinaOh=(EditText)alertView.findViewById(R.id.et_kolicina_oh);
                etEnotInzulina=(EditText)alertView.findViewById(R.id.et_enot_inzulina);
                spinnerTip=null;
                etOpis=null;
                ((EditText)alertView.findViewById(R.id.et_datum)).setText(simpleDateFormat.format(datum));
                break;
            case 1:
                alertView = layoutInflater.inflate(R.layout.layout_obrok,parent,false);
                alertView.getBackground().setAlpha(40);
                etSladkor=(EditText)alertView.findViewById(R.id.et_sladkor);
                etKolicinaOh=(EditText)alertView.findViewById(R.id.et_kolicina_oh);
                etEnotInzulina=(EditText)alertView.findViewById(R.id.et_enot_inzulina);
                spinnerTip=(Spinner) alertView.findViewById(R.id.spinner);
                spinnerTip.setSelection(3,true);
                etOpis=(EditText)alertView.findViewById(R.id.et_opis);
                ((EditText)alertView.findViewById(R.id.et_datum)).setText(simpleDateFormat.format(datum));
                Button addButton = (Button) alertView.findViewById(R.id.notifi);
                //инициализировали наш массив с edittext.aьи
                allEds = new ArrayList<View>();
                //находим наш linear который у нас под кнопкой add edittext в activity_main.xml
                final LinearLayout linear = (LinearLayout) alertView.findViewById(R.id.linear);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        counter++;

                        //берем наш кастомный лейаут находим через него все наши кнопки и едит тексты, задаем нужные данные
                        final View view = getLayoutInflater().inflate(R.layout.custom_edittext_layout, null);
                        Button deleteField = (Button) view.findViewById(R.id.button2);
                        deleteField.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    ((LinearLayout) view.getParent()).removeView(view);
                                    allEds.remove(view);
                                } catch(IndexOutOfBoundsException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                        AutoCompleteTextView text = (AutoCompleteTextView) view.findViewById(R.id.timeinput);
                         //добавляем все что создаем в массив
                        allEds.add(view);
                        //добавляем елементы в linearlayout
                        linear.addView(view);
                        mList = retrievePeople();

                        text.setThreshold(1);
                        adapter = new AddAdapter(getApplicationContext(), R.layout.custom_edittext_layout, R.id.lbl_name, mList);
                        text.setAdapter(adapter);
                        EditText OH = (EditText) view.findViewById(R.id.aboutins);
                        EditText massa = (EditText) view.findViewById(R.id.massa);


                    }
                });

                Button showDataBtn = (Button) alertView.findViewById(R.id.button3);
                showDataBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //преобразуем наш ArrayList в просто String Array
                        String [] name = new String[allEds.size()];
                        int [] OH=new int[allEds.size()];
                        int [] mass=new int[allEds.size()];
                        String temp;
                        View view = getLayoutInflater().inflate(R.layout.layout_obrok, null);
                        //запускаем чтение всех елементов этого списка и запись в массив
                        for(int i=0; i < allEds.size(); i++) {
                            try {
                                name[i] = ((EditText) allEds.get(i).findViewById(R.id.timeinput)).getText().toString();

                                OH[i] = Integer.parseInt(name[i].replaceAll("\\D+", "")) / 12;

                                mass[i] = Integer.parseInt(((EditText) allEds.get(i).findViewById(R.id.massa)).getText().toString());

                                OH[i] = (mass[i] * OH[i] / 100);
                                temp = String.valueOf(OH[i]);
                                ((EditText) allEds.get(i).findViewById(R.id.aboutins)).setText(temp);
                                ((EditText) view.findViewById(R.id.oh_counter)).setText(temp);
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(getApplicationContext(), "Будь ласка, заповніть дані!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                break;
            case 2:
                alertView = layoutInflater.inflate(R.layout.layout_aktivnost,parent,false);
                alertView.getBackground().setAlpha(40);
                etSladkor=(EditText)alertView.findViewById(R.id.et_sladkor_add);
                etKolicinaOh=(EditText)alertView.findViewById(R.id.et_kolicina_oh);
                etEnotInzulina=(EditText)alertView.findViewById(R.id.et_enot_inzulina);
                spinnerTip=null;
                etOpis=null;
                ((EditText)alertView.findViewById(R.id.et_datum)).setText(simpleDateFormat.format(datum));
                alertView.findViewById(R.id.et_sladkorji).setVisibility(View.INVISIBLE);
                break;
            default:
                etSladkor=null;
                etKolicinaOh=null;
                etEnotInzulina=null;
                spinnerTip=null;
                etOpis=null;
                break;
        }
        etSladkor.setFilters(new InputFilter[]{
                new DecimalDigitsFilter(),
                new InputFilter.LengthFilter(4)
        });
        alertDialogBuilder.setView(alertView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        alertView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                closeMenu();
            }
        });
        alertView.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type!=1) {
                    if (!etSladkor.getText().toString().equals("")){
                        if (type == 0) {
                            Vnos noviVnos = fillVnos(etSladkor.getText().toString(),
                                    etKolicinaOh.getText().toString(),
                                    etEnotInzulina.getText().toString(),
                                    calendar
                            );
                            applicationDiabetoLog.getUser().getVnosi().add(0, noviVnos);
                        } else {
                            Aktivnost novaAktivnost = fillAktivnost(etSladkor.getText().toString(),
                                    etKolicinaOh.getText().toString(),
                                    etEnotInzulina.getText().toString(),
                                    calendar
                            );
                            applicationDiabetoLog.getUser().getVnosi().add(0, novaAktivnost);
                        }
                        gridViewAdapter.notifyDataSetChanged();
                        gridView.setAdapter(gridViewAdapter);
                        alertDialog.cancel();
                        closeMenu();
                    }else{
                        Toast.makeText(getApplicationContext(), "Будь ласка, введіть цукор!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if(!etKolicinaOh.getText().toString().equals("")) {
                        Obrok noviObrok = fillObrok(etSladkor.getText().toString(),
                                etKolicinaOh.getText().toString(),
                                etEnotInzulina.getText().toString(),
                                etOpis.getText().toString(),
                                spinnerTip.getSelectedItemPosition(),
                                calendar
                        );
                        applicationDiabetoLog.getUser().getVnosi().add(0, noviObrok);
                        gridViewAdapter.notifyDataSetChanged();
                        gridView.setAdapter(gridViewAdapter);
                        alertDialog.cancel();
                        closeMenu();
                    }else{
                        Toast.makeText(getApplicationContext(), "Будь ласка, введіть OH!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        alertView.findViewById(R.id.btn_calculate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double sladkor = 0d;
                Double kolicinaOH = 0d;
                if(!etKolicinaOh.getText().toString().equals("")){
                    kolicinaOH = Double.valueOf(etKolicinaOh.getText().toString());
                }
                if(!etSladkor.getText().toString().equals("")){
                    sladkor=Double.valueOf(etSladkor.getText().toString());
                }
                etEnotInzulina.setText(applicationDiabetoLog.izracunajPotrebneEnoteInzulina(new Vnos(
                        sladkor,
                        kolicinaOH)
                ));
            }
        });
    }

    public void infoVnos(Activity activity, final int id, ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        View alertView;
        final int type;//0=vnos, 1=obrok, 2=aktivnost
        final Vnos trenutni = applicationDiabetoLog.getUser().getVnosi().get(id);
        //Vnos fields
        final EditText etDatum, etSladkor, etKolicinaOh, etEnotInzulina;
        //Obrok extra fields
        final EditText etOpis;
        final Spinner spinnerTip;
        //Aktivnost extra fields
        final EditText etSladkorji, etKonecAktivnosti;
        final Button btnEndAktivnost;
        final ArrayList<Double>novoDodaniSladkorji=new ArrayList<>();
        final ImageButton btnAddSladkor;

        if(trenutni.getClass().equals(Vnos.class)){
            alertView = layoutInflater.inflate(R.layout.layout_vnos,parent,false);
            alertView.getBackground().setAlpha(40);
            etDatum=(EditText)alertView.findViewById(R.id.et_datum);
            etSladkor=(EditText)alertView.findViewById(R.id.et_sladkor);
            etKolicinaOh=(EditText)alertView.findViewById(R.id.et_kolicina_oh);
            etEnotInzulina=(EditText)alertView.findViewById(R.id.et_enot_inzulina);
            etOpis=null;
            etKonecAktivnosti=null;
            spinnerTip=null;
            alertView.findViewById(R.id.btn_calculate).setEnabled(false);

            etDatum.setText(trenutni.getDatumString());
            if(trenutni.getSladkor()!=null) {
                etSladkor.setText(String.valueOf(trenutni.getSladkor()));
            }else{
                etSladkor.setText("");
            }
            if(trenutni.getOH()!=null) {
                etKolicinaOh.setText(String.valueOf(trenutni.getOH()));
            }else{
                etKolicinaOh.setText("");
            }
            if(trenutni.getEnotInzulina()!=null) {
                etEnotInzulina.setText(String.valueOf(trenutni.getEnotInzulina()));
            }else{
                etEnotInzulina.setText("");
            }

            type=0;
            etSladkor.setEnabled(false);
            etKolicinaOh.setEnabled(false);
            etEnotInzulina.setEnabled(false);
        } else if(trenutni.getClass().equals(Obrok.class)){
            alertView = layoutInflater.inflate(R.layout.layout_obrok,parent,false);
            alertView.getBackground().setAlpha(40);
            etDatum=(EditText)alertView.findViewById(R.id.et_datum);
            etSladkor=(EditText)alertView.findViewById(R.id.et_sladkor);
            etKolicinaOh=(EditText)alertView.findViewById(R.id.et_kolicina_oh);
            etEnotInzulina=(EditText)alertView.findViewById(R.id.et_enot_inzulina);
            spinnerTip=(Spinner) alertView.findViewById(R.id.spinner);
            etOpis=(EditText)alertView.findViewById(R.id.et_opis);
            etKonecAktivnosti=null;
            alertView.findViewById(R.id.btn_calculate).setEnabled(false);

            etDatum.setText(trenutni.getDatumString());
            if(trenutni.getSladkor()!=null) {
                etSladkor.setText(String.valueOf(trenutni.getSladkor()));
            }else{
                etSladkor.setText("");
            }
            if(trenutni.getOH()!=null) {
                etKolicinaOh.setText(String.valueOf(trenutni.getOH()));
            }else{
                etKolicinaOh.setText("");
            }
            if(trenutni.getEnotInzulina()!=null) {
                etEnotInzulina.setText(String.valueOf(trenutni.getEnotInzulina()));
            }else{
                etEnotInzulina.setText("");
            }
            switch(((Obrok)trenutni).getTip()){
                case Eat1:
                    spinnerTip.setSelection(0,true);
                    break;
                case Eat2:
                    spinnerTip.setSelection(1,true);
                    break;
                case Eat3:
                    spinnerTip.setSelection(2,true);
                    break;
                default:
                    spinnerTip.setSelection(3,true);
                    break;
            }
            etOpis.setText(String.valueOf(((Obrok) trenutni).getOpis()));
            type=1;
            etSladkor.setEnabled(false);
            etKolicinaOh.setEnabled(false);
            etEnotInzulina.setEnabled(false);
            spinnerTip.setEnabled(false);
            etOpis.setEnabled(false);
        } else {
            alertView = layoutInflater.inflate(R.layout.layout_aktivnost,parent,false);
            alertView.getBackground().setAlpha(40);
            etDatum=(EditText)alertView.findViewById(R.id.et_datum);
            etSladkor=(EditText)alertView.findViewById(R.id.et_sladkor_edit);
            etKolicinaOh=(EditText)alertView.findViewById(R.id.et_kolicina_oh);
            etEnotInzulina=(EditText)alertView.findViewById(R.id.et_enot_inzulina);
            etSladkorji=(EditText)alertView.findViewById(R.id.et_sladkorji);
            etKonecAktivnosti=(EditText)alertView.findViewById(R.id.et_konec_aktivnosti);
            etOpis=null;
            spinnerTip=null;
            alertView.findViewById(R.id.btn_calculate).setEnabled(false);

            alertView.findViewById(R.id.edit_layout).setVisibility(View.VISIBLE);
            alertView.findViewById(R.id.til_add).setVisibility(View.INVISIBLE);
            btnAddSladkor=(ImageButton)alertView.findViewById(R.id.btn_add);
            btnAddSladkor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    novoDodaniSladkorji.add(Double.valueOf(etSladkor.getText().toString()));
                    etSladkorji.append(","+etSladkor.getText().toString());
                    etSladkor.setText("");
                }
            });

            etDatum.setText(trenutni.getDatumString());
            if(trenutni.getSladkor()!=null) {
                etSladkorji.setText(String.valueOf(trenutni.getSladkor())+((Aktivnost)trenutni).sladkorjiToString());
            }else{
                etSladkor.setText("");
            }
            if(trenutni.getOH()!=null) {
                etKolicinaOh.setText(String.valueOf(trenutni.getOH()));
            }else{
                etKolicinaOh.setText("");
            }
            if(trenutni.getEnotInzulina()!=null) {
                etEnotInzulina.setText(String.valueOf(trenutni.getEnotInzulina()));
            }else{
                etEnotInzulina.setText("");
            }
            etKonecAktivnosti.setText(((Aktivnost)trenutni).getKonecAktivnosti());
            if(etKonecAktivnosti.getText().toString().equals("Операція не завершена")){
                btnEndAktivnost=(Button)alertView.findViewById(R.id.btn_end_aktivnost);
                btnEndAktivnost.setEnabled(true);
                btnEndAktivnost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());
                        etKonecAktivnosti.setText(simpleDateFormat.format(new Date()));
                        btnEndAktivnost.setEnabled(false);
                        btnAddSladkor.setEnabled(false);
                        btnAddSladkor.setVisibility(View.INVISIBLE);
                        etSladkor.setEnabled(false);
                    }
                });
            }else{
                btnAddSladkor.setEnabled(false);
                btnAddSladkor.setVisibility(View.INVISIBLE);
                etSladkor.setEnabled(false);
            }
            type=2;
            etKolicinaOh.setEnabled(false);
            etEnotInzulina.setEnabled(false);
        }
        etSladkor.setFilters(new InputFilter[]{
                new DecimalDigitsFilter(),
                new InputFilter.LengthFilter(4)
        });
        alertDialogBuilder.setView(alertView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        alertView.findViewById(R.id.btn_cancel).setVisibility(View.INVISIBLE);
        alertView.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type==2) {
                    ((Aktivnost) trenutni).appendSladkorji(novoDodaniSladkorji);
                    if (!etKonecAktivnosti.getText().toString().equals("Операція не завершена")) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());
                        try {
                            ((Aktivnost) trenutni).setKonecAktivnosti(simpleDateFormat.parse(etKonecAktivnosti.getText().toString()));
                        } catch (ParseException e) {
                            Log.e("Error", "This should not happen.");
                        }
                    }
                    applicationDiabetoLog.getUser().getVnosi().set(id, trenutni);
                }
                gridViewAdapter.notifyDataSetChanged();
                gridView.setAdapter(gridViewAdapter);
                alertDialog.cancel();
                closeMenu();
            }
        });
    }

    public void deleteVnos(ArrayList<Integer> arrayList){
        for(int i=0;i<arrayList.size();i++){
            applicationDiabetoLog.getUser().getVnosi().remove((int)arrayList.get(i));
        }
        gridViewAdapter.setLongClickFalse();
        gridViewAdapter.notifyDataSetChanged();
        gridView.setAdapter(gridViewAdapter);
    }

    private void filterDatePicker(final Activity activity, ViewGroup parent){
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        final View alertView = layoutInflater.inflate(R.layout.layout_filter_date_picker,parent,false);
        alertDialogBuilder.setView(alertView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        final LocalDateTime localDateTime = new LocalDateTime();
        alertDialog.show();
        alertView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertView.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((EditText)alertView.findViewById(R.id.et_od)).getText().toString().equals("") || ((EditText)alertView.findViewById(R.id.et_do)).getText().toString().equals("")){
                    Toast.makeText(activity,"Виберіть обидві дати!", Toast.LENGTH_SHORT).show();
                }else {
                    alertDialog.cancel();
                    Intent intent = new Intent(activity, ActivityStatistics.class);
                    intent.putExtra("datumOd",((EditText)alertView.findViewById(R.id.et_od)).getText().toString());
                    intent.putExtra("datumDo",((EditText)alertView.findViewById(R.id.et_do)).getText().toString());
                    startActivity(intent);
                }
            }
        });
        alertView.findViewById(R.id.et_od).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        ((EditText)alertView.findViewById(R.id.et_od)).setText(dayOfMonth+"."+monthOfYear+"."+year);
                    }
                },localDateTime.getYear(),localDateTime.getMonthOfYear(),localDateTime.getDayOfMonth());
                datePickerDialog.setTitle("Від:");
                datePickerDialog.show();
            }
        });
        alertView.findViewById(R.id.et_do).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        ((EditText)alertView.findViewById(R.id.et_do)).setText(dayOfMonth+"."+monthOfYear+"."+year);
                    }
                },localDateTime.getYear(),localDateTime.getMonthOfYear(),localDateTime.getDayOfMonth());
                datePickerDialog.setTitle("До:");
                datePickerDialog.show();
            }
        });
    }

    private Vnos fillVnos(String sladkor, String kolicinaOH, String enotInzulina, Calendar datum){
        Vnos vnos = new Vnos(datum);
        vnos.setSladkor(Double.valueOf(sladkor));
        if(!kolicinaOH.equals("")){
            vnos.setOH(Double.valueOf(kolicinaOH));
        }else{
            vnos.setOH(0d);
        }
        if(!enotInzulina.equals("")){
            vnos.setEnotInzulina(Double.valueOf(enotInzulina));
        }else{
            vnos.setEnotInzulina(0d);
        }
        return vnos;
    }

    private Obrok fillObrok(String sladkor, String kolicinaOH, String enotInzulina, String opis, Integer position , Calendar datum){
        Obrok vnos = new Obrok(datum);
        if(!sladkor.equals("")) {
            vnos.setSladkor(Double.valueOf(sladkor));
        }else{
            vnos.setSladkor(null);
        }
        vnos.setOH(Double.valueOf(kolicinaOH));
        if(!enotInzulina.equals("")){
            vnos.setEnotInzulina(Double.valueOf(enotInzulina));
        }else{
            vnos.setEnotInzulina(0d);
        }
        if(!opis.equals("")){
            vnos.setOpis(opis);
        }else{
            vnos.setOpis("");
        }
        vnos.setTip(position);
        return vnos;
    }

    private Aktivnost fillAktivnost(String sladkor, String kolicinaOH, String enotInzulina, Calendar datum){
        Aktivnost vnos = new Aktivnost(datum);
        vnos.setSladkor(Double.valueOf(sladkor));
        if(!kolicinaOH.equals("")){
            vnos.setOH(Double.valueOf(kolicinaOH));
        }else{
            vnos.setOH(0d);
        }
        if(!enotInzulina.equals("")){
            vnos.setEnotInzulina(Double.valueOf(enotInzulina));
        }else{
            vnos.setEnotInzulina(0d);
        }
        return vnos;
    }

    void openMenu(){
        isMenuOpen=true;
        final RelativeLayout rlAktivnost =(RelativeLayout)findViewById(R.id.rl_aktivnost);
        final RelativeLayout rlObrok = (RelativeLayout)findViewById(R.id.rl_obrok);
        final RelativeLayout rlVnos = (RelativeLayout)findViewById(R.id.rl_vnos);
        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        final FloatingActionButton fabAktivnost=(FloatingActionButton)findViewById(R.id.fab_aktivnost);
        final FloatingActionButton fabObrok=(FloatingActionButton)findViewById(R.id.fab_obrok);
        final FloatingActionButton fabVnos=(FloatingActionButton) findViewById(R.id.fab_vnos);
        final TextView tvAktivnost = (TextView)findViewById(R.id.tv_aktivnost);
        final TextView tvObrok = (TextView)findViewById(R.id.tv_obrok);
        final TextView tvVnos = (TextView)findViewById(R.id.tv_input);

        if(rlAktivnost!=null && rlObrok!=null && rlVnos!=null && frameLayout !=null &&
                fabAktivnost!=null && fabObrok!=null && fabVnos!=null &&
                tvAktivnost!=null && tvObrok!=null && tvVnos!=null) {
            frameLayout.setBackgroundColor(getResources().getColor(R.color.dimBackground,getTheme()));
            frameLayout.setEnabled(true);
            frameLayout.setClickable(true);
            frameLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.frame_layout_open));

            rlVnos.setVisibility(View.VISIBLE);
            fabVnos.setVisibility(View.VISIBLE);
            tvVnos.setVisibility(View.VISIBLE);
            rlVnos.setClickable(true);
            rlVnos.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_menu_open));

            rlObrok.setVisibility(View.VISIBLE);
            fabObrok.setVisibility(View.VISIBLE);
            tvObrok.setVisibility(View.VISIBLE);
            rlObrok.setClickable(true);
            rlObrok.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_menu_open));

            rlAktivnost.setVisibility(View.VISIBLE);
            fabAktivnost.setVisibility(View.VISIBLE);
            tvAktivnost.setVisibility(View.VISIBLE);
            rlAktivnost.setClickable(true);
            rlAktivnost.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_menu_open));
        }
    }

    void closeMenu(){
        isMenuOpen=false;
        final RelativeLayout rlAktivnost =(RelativeLayout)findViewById(R.id.rl_aktivnost);
        final RelativeLayout rlObrok = (RelativeLayout)findViewById(R.id.rl_obrok);
        final RelativeLayout rlVnos = (RelativeLayout)findViewById(R.id.rl_vnos);
        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        final FloatingActionButton fabAktivnost=(FloatingActionButton)findViewById(R.id.fab_aktivnost);
        final FloatingActionButton fabObrok=(FloatingActionButton)findViewById(R.id.fab_obrok);
        final FloatingActionButton fabVnos=(FloatingActionButton) findViewById(R.id.fab_vnos);
        final TextView tvAktivnost = (TextView)findViewById(R.id.tv_aktivnost);
        final TextView tvObrok = (TextView)findViewById(R.id.tv_obrok);
        final TextView tvVnos = (TextView)findViewById(R.id.tv_input);

        if(rlAktivnost!=null && rlObrok!=null && rlVnos!=null && frameLayout !=null &&
                fabAktivnost!=null && fabObrok!=null && fabVnos!=null &&
                tvAktivnost!=null && tvObrok!=null && tvVnos!=null) {
            Animation fabMenuCloseAnimation =AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_menu_close);
            fabMenuCloseAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //Do Nothing
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    rlAktivnost.setVisibility(View.INVISIBLE);
                    fabAktivnost.setVisibility(View.INVISIBLE);
                    tvAktivnost.setVisibility(View.INVISIBLE);
                    rlAktivnost.setClickable(false);


                    rlObrok.setVisibility(View.INVISIBLE);
                    fabObrok.setVisibility(View.INVISIBLE);
                    tvObrok.setVisibility(View.INVISIBLE);
                    rlObrok.setClickable(false);


                    rlVnos.setVisibility(View.INVISIBLE);
                    fabVnos.setVisibility(View.INVISIBLE);
                    tvVnos.setVisibility(View.INVISIBLE);
                    rlVnos.setClickable(false);


                    frameLayout.setEnabled(false);
                    frameLayout.setClickable(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    //Do Nothing
                }
            });
            rlAktivnost.startAnimation(fabMenuCloseAnimation);
            rlObrok.startAnimation(fabMenuCloseAnimation);
            rlVnos.startAnimation(fabMenuCloseAnimation);
            frameLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.frame_layout_close));

        }
    }
}