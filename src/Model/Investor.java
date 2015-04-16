package Model;

import java.io.Serializable;

/**
 * Created by Felix on 09.04.2015.
 */
public class Investor implements Serializable {

    private static final long serialVersionUID = -9218736705752854815L;
    private String id;

    public Investor(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


}
