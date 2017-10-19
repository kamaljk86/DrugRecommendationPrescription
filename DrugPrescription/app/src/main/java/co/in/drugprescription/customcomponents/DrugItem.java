package co.in.drugprescription.customcomponents;

public class DrugItem extends java.lang.Object {
    private String ID;
    private String Name;

    public DrugItem() {}

    public DrugItem(String id, String Name) {
        this.ID = id;
        this.Name = Name;
    }

    public String getId() { return ID; }
    public String getName() { return Name; }

    @Override
    public String toString()
    {
        return Name;
    }
}
