package sample.models;

public class TulovKar extends BaseModel {
    public TulovKar() {
        tablename="klient_balans";
    }
    private int no;
    private double summa;
    private String valyuta;
    private String tulov;

    public String getTulov() {
        return tulov;
    }

    public void setTulov(String tulov) {
        this.tulov = tulov;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public double getSumma() {
        return summa;
    }

    public void setSumma(double summa) {
        this.summa = summa;
    }

    public String getValyuta() {
        return valyuta;
    }

    public void setValyuta(String valyuta) {
        this.valyuta = valyuta;
    }
}
