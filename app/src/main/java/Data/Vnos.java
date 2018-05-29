package Data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Vnos
{
    private Double sladkor;
    private Double OH;
    private Double enotInzulina;
    private Calendar datum;

    public Vnos(double sladkor, double OH, double enotInzulina, Calendar datum) {
        this.sladkor = sladkor;
        this.OH = OH;
        this.enotInzulina = enotInzulina;
        this.datum = datum;
    }

    public Vnos(double sladkor, double OH) {
        this.sladkor = sladkor;
        this.OH = OH;
    }
    public Vnos(Calendar datum){
        this.datum = datum;
    }
    public Double getSladkor() {
        return sladkor;
    }

    public void setSladkor(Double sladkor) {
        this.sladkor = sladkor;
    }

    public Double getOH() {
        return OH;
    }

    public void setOH(Double OH) {
        this.OH = OH;
    }

    public Double getEnotInzulina() {
        return enotInzulina;
    }

    public void setEnotInzulina(Double enotInzulina) {
        this.enotInzulina = enotInzulina;
    }

    public Calendar getDatum() {
        return datum;
    }

    public String getDatumString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());
        return simpleDateFormat.format(datum.getTime());
    }
}
