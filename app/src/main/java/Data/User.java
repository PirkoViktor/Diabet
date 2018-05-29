package Data;

import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Calendar;

public class User {
    private ArrayList<Vnos> vnosi;
    private ArrayList<Nastavitev> bazalniOdmerki;
    private ArrayList<Nastavitev> razmerjaOH;
    private ArrayList<Nastavitev> obcutljivost;
    private LocalTime casDelovanjaInzulina;
    private Double ciljnaGKSpodnjaMeja;
    private Double ciljnaGKZgornjaMeja;

    public User() {
        vnosi= new ArrayList<>();
        bazalniOdmerki =  new ArrayList<>();
        razmerjaOH =  new ArrayList<>();
        obcutljivost = new ArrayList<>();
        casDelovanjaInzulina = null;
        ciljnaGKSpodnjaMeja = null;
        ciljnaGKZgornjaMeja = null;
    }
    public void addBazalniOdmerek(Nastavitev nastavitev){
        bazalniOdmerki.add(nastavitev);
    }

    public void addRazmerjaOH(Nastavitev nastavitev){
        razmerjaOH.add(nastavitev);
    }

    public void addObcutljivost(Nastavitev nastavitev){
        obcutljivost.add(nastavitev);
    }

    public void setVnosi(ArrayList<Vnos> vnosi) {
        this.vnosi = vnosi;
    }

    public void setBazalniOdmerki(ArrayList<Nastavitev> bazalniOdmerki) {
        this.bazalniOdmerki = bazalniOdmerki;
    }

    public void setRazmerjaOH(ArrayList<Nastavitev> razmerjaOH) {
        this.razmerjaOH = razmerjaOH;
    }

    public void setObcutljivost(ArrayList<Nastavitev> obcutljivost) {
        this.obcutljivost = obcutljivost;
    }

    public void setCasDelovanjaInzulina(LocalTime casDelovanjaInzulina) {
        this.casDelovanjaInzulina = casDelovanjaInzulina;
    }

    public void setCiljnaGKSpodnjaMeja(double ciljnaGKSpodnjaMeja) {
        this.ciljnaGKSpodnjaMeja = ciljnaGKSpodnjaMeja;
    }

    public void setCiljnaGKZgornjaMeja(Double ciljnaGKZgornjaMeja) {
        this.ciljnaGKZgornjaMeja = ciljnaGKZgornjaMeja;
    }

    public ArrayList<Vnos> getVnosi() {
        return vnosi;
    }

    public ArrayList<Nastavitev> getBazalniOdmerki() {
        return bazalniOdmerki;
    }

    public ArrayList<Nastavitev> getRazmerjaOH() {
        return razmerjaOH;
    }

    public ArrayList<Nastavitev> getObcutljivost() {
        return obcutljivost;
    }

    public LocalTime getCasDelovanjaInzulina() {
        return casDelovanjaInzulina;
    }

    public Double getCiljnaGKSpodnjaMeja() {
        return ciljnaGKSpodnjaMeja;
    }

    public Double getCiljnaGKZgornjaMeja() {
        return ciljnaGKZgornjaMeja;
    }

    public Nastavitev getLastBazalniOdmerek(){
        if(bazalniOdmerki.size()>1){
            return bazalniOdmerki.get(bazalniOdmerki.size()-2);
        }
        return null;
    }

    public Nastavitev getLastRazmerjeOH(){
        if(razmerjaOH.size()>1){
            return razmerjaOH.get(razmerjaOH.size()-2);
        }
        return null;
    }

    public Nastavitev getLastObcutljivost(){
        if(obcutljivost.size()>1){
            return obcutljivost.get(obcutljivost.size()-2);
        }
        return null;
    }

    public Double getTrenutniAktivniInzulin(LocalDateTime localDateTime){
        Calendar datum;
        Double sumAktivnitEnotInzulina=0d;
        Double casDelovanjaVMinutah=(double)casDelovanjaInzulina.getHourOfDay()*60+casDelovanjaInzulina.getMinuteOfHour();
        Double razlikaVMinutah,procentiDelujecegaInzulina, aktivniInzulinOdVnosa;
        Vnos trenutniVnos;
        LocalDateTime localDateTimeZadnjiVnos;
        Interval interval;
        LocalTime razlika;
        int i=0;
        do{
            trenutniVnos=vnosi.get(i);
            datum = trenutniVnos.getDatum();
            localDateTimeZadnjiVnos = new LocalDateTime(datum.getTimeInMillis());
            interval = new Interval(localDateTimeZadnjiVnos.toDateTime(),localDateTime.toDateTime());
            razlika = new LocalTime(interval.toPeriod().getHours(),interval.toPeriod().getMinutes());
            if(Days.daysBetween(localDateTimeZadnjiVnos,localDateTime).getDays()<1) {
                if (razlika.isBefore(casDelovanjaInzulina)) {
                    razlikaVMinutah = (double) razlika.getHourOfDay() * 60 + razlika.getMinuteOfHour();
                    procentiDelujecegaInzulina = 100 - ((100d * razlikaVMinutah) / casDelovanjaVMinutah);
                    aktivniInzulinOdVnosa = (trenutniVnos.getEnotInzulina() * procentiDelujecegaInzulina) / 100;
                    sumAktivnitEnotInzulina += aktivniInzulinOdVnosa;
                }
            }
            i++;
        }while(razlika.isBefore(casDelovanjaInzulina));
        return sumAktivnitEnotInzulina;
    }

    public Double getNastavitevValue(int type, LocalDateTime localDateTime){
        //type-> 0=razmerja OH, 1=obcutljivost
        LocalTime localTime = new LocalTime(localDateTime.getHourOfDay(),localDateTime.getMinuteOfHour());
        Nastavitev tempNastavitev;
        ArrayList<Nastavitev> arrayList= new ArrayList<>();
        switch (type){
            case 0:
                arrayList=razmerjaOH;
                break;
            case 1:
                arrayList=obcutljivost;
                break;
        }
        for(int i=0;i<arrayList.size();i++){
            tempNastavitev=arrayList.get(i);
            if(tempNastavitev.getTo().getHourOfDay()==0 && tempNastavitev.getTo().getHourOfDay()==0){
                tempNastavitev.setTo(new LocalTime(23,59));
            }
            if(localTime.isAfter(tempNastavitev.getFrom()) && localTime.isBefore(tempNastavitev.getTo())){
                return Double.valueOf(tempNastavitev.getValue());
            }
        }
        return null;
    }

    public ArrayList<Vnos> getVnosiFiltered(LocalDate from, LocalDate to) {
        ArrayList<Vnos> filteredArrayList = new ArrayList<>();
        Calendar datum;
        Vnos trenutniVnos;
        for(int i=0;i<vnosi.size();i++){
            trenutniVnos=vnosi.get(i);
            datum = trenutniVnos.getDatum();
            LocalDate dateTrenutniVnos = new LocalDate(datum.getTimeInMillis());
            if((dateTrenutniVnos.isBefore(to) && dateTrenutniVnos.isAfter(from)) ||(dateTrenutniVnos.equals(from) || dateTrenutniVnos.equals(to))){
                filteredArrayList.add(trenutniVnos);
            }
        }
        return filteredArrayList;
    }
}
