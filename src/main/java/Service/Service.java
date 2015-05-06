package Service;

import Factory.IFactory;

/**
 * Created by Felix on 09.04.2015.
 */
public abstract class Service {

    protected IFactory factory;

    public Service(IFactory factory) {
        this.factory = factory;
    }
}
