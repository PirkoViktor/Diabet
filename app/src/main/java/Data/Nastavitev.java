package Data;


import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Nastavitev {
    private LocalTime from;
    private LocalTime to;
    private Double value;

    public Nastavitev(LocalTime from, LocalTime to, Double value) {
        this.from = from;
        this.to = to;
        this.value = value;
    }

    public Nastavitev() {
        this.from=null;
        this.to=null;
        this.value=null;
    }

    public LocalTime getFrom() {
        return from;
    }

    public void setFrom(LocalTime from) {
        this.from = from;
    }

    public LocalTime getTo() {
        return to;
    }

    public void setTo(LocalTime to) {
        this.to = to;
    }

    public String getValue() {
        if(this.value!=null){
            return String.valueOf(this.value);
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("HH:mm");
        if(from!=null && to!=null && value!=null) {
            return from.toString(dateFormat) + "," +
                    to.toString(dateFormat) + "," +
                    String.valueOf(value);
        }
        return null;
    }
}
