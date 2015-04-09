package Model;

import java.io.Serializable;

/**
 * Created by Felix on 06.04.2015.
 */
public class Stock implements Serializable {

    private String companyId;

    public Stock(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String toString() {
        return companyId+"-Stock";
    }
}
