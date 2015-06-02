package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 02.06.2015.
 */
public class IssueFondsRequest extends IssueRequest {

    public IssueFondsRequest(Investor investor, Integer amount) {
        if (!investor.isFonds()) {
            //TODO: Investor is not fonds?
        }
        this.id = investor.getId();
        this.amount = amount;
    }

    public Investor getInvestor() {
        Investor inv = new Investor(id);
        inv.setFonds(true);
        return inv;
    }

    public List<Fond> toFonds() {
        List<Fond> fonds = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            fonds.add(new Fond(new Investor(id)));
        }
        return fonds;
    }

    public List<TradeObject> toTradeObjects() {
        List<TradeObject> tradeObjects = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            tradeObjects.add(new Fond(new Investor(id)));
        }
        return tradeObjects;
    }

    public String toString() {
        return id+"-Fonds: #"+amount;
    }



}
