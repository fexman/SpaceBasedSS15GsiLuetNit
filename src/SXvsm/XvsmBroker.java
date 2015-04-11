package SXvsm;

import SInterface.ConnectionError;
import SInterface.IBroker;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.notifications.NotificationManager;

/**
 * Created by Felix on 11.04.2015.
 */
public class XvsmBroker extends XvsmService implements IBroker {

    public XvsmBroker(String uri) throws ConnectionError {
        super(uri);
    }

    @Override
    public void startBroking() {
        // Ensure that the container "products" exists
        ContainerReference cref = XvsmUtil.getContainer(XvsmUtil.Container.ISSUED_STOCK_REQUESTS);

        // Create notification
        NotificationManager notifManager = new NotificationManager(core);
        Set<Operation> operations = new HashSet<Operation>();
        operations.add(Operation.WRITE);
        notifManager.createNotification(cref, this, operations, null, null);

        // Read all existing entries
        ArrayList<Selector> selectors = new ArrayList<Selector>();
        selectors.add(FifoCoordinator.newSelector(Selecting.COUNT_ALL));
        ArrayList<String> resultEntries = capi.read(cref, selectors, RequestTimeout.INFINITE, null);

        // output
        for (String entry : resultEntries) {
            System.out.println(entry);
            textArea.append(entry);
            textArea.append("\n");
        }

    } catch (MzsCoreException ex) {
        ex.printStackTrace();
    } catch (InterruptedException ex) {
        ex.printStackTrace();
    }
}
    }



// Callback method (NotificationListener)
@Override
public void entryOperationFinished(final Notification arg0, final Operation arg1,
final List<? extends Serializable> entries) {
        for (Serializable entry : entries) {
        String message = ((Entry) entry).getValue().toString();
        System.out.println(message);
        textArea.append(message);
        textArea.append("\n");
        }

        }
