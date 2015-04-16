package Factory;

import Service.Broker;
import Service.ConnectionError;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.Operation;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Felix on 16.04.2015.
 */
public class XvsmSubscriber extends AbstractSubscriber implements NotificationListener {

    public XvsmSubscriber(Broker broker) {
        super(broker);
    }

    @Override
    public void entryOperationFinished(Notification source, Operation operation, List<? extends Serializable> entries) {
        try {
            broker.takeISRs();
        } catch (ConnectionError connectionError) {
            System.out.println("FATAL ERROR: TAKEISR THREW CONNECTION ERROR IN SUBSCRIPTION!!!\n"+ connectionError.getMessage());
        }
    }
}
