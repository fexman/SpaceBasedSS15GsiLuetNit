package Model;

import java.io.Serializable;

/**
 * Created by j0h1 on 28.04.2015.
 */
public abstract class StockOwner implements Serializable {

    protected static final long serialVersionUID = 3116234320068709532L;

    protected String id;

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

}
