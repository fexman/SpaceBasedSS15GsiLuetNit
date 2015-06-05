package MarketEntities.XVSM;

import MarketEntities.FondsIndexContainer;
import Model.AddressInfo;
import Model.Investor;
import Service.ConnectionErrorException;
import Util.Container;
import Util.XvsmUtil;
import org.mozartspaces.capi3.CoordinationData;
import org.mozartspaces.capi3.LabelCoordinator;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 05.06.2015.
 */
public class XvsmFondsIndexContainer extends FondsIndexContainer {

    private ContainerReference fondsIndexContainer;
    private XvsmUtil.XvsmConnection xc;
    private XvsmUtil util;

    public XvsmFondsIndexContainer(XvsmUtil util) {
        this.util = util;
        fondsIndexContainer = util.getContainer(Container.FONDS_INDEX_CONTAINER);
        xc = util.getXvsmConnection();
    }

    @Override
    public List<AddressInfo> getMarkets(Investor investor, String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

        Selector selector = LabelCoordinator.newSelector(investor.getId(), Selector.COUNT_MAX);
        List<AddressInfo> result;

        try {
            result = xc.getCapi().read(fondsIndexContainer, selector, XvsmUtil.ACTION_TIMEOUT, tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }

        if (result.size() > 0) {
            return result;
        }
        return null;
    }

    @Override
    public void registerMarkets(Investor investor, List<AddressInfo> markets, String transactionId) throws ConnectionErrorException {
        TransactionReference tx = util.getTransaction(transactionId);

        //Remove old entries, if any
        Selector selector = LabelCoordinator.newSelector(investor.getId(), Selector.COUNT_MAX);
        try {
            xc.getCapi().delete(fondsIndexContainer,selector,XvsmUtil.ACTION_TIMEOUT,tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }

        //Add new entries
        CoordinationData coordinationData = LabelCoordinator.newCoordinationData(investor.getId());
        List<Entry> entries = new ArrayList<>();
        for (AddressInfo addressInfo: markets) {
            entries.add(new Entry(addressInfo,coordinationData));
        }

        try {
            xc.getCapi().write(entries,fondsIndexContainer,XvsmUtil.ACTION_TIMEOUT,tx);
        } catch (MzsCoreException e) {
            throw new ConnectionErrorException(e);
        }

    }
}
