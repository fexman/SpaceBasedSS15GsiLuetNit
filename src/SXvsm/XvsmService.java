package SXvsm;

import SInterface.ConnectionError;
import SInterface.Service;
import org.mozartspaces.core.MzsCoreException;

/**
 * Created by Felix on 09.04.2015.
 */
public abstract class XvsmService implements Service {

    protected XvsmUtil.XvsmConnection xc;

    public XvsmService(String uri) throws ConnectionError {
        try {
            this.xc = XvsmUtil.initConnection(uri);
        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void shutdown() throws ConnectionError {
        //TODO: Close capi, is it possible/necessary?
    }
}
