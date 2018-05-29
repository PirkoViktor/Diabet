package Data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Aktivnost extends Vnos
{
    private Date konecAktivnosti;
    private ArrayList<Double> sladkorji;

    public Aktivnost(double sladkor, double OH, double enotInzulina, Calendar datum) {
        super(sladkor, OH, enotInzulina, datum);
        this.konecAktivnosti = null;
        this.sladkorji = new ArrayList<>();
    }

    public Aktivnost(Calendar datum) {
        super(datum);
        this.konecAktivnosti = null;
        this.sladkorji = new ArrayList<>();
    }

    public String getKonecAktivnosti() {
        if(konecAktivnosti!=null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());
            return simpleDateFormat.format(konecAktivnosti);
        } else {
            return "Операція не завершена";
        }

    }

    public void setKonecAktivnosti(Date konecAktivnosti) {
        this.konecAktivnosti = konecAktivnosti;
    }

    public ArrayList<Double> getSladkorji() {
        return sladkorji;
    }

    public String sladkorjiToString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<sladkorji.size();i++){
            stringBuilder.append(",").append(sladkorji.get(i));
        }
        return stringBuilder.toString();
    }
    public void appendSladkorji(ArrayList<Double> arrayList){
        sladkorji.addAll(arrayList);
    }
}
