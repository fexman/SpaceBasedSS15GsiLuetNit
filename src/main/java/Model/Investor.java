package Model;

import java.io.Serializable;

/**
 * Created by Felix on 09.04.2015.
 */
public class Investor extends StockOwner {

    private boolean fonds;

    public Investor() {
        this.fonds = false;
    }

    public Investor(String id) {
        this.id = id;
    }

    public boolean isFonds() {
        return fonds;
    }

    public void setFonds(boolean fonds) {
        this.fonds = fonds;
    }


}
