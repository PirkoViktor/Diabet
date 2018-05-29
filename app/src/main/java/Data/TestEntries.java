package Data;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Calendar;

public class TestEntries {
    public ArrayList<Vnos> testEntries() {
        ArrayList<Vnos> arrayList = new ArrayList<>();

        Vnos vnos= new Vnos(5.5,0,0, Calendar.getInstance());
        arrayList.add(vnos);
        Aktivnost aktivnost = new Aktivnost(10.0,0,0,Calendar.getInstance());
        arrayList.add(aktivnost);
        Obrok obrok = new Obrok(19.9,0,0,Calendar.getInstance(), Tip.DRUGO,"opis");
        arrayList.add(obrok);

        vnos= new Vnos(5.5,0,0,Calendar.getInstance());
        arrayList.add(vnos);
        aktivnost = new Aktivnost(10.0,0,0,Calendar.getInstance());
        arrayList.add(aktivnost);
        obrok = new Obrok(19.9,0,0,Calendar.getInstance(),Tip.DRUGO,"opis");
        arrayList.add(obrok);

        vnos= new Vnos(5.5,0,0,Calendar.getInstance());
        arrayList.add(vnos);
        aktivnost = new Aktivnost(10.0,0,0,Calendar.getInstance());
        arrayList.add(aktivnost);
        obrok = new Obrok(19.9,0,0,Calendar.getInstance(),Tip.DRUGO,"opis");
        arrayList.add(obrok);

        vnos= new Vnos(5.5,0,0,Calendar.getInstance());
        arrayList.add(vnos);
        aktivnost = new Aktivnost(10.0,0,0,Calendar.getInstance());
        arrayList.add(aktivnost);
        obrok = new Obrok(19.9,0,0,Calendar.getInstance(),Tip.DRUGO,"opis");
        arrayList.add(obrok);

        vnos= new Vnos(5.5,0,0,Calendar.getInstance());
        arrayList.add(vnos);
        aktivnost = new Aktivnost(10.0,0,0,Calendar.getInstance());
        arrayList.add(aktivnost);
        obrok = new Obrok(19.9,0,0,Calendar.getInstance(),Tip.DRUGO,"opis");
        arrayList.add(obrok);

        vnos= new Vnos(5.5,0,0,Calendar.getInstance());
        arrayList.add(vnos);
        aktivnost = new Aktivnost(10.0,0,0,Calendar.getInstance());
        arrayList.add(aktivnost);
        obrok = new Obrok(19.9,0,0,Calendar.getInstance(),Tip.DRUGO,"opis");
        arrayList.add(obrok);

        return arrayList;
    }

    public ArrayList<Nastavitev> testBazal() {
        ArrayList<Nastavitev> arrayList = new ArrayList<>();
        arrayList.add(new Nastavitev(new LocalTime(0,0),new LocalTime(2,0),0.9));
        arrayList.add(new Nastavitev(new LocalTime(2,0),new LocalTime(7,0),1.0));
        arrayList.add(new Nastavitev(new LocalTime(7,0),new LocalTime(14,0),1.25));
        arrayList.add(new Nastavitev(new LocalTime(14,0),new LocalTime(20,0),1.0));
        arrayList.add(new Nastavitev(new LocalTime(20,0),new LocalTime(0,0),0.8));
        return arrayList;
    }

    public ArrayList<Nastavitev> testOHrazmerja() {
        ArrayList<Nastavitev> arrayList = new ArrayList<>();
        arrayList.add(new Nastavitev(new LocalTime(0,0),new LocalTime(7,0),11.0));
        arrayList.add(new Nastavitev(new LocalTime(7,0),new LocalTime(21,0),10.0));
        arrayList.add(new Nastavitev(new LocalTime(21,0),new LocalTime(0,0),11.0));
        return arrayList;
    }

    public ArrayList<Nastavitev> testObcutljivost() {
        ArrayList<Nastavitev> arrayList = new ArrayList<>();
        arrayList.add(new Nastavitev(new LocalTime(0,0),new LocalTime(7,0),3.0));
        arrayList.add(new Nastavitev(new LocalTime(7,0),new LocalTime(21,0),2.5));
        arrayList.add(new Nastavitev(new LocalTime(21,0),new LocalTime(0,0),3.0));
        return arrayList;
    }
}
