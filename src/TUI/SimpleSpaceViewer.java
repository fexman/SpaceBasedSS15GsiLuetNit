package TUI;

import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.io.Serializable;
import java.net.URI;
import java.util.*;

import static SXvsm.XvsmUtil.lookUpOrCreateContainer;

/**
 * Created by Felix on 05.04.2015.
 */
public class SimpleSpaceViewer implements NotificationListener {

    public static final URI SPACE = URI.create("xvsm://localhost:12345");
    public static Capi capi;

    public SimpleSpaceViewer() {

        //Holt sich die Sync-Api Instanz
        MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
        capi = new Capi(core);

        System.out.println("Welcome to the text-based space viewer!");
        System.out.println("\tUsage: <container>");
        Scanner scan = new Scanner(System.in);

        //Loop die input einliest
        while(scan.hasNextLine()) {

            String container = scan.nextLine();

            if (container.split(" ").length == 1) {

                ContainerReference cref = null;

                try {
                    cref = lookUpOrCreateContainer(container, SPACE, capi);
                } catch (MzsCoreException e) {
                    System.out.println("Could not look up or create container: " + e.getMessage());
                }

                //Notification für per Input-bestimmten Container registrieren
                NotificationManager notifManager = new NotificationManager(core);
                Set<Operation> operations = new HashSet<Operation>();
                operations.add(Operation.WRITE);
                try {
                    notifManager.createNotification(cref, this, operations, null, null);
                } catch (MzsCoreException e) {
                    System.out.println("Oops, that should not happen. Unexpected error: "+e.getMessage());
                } catch (InterruptedException e) {
                    System.out.println("Oops, that should not happen. Unexpected error: " + e.getMessage());
                }

                System.out.println("Now observing "+container+".");
                //Alles ausgeben was derzeit in container it
                ArrayList<Selector> selectors = new ArrayList<Selector>();
                selectors.add(FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL));
                ArrayList<String> resultEntries = null;
                try {
                    resultEntries = capi.read(cref, selectors, MzsConstants.RequestTimeout.INFINITE, null);
                } catch (MzsCoreException e) {
                    e.printStackTrace();
                }
                for (String entry : resultEntries) {
                    System.out.println(container+": "+entry);
                }

            } else {
                //Invalid Input
                System.out.println("Invalid input!");
                System.out.println("\tUsage: <container> <entry>");
            }
        }
    }

    public static void main (String[] args) {
        new SimpleSpaceViewer();
    }

    /**
     * Executed on write Operation
     * @param source
     * @param operation
     * @param entries
     */
    @Override
    public void entryOperationFinished(Notification source, Operation operation, List<? extends Serializable> entries) {
        for (Serializable entry : entries) {
            System.out.println(source.getObservedContainer().getId()+": "+((Entry)entry).getValue());
        }
    }
}
