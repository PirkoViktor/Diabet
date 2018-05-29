package com.ales.diabetolog;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import Data.Aktivnost;
import Data.Nastavitev;
import Data.Obrok;
import Data.User;
import Data.Vnos;


public class ApplicationDiabetoLog extends Application
{
    private User user;
    private static final String FILE_NAME_BAZALNI_ODMERKI="Bazalni_odmerki.json";
    private static final String FILE_NAME_RAZMERJA_OH="Razmerja_OH.json";
    private static final String FILE_NAME_OBCUTLJIVOST="Obcutljivost.json";
    private static final String FILE_NAME_CAS_CILJNA="Cas_delovanja_in_ciljna_gk.json";
    private static final String FILE_NAME_VNOSI="Vnosi.json";
    private static final String MAP_NAME="Data";
    boolean IMPORT=false,IMPORT_FAILED=false, EXPORT=false, EXPORT_FAILED=false, NO_FILES_FOUND=false;

    public User getUser(){
        return user;
    }

    public void setUser(){
        user = new User();
    }

    public void save(){
        Log.i("Info-AppDiabetoLog","Saving data into Json file");
        if(isExternalStorageWritable()){
            File[] files = new File[5];
            files[0]=new File(this.getExternalFilesDir(MAP_NAME),FILE_NAME_BAZALNI_ODMERKI);
            files[1]=new File(this.getExternalFilesDir(MAP_NAME),FILE_NAME_RAZMERJA_OH);
            files[2]=new File(this.getExternalFilesDir(MAP_NAME),FILE_NAME_OBCUTLJIVOST);
            files[3]=new File(this.getExternalFilesDir(MAP_NAME),FILE_NAME_CAS_CILJNA);
            files[4]=new File(this.getExternalFilesDir(MAP_NAME),FILE_NAME_VNOSI);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            PrintWriter printWriter;
            try{
                for(int i=0;i<files.length;i++){
                    String jsonString="";
                    switch (i){
                        case 0:
                            if(user.getBazalniOdmerki().size()!=0) {
                                ArrayList<Nastavitev> arrayList = user.getBazalniOdmerki();
                                for(int j=0;j<arrayList.size();j++){
                                    if(arrayList.get(j).toString()!=null) {
                                        jsonString += arrayList.get(j).toString()+"\n";
                                    }
                                }
                            }
                            break;
                        case 1:
                            if(user.getRazmerjaOH().size()!=0) {
                                ArrayList<Nastavitev> arrayList = user.getRazmerjaOH();
                                for(int j=0;j<arrayList.size();j++){
                                    if(arrayList.get(j).toString()!=null) {
                                        jsonString += arrayList.get(j).toString()+"\n";
                                    }
                                }
                            }
                            break;
                        case 2:
                            if(user.getObcutljivost().size()!=0) {
                                ArrayList<Nastavitev> arrayList = user.getObcutljivost();
                                for(int j=0;j<arrayList.size();j++){
                                    if(arrayList.get(j).toString()!=null) {
                                        jsonString += arrayList.get(j).toString()+"\n";
                                    }
                                }
                            }
                            break;
                        case 3:
                            if(user.getCasDelovanjaInzulina()!=null && user.getCiljnaGKSpodnjaMeja()!=null && user.getCiljnaGKZgornjaMeja()!=null) {
                                jsonString = gson.toJson(user.getCasDelovanjaInzulina().getHourOfDay()+":"+user.getCasDelovanjaInzulina().getMinuteOfHour());
                                jsonString += "\n" + gson.toJson(user.getCiljnaGKSpodnjaMeja());
                                jsonString += "\n" + gson.toJson(user.getCiljnaGKZgornjaMeja());
                            }
                            break;
                        case 4:
                            if(user.getVnosi().size()!=0) {
                                RuntimeTypeAdapterFactory runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                                        .of(Vnos.class, "Vnos")
                                        .registerSubtype(Vnos.class, "Vnos")
                                        .registerSubtype(Obrok.class, "Obrok")
                                        .registerSubtype(Aktivnost.class, "Aktivnost");
                                gson = new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).setPrettyPrinting().create();
                                jsonString = gson.toJson(user.getVnosi(), new TypeToken<ArrayList<Vnos>>() {
                                }.getType());
                            }
                            break;
                    }
                    printWriter = new PrintWriter(files[i]);
                    if(!jsonString.equals("")){
                        Log.i("Info-AppDiabetoLog",i+" "+files[i].getName());
                        printWriter.println(jsonString);
                        printWriter.close();
                    } else {
                        if(files[i].delete()){
                            Log.i("Info-AppDiabetoLog","Nothing to save, file was succesfully deleted");
                        }
                    }

                }
            }catch (FileNotFoundException e){
                Log.e("ERROR-AppDiabetoLog","FILE NOT FOUND!");
            }
        } else {
            Log.e("ERROR-AppDiabetoLog","STORAGE IS NOT WRITABLE!");
        }
        Log.i("Info-AppDiabetoLog","Saving is completed.");
    }

    public boolean wasLoadSuccesfull(){
        user = load();
        if(user!=null){
            return true;
        }else {
            user = new User();
        }
        return false;
    }

    @Nullable
    private User load(){
        if(isExternalStorageReadable()){
            User userLoad = new User();
            File[] files = new File[5];
            files[0]=new File(this.getExternalFilesDir(MAP_NAME),FILE_NAME_BAZALNI_ODMERKI);
            files[1]=new File(this.getExternalFilesDir(MAP_NAME),FILE_NAME_RAZMERJA_OH);
            files[2]=new File(this.getExternalFilesDir(MAP_NAME),FILE_NAME_OBCUTLJIVOST);
            files[3]=new File(this.getExternalFilesDir(MAP_NAME),FILE_NAME_CAS_CILJNA);
            files[4]=new File(this.getExternalFilesDir(MAP_NAME),FILE_NAME_VNOSI);
            FileInputStream fileInputStream;
            BufferedReader bufferedReader;
            StringBuilder stringBuilder;
            for(int i=0;i<5;i++) {
                try {
                    fileInputStream = new FileInputStream(files[i]);
                } catch (FileNotFoundException e) {
                    Log.e("ERROR-AppDiabetoLog", i+" FILE NOT FOUND:" + files[i].getName());
                    Log.i("Info-AppDiabetoLog", "Continuing with loading");
                    fileInputStream=null;
                }
                if(fileInputStream!=null) {
                    Log.i("Info-AppDiabetoLog", i+" File found:" + files[i].getName());
                    bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                    stringBuilder = new StringBuilder();
                    String readLine;
                    try {
                        while ((readLine = bufferedReader.readLine()) != null) {
                            stringBuilder.append(readLine).append("\n");
                        }
                    } catch (IOException e) {
                        Log.e("ERROR-AppDiabetoLog", "ERROR WHILE READING FILE:" + files[i].getName());
                    }
                    switch (i) {
                        case 0:
                            userLoad.setBazalniOdmerki(processLoadData(stringBuilder.toString()));
                            break;
                        case 1:
                            userLoad.setRazmerjaOH(processLoadData(stringBuilder.toString()));
                            break;
                        case 2:
                            userLoad.setObcutljivost(processLoadData(stringBuilder.toString()));
                            break;
                        case 3:
                            String[] splitString = stringBuilder.toString().replace("\"", "").split("\n");
                            String[] timeString = splitString[0].split(":");
                            userLoad.setCasDelovanjaInzulina(new LocalTime(
                                    Integer.valueOf(timeString[0]),
                                    Integer.valueOf(timeString[1])
                            ));
                            userLoad.setCiljnaGKSpodnjaMeja(Double.valueOf(splitString[1]));
                            userLoad.setCiljnaGKZgornjaMeja(Double.valueOf(splitString[2]));
                            break;
                        case 4:
                            RuntimeTypeAdapterFactory runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                                    .of(Vnos.class, "Vnos")
                                    .registerSubtype(Vnos.class, "Vnos")
                                    .registerSubtype(Obrok.class, "Obrok")
                                    .registerSubtype(Aktivnost.class, "Aktivnost");
                            Gson gson = new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).setPrettyPrinting().create();
                            ArrayList<Vnos> vnosArrayList = gson.fromJson(stringBuilder.toString(), new TypeToken<ArrayList<Vnos>>() {
                            }.getType());
                            userLoad.setVnosi(vnosArrayList);
                            break;
                    }
                    try {
                        fileInputStream.close();
                    }catch (IOException e){
                        Log.e("ERROR-AppDiabetoLog", "ERROR WHILE CLOSING STREAM!");
                    }finally {
                        Log.i("Info-AppDiabetoLog","Streams closed!");
                    }
                }
            }
            if(checkUserSetValues(userLoad)){
                return userLoad;
            }
        } else {
            Log.e("ERROR-AppDiabetoLog","STORAGE IS NOT READABLE");
        }
        return null;
    }

    public boolean isExternalStorageWritable() {
        String state= Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean isExternalStorageReadable() {
        String state= Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||  Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    private boolean checkUserSetValues(User testUser){
        return !(testUser.getVnosi().size() == 0 &&
                testUser.getObcutljivost().size() == 0 &&
                testUser.getRazmerjaOH().size() == 0 &&
                testUser.getBazalniOdmerki().size() == 0 &&
                testUser.getCasDelovanjaInzulina() == null &&
                testUser.getCiljnaGKSpodnjaMeja() == null &&
                testUser.getCiljnaGKZgornjaMeja() == null);
    }

    private ArrayList<Nastavitev> processLoadData(String string){
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("HH:mm");
        String[] stringSplitByNewLine = string.split("\n");
        ArrayList<Nastavitev> arrayList = new ArrayList<>();
        for (String aStringSplitByNewLine : stringSplitByNewLine) {
            String[] stringSplit = aStringSplitByNewLine.split(",");
            LocalTime from = dateFormat.parseLocalTime(stringSplit[0]);
            LocalTime to = dateFormat.parseLocalTime(stringSplit[1]);
            Double value = Double.valueOf(stringSplit[2]);
            Nastavitev nastavitev = new Nastavitev(from,to,value);
            arrayList.add(nastavitev);
        }
        return arrayList;
    }

    public String getExportData(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        StringBuilder stringBuilder = new StringBuilder();
        if(user.getBazalniOdmerki().size()!=0) {
            ArrayList<Nastavitev> arrayList = user.getBazalniOdmerki();
            for(int j=0;j<arrayList.size();j++){
                if(arrayList.get(j).toString()!=null) {
                    stringBuilder.append(arrayList.get(j).toString()).append("\n");
                }
            }
        }
        stringBuilder.append("-----------------\n");
        if(user.getRazmerjaOH().size()!=0) {
            ArrayList<Nastavitev> arrayList = user.getRazmerjaOH();
            for(int j=0;j<arrayList.size();j++){
                if(arrayList.get(j).toString()!=null) {
                    stringBuilder.append(arrayList.get(j).toString()).append("\n");
                }
            }
        }
        stringBuilder.append("-----------------\n");
        if(user.getObcutljivost().size()!=0) {
            ArrayList<Nastavitev> arrayList = user.getObcutljivost();
            for(int j=0;j<arrayList.size();j++){
                if(arrayList.get(j).toString()!=null) {
                    stringBuilder.append(arrayList.get(j).toString()).append("\n");
                }
            }
        }
        stringBuilder.append("-----------------\n");
        if(user.getCasDelovanjaInzulina()!=null && user.getCiljnaGKSpodnjaMeja()!=null && user.getCiljnaGKZgornjaMeja()!=null) {
            stringBuilder.append(gson.toJson(user.getCasDelovanjaInzulina().getHourOfDay()))
                    .append(":")
                    .append(user.getCasDelovanjaInzulina().getMinuteOfHour())
                    .append("\n");
            stringBuilder.append(gson.toJson(user.getCiljnaGKSpodnjaMeja())).append("\n");
            stringBuilder.append(gson.toJson(user.getCiljnaGKZgornjaMeja())).append("\n");
        }
        stringBuilder.append("-----------------\n");
        if(user.getVnosi().size()!=0) {
            RuntimeTypeAdapterFactory runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                    .of(Vnos.class, "Vnos")
                    .registerSubtype(Vnos.class, "Vnos")
                    .registerSubtype(Obrok.class, "Obrok")
                    .registerSubtype(Aktivnost.class, "Aktivnost");
            gson = new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).setPrettyPrinting().create();
            stringBuilder.append(
                    gson.toJson(
                            user.getVnosi(),
                            new TypeToken<ArrayList<Vnos>>(){}.getType()
                    )
            );
        }
        return stringBuilder.toString();
    }

    public boolean saveImportData(BufferedReader bufferedReader){
        Log.i("Info-AppDiabetoLog","Saving imported data...");
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        }catch (IOException e){
            Log.e("Error-AppDiabetoLog","Error while reading from google drive stream!");
            return false;
        }
        String[] strings= stringBuilder.toString().split("-----------------\n");
        for(int i=0;i<strings.length;i++) {
            switch (i) {
                case 0:
                    user.setBazalniOdmerki(processLoadData(strings[i]));
                    break;
                case 1:
                    user.setRazmerjaOH(processLoadData(strings[i]));
                    break;
                case 2:
                    user.setObcutljivost(processLoadData(strings[i]));
                    break;
                case 3:
                    String[] splitString = strings[i].replace("\"", "").split("\n");
                    String[] timeString = splitString[0].split(":");
                    user.setCasDelovanjaInzulina(new LocalTime(
                            Integer.valueOf(timeString[0]),
                            Integer.valueOf(timeString[1])
                    ));
                    user.setCiljnaGKSpodnjaMeja(Double.valueOf(splitString[1]));
                    user.setCiljnaGKZgornjaMeja(Double.valueOf(splitString[2]));
                    break;
                case 4:
                    RuntimeTypeAdapterFactory runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                            .of(Vnos.class, "Vnos")
                            .registerSubtype(Vnos.class, "Vnos")
                            .registerSubtype(Obrok.class, "Obrok")
                            .registerSubtype(Aktivnost.class, "Aktivnost");
                    Gson gson = new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).setPrettyPrinting().create();
                    ArrayList<Vnos> vnosArrayList = gson.fromJson(strings[i], new TypeToken<ArrayList<Vnos>>() {
                    }.getType());
                    user.setVnosi(vnosArrayList);
                    break;
            }
        }
        Log.i("Info-AppDiabetoLog","Importing from Google Drive is finished.");
        IMPORT=true;
        return true;
    }

    public String izracunajPotrebneEnoteInzulina(Vnos vnos){
        LocalDateTime localDateTime = new LocalDateTime();
        LocalTime casDelovanjaInzulina= user.getCasDelovanjaInzulina();
        Double spodnjaMeja = user.getCiljnaGKSpodnjaMeja();
        Double zgornjaMeja = user.getCiljnaGKZgornjaMeja();
        Double razmerjeOH = user.getNastavitevValue(0,localDateTime);//type-> 0=razmerja OH, 1=obcutljivost
        Double obcutljivost = user.getNastavitevValue(1,localDateTime);//type-> 0=razmerja OH, 1=obcutljivost
        Double trenutniSladkor = vnos.getSladkor();
        Double trenutniOH = vnos.getOH();
        Double ocenaZaHrano=0d, ocenaZaKorekcijo=0d;
        if(casDelovanjaInzulina == null
                || spodnjaMeja == null
                || zgornjaMeja == null){
            Toast.makeText(getApplicationContext(),"Даних, необхідних для обчислення, немає! " +
                    "Перевірте налаштування!",Toast.LENGTH_SHORT).show();
            return "";
        }
        if(trenutniOH!=0d){
            if(razmerjeOH==null){
                Toast.makeText(getApplicationContext(),"Немає поточного часу " +
                        "Налаштування на OH!",Toast.LENGTH_SHORT).show();
                return "";
            }
            ocenaZaHrano=vnos.getOH()/razmerjeOH;
        }
        if(trenutniSladkor!=0d) {
            if(obcutljivost==null){
                Toast.makeText(getApplicationContext(),"Немає налаштувань для поточного часу " +
                        "чутливість до інсуліну!",Toast.LENGTH_SHORT).show();
                return "";
            }
            Double aktivniInzuluin = user.getTrenutniAktivniInzulin(localDateTime);
            if (aktivniInzuluin != null) {
                if (trenutniSladkor > zgornjaMeja) {
                    ocenaZaKorekcijo = izracunajKorekcijo(trenutniSladkor, spodnjaMeja, zgornjaMeja, obcutljivost);
                    if (ocenaZaKorekcijo > aktivniInzuluin) {
                        ocenaZaKorekcijo -= aktivniInzuluin;
                    } else {
                        ocenaZaKorekcijo = 0d;
                    }
                } else {
                    ocenaZaKorekcijo = izracunajKorekcijo(trenutniSladkor, spodnjaMeja, zgornjaMeja, obcutljivost);
                }
            } else {
                ocenaZaKorekcijo = izracunajKorekcijo(trenutniSladkor, spodnjaMeja, zgornjaMeja, obcutljivost);
            }
        }
        Double ocena=ocenaZaHrano+ocenaZaKorekcijo;
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        return decimalFormat.format(ocena);
    }

    private Double izracunajKorekcijo(Double trenutniSladkor,Double spodnjaMeja, Double zgornjaMeja, Double obcutljivost){
        if(trenutniSladkor>zgornjaMeja){
            return (trenutniSladkor-zgornjaMeja)/obcutljivost;
        }else if(trenutniSladkor<spodnjaMeja){
            return(trenutniSladkor-spodnjaMeja)/obcutljivost;
        }
        return 0d;
    }
}

//    public void setUporabnikTestScenario(){
//        user.setVnosi(new TestEntries().testEntries());
//        user.setBazalniOdmerki(new TestEntries().testBazal());
//        user.setRazmerjaOH(new TestEntries().testOHrazmerja());
//        user.setObcutljivost(new TestEntries().testObcutljivost());
//        user.setCiljnaGKSpodnjaMeja(5.0);
//        user.setCiljnaGKZgornjaMeja(6.8);
//        user.setCasDelovanjaInzulina(new LocalTime(3,0));
//    }
