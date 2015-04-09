package SXvsm;

import org.mozartspaces.capi3.*;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Felix on 05.04.2015.
 */

public class XvsmUtil {

    public static int ACTION_TIMEOUT = 2500;


    private static HashMap<Container, ContainerReference> containers = new HashMap<>();


    private static XvsmConnection xc;


    /**
     * Looks up container from space with given capi and uri, or creates new one if not existent.
     * @param containerName
     * @param space
     * @param capi
     * @return
     * @throws MzsCoreException Thrown if container could neither be looked up, nor created.
     */
    private static ContainerReference lookUpOrCreateContainer(String containerName, URI space, Capi capi, List<CoordinatorType> types) throws MzsCoreException {
        ContainerReference cref;
        try {
            cref = capi.lookupContainer(containerName, space, MzsConstants.RequestTimeout.DEFAULT, null);
        } catch (MzsCoreException e) {
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
            for (CoordinatorType ct: types) {
                obligatoryCoords.add(CoordinatorType.getCoordinator(ct));
            }
            cref = capi.createContainer(containerName, space, MzsConstants.Container.UNBOUNDED, obligatoryCoords, null, null);
        }
        return cref;
    }

    //Public and "still there" for SimpleSpaceWriter/SimpleSpaceViewer
    public static ContainerReference lookUpOrCreateContainer(String containerName, URI space, Capi capi) throws MzsCoreException {
        return lookUpOrCreateContainer(containerName, space, capi,  new ArrayList<CoordinatorType>() {{ add(CoordinatorType.FIFO_COORDINATOR);}});
    }



    /**
     * Connects to a XVSM-Space and returns XvsmConnection.
     * @param spaceUri Uri of the XVSM-Space
     * @return XvsmConnection containing capi and space Uri
     */
    public static XvsmConnection initConnection(String spaceUri) throws MzsCoreException {

        if (xc != null) {
            return getXvsmConnection();
        }

        xc = new XvsmConnection(spaceUri);
        System.out.println("XvsmUtil: Connection initialized - XVSM up and running.");

        containers.put(Container.COMPANY_DEPOT, lookUpOrCreateContainer(Container.COMPANY_DEPOT.toString(), xc.getSpace(), xc.getCapi(), new ArrayList<CoordinatorType>() {{add(CoordinatorType.LABEL_COORDINATOR);}}));
        containers.put(Container.ISSUED_STOCK_REQUESTS, lookUpOrCreateContainer(Container.ISSUED_STOCK_REQUESTS.toString(), xc.getSpace(), xc.getCapi(),  new ArrayList<CoordinatorType>() {{ add(CoordinatorType.FIFO_COORDINATOR);}}));

        return xc;
    }

    public static ContainerReference getContainer(Container cont) {
        return containers.get(cont);
    }

    /**
     * Returns the current connection to a XVSM-Space, may return null if no connection was estalbished yet.
     * @return
     */
    public static XvsmConnection getXvsmConnection () {
        return xc;
    }

    public static class XvsmConnection {

        private URI space;
        private Capi capi;

        public XvsmConnection(String spaceUri) {
            space = URI.create(spaceUri);
            MzsCore core = DefaultMzsCore.newInstanceWithoutSpace();
            capi = new Capi(core);
        }

        public URI getSpace() {
            return space;
        }

        public Capi getCapi() {
            return capi;
        }
    }

    public enum Container {
        COMPANY_DEPOT("companyDepots"),
        ISSUED_STOCK_REQUESTS("issuedStockRequests");

        private final String text;

        /**
         * @param text
         */
        private Container(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    public enum CoordinatorType {

        ANY_COORDINATOR,
        RANDOM_COORDINATOR,
        FIFO_COORDINATOR,
        LIFO_COORDINATOR,
        KEY_COORDINATOR,
        LABEL_COORDINATOR;

        public static Coordinator getCoordinator(CoordinatorType type) {
            switch (type) {
                case ANY_COORDINATOR:
                    return new AnyCoordinator();
                case RANDOM_COORDINATOR:
                    return new RandomCoordinator();
                case FIFO_COORDINATOR:
                    return new FifoCoordinator();
                case LIFO_COORDINATOR:
                    return new LifoCoordinator();
                case KEY_COORDINATOR:
                    return new KeyCoordinator();
                case LABEL_COORDINATOR:
                    return new LabelCoordinator();
                default:
                    return new AnyCoordinator();
            }
        }

    }
}
