package Data;

import java.util.Calendar;

public class Obrok extends Vnos {
    private Tip tip;
    private String opis;

    Obrok(double sladkor, double OH, double enotInzulina, Calendar datum, Tip tip, String opis) {
        super(sladkor, OH, enotInzulina, datum);
        this.tip = tip;
        this.opis = opis;
    }

    public Obrok(Calendar datum){
        super(datum);

    }
    public Tip getTip() {
        return tip;
    }

    public void setTip(Integer position) {
        switch (position){
            case 0:
                this.tip = Tip.Eat1;
                break;
            case 1:
                this.tip = Tip.Eat2;
                break;
            case 2:
                this.tip = Tip.Eat3;
                break;
            default:
                this.tip = Tip.DRUGO;
                break;
        }
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }
}
