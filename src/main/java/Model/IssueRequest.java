package Model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Felix on 02.06.2015.
 */
public abstract class IssueRequest implements Serializable {

    protected String id;
    protected Integer amount;

    public String getId() {
        return id;
    }

    public Integer getAmount() {
        return amount;
    }

    public abstract List<TradeObject> toTradeObjects();
}
