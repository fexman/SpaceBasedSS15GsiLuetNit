package Factory;

import Service.Broker;

/**
 * Created by Felix on 16.04.2015.
 */
public abstract class AbstractSubscriber {

    protected Broker broker;

    public AbstractSubscriber(Broker broker) {
        this.broker = broker;
    }
}
