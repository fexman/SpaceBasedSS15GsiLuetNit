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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockOwner that = (StockOwner) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
