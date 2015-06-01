package Model;

import org.mozartspaces.capi3.Queryable;

import java.io.Serializable;

/**
 * Created by Felix on 01.06.2015.
 */
@Queryable(autoindex = true)
public class Fond implements Serializable, TradeObject {

    private static final long serialVersionUID = 1284085595012341536L;

    private String investorId;

    public Fond(Investor investor) {
        if (!investor.isFonds()) {
            //TODO: Fondinvestor check
        }
        this.investorId = investor.getId();
    }

    public Investor getInvestor() {
        Investor inv = new Investor(investorId);
        inv.setFonds(true);
        return inv;
    }

    @Override
    public String getId() {
        return investorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Fond fond = (Fond) o;

        return investorId.equals(fond.investorId);

    }

    @Override
    public int hashCode() {
        return investorId.hashCode();
    }
}
