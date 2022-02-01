package sample.models;

public class Korzinka {
    private int id;
    private int mah_id;

    public int getMah_id() {
        return mah_id;
    }

    public void setMah_id(int mah_id) {
        this.mah_id = mah_id;
    }

    private int nomer;
    private String name;
    private String turi;
    private double narxi;
    private double soni;
    private String sana;
    private String umumiySumma;
    private double foyda;

    public String getUmumiySumma() {
        return umumiySumma;
    }

    public void setUmumiySumma(String umumiySumma) {
        this.umumiySumma = umumiySumma;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNomer() {
        return nomer;
    }

    public void setNomer(int nomer) {
        this.nomer = nomer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTuri() {
        return turi;
    }

    public void setTuri(String turi) {
        this.turi = turi;
    }

    public double getNarxi() {
        return narxi;
    }

    public void setNarxi(double narxi) {
        this.narxi = narxi;
    }

    public double getSoni() {
        return soni;
    }

    public void setSoni(double soni) {
        this.soni = soni;
    }

    public String getSana() {
        return sana;
    }

    public void setSana(String sana) {
        this.sana = sana;
    }



    public double getFoyda() {
        return foyda;
    }

    public void setFoyda(double foyda) {
        this.foyda = foyda;
    }
}
