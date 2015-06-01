package Model;

import org.mozartspaces.capi3.Queryable;

import java.io.Serializable;

/**
 * Created by Felix on 06.04.2015.
 */
@Queryable(autoindex = true)
public class Stock implements Serializable, TradeObject {

    private static final long serialVersionUID = 7338860624387847800L;
    private String companyId;

    public Stock(Company company) {
        this.companyId = company.getId();
    }

    public Company getCompany() {
        return new Company(companyId);
    }

    public String toString() {
        return companyId+"-Stock";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stock stock = (Stock) o;

        return !(companyId != null ? !companyId.equals(stock.companyId) : stock.companyId != null);

    }

    @Override
    public int hashCode() {
        return companyId != null ? companyId.hashCode() : 0;
    }

    @Override
    public String getId() {
        return companyId;
    }
}
