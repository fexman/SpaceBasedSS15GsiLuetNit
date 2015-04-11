package Model;

import java.io.Serializable;

/**
 * Created by Felix on 06.04.2015.
 */
public class Stock implements Serializable {

    private static final long serialVersionUID = 7338860624387847800L;
    private Company company;

    public Stock(Company company) {
        this.company = company;
    }

    public Company getCompany() {
        return company;
    }

    public String toString() {
        return company.getId()+"-Stock";
    }
}
