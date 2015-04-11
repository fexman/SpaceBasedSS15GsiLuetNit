package Model;

import java.io.Serializable;

/**
 * Created by Felix on 09.04.2015.
 */
public class Investor implements Serializable {

    private static final long serialVersionUID = -9218736705752854815L;
    private String id;
    private Double budget;

    public Investor(String id) {

    }

    public String getId() {
        return id;
    }

    public Double getBudget() {
        return budget;
    }

}
