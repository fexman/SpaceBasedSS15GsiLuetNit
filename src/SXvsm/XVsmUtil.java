package SXvsm;

import org.mozartspaces.capi3.Coordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.*;

import java.net.URI;
import java.util.ArrayList;

/**
 * Created by Felix on 05.04.2015.
 */

public class XvsmUtil {

    /**
     * Looks up container from space with given capi and uri, or creates new one if not existent.
     * @param containerName
     * @param space
     * @param capi
     * @return
     * @throws MzsCoreException Thrown if container could neither be looked up, nor created.
     */
    public static ContainerReference lookUpOrCreateContainer(String containerName, URI space, Capi capi) throws MzsCoreException {
        ContainerReference cref;
        try {
            cref = capi.lookupContainer(containerName, space, MzsConstants.RequestTimeout.DEFAULT, null);
        } catch (MzsCoreException e) {
            ArrayList<Coordinator> obligatoryCoords = new ArrayList<Coordinator>();
            obligatoryCoords.add(new FifoCoordinator());
            cref = capi.createContainer(containerName, space, MzsConstants.Container.UNBOUNDED, obligatoryCoords, null, null);
        }
        return cref;
    }
}
