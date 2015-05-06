package MarketEntities.Subscribing;

/**
 * Created by Felix on 23.04.2015.
 */
public abstract class ASubManager<T extends Subscription>  {

    protected T subscription;

    public ASubManager(T subscription) {
        this.subscription = subscription;
    }
}
