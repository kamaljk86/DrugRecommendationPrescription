package co.in.drugprescription.sync;

import java.io.Serializable;

public class DrugEntity implements Serializable {

    private String id;
    private String drugName;
    private String manufacturedBy;
    private String usedFor;
    private String rate;
    private String description;

    public String getId(){ return id; }
    public void setId(String id){ this.id = id; }

    public String getDrugName(){ return drugName; }
    public void setDrugName(String drugName){ this.drugName = drugName; }

    public String getManufacturedBy(){ return manufacturedBy; }
    public void setManufacturedBy(String manufacturedBy){ this.manufacturedBy = manufacturedBy; }

    public String getUsedFor(){ return usedFor; }
    public void setUsedFor(String usedFor){ this.usedFor = usedFor; }

    public String getRate(){ return rate; }
    public void setRate(String rate){ this.rate = rate; }

    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }
}
