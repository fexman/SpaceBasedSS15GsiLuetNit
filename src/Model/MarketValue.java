package Model;

import java.io.Serializable;

/**
 * Created by Felix on 20.04.2015.
 */
public class MarketValue implements Serializable {

    private static final long serialVersionUID = 6475447917214042055L;

    private String companyId;
    private Double price;

    public MarketValue(Company company, Double price) {
        this.companyId = company.getId();
        this.price = price;
    }

    public Company getCompany() {
        return new Company(companyId);
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompany(Company comp) {
        this.companyId = comp.getId();
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String toString() {
        return "MarketValue of "+companyId+": "+price+"$";
    }

}
