package Service.XVSM;

import MarketEntities.ISRContainer;
import MarketEntities.XVSM.XvsmISRContainer;
import Model.IssueStockRequest;
import Service.ConnectionError;
import Service.IBroker;
import Util.XvsmUtil;
import org.mozartspaces.capi3.CoordinationData;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.capi3.Selector;
import org.mozartspaces.core.*;
import org.mozartspaces.notifications.Notification;
import org.mozartspaces.notifications.NotificationListener;
import org.mozartspaces.notifications.NotificationManager;
import org.mozartspaces.notifications.Operation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Felix on 11.04.2015.
 */
public class XvsmBroker extends XvsmService implements IBroker, NotificationListener {

    private ISRContainer isrContainer;

    public XvsmBroker(String uri) throws ConnectionError {
        super(uri);
        this.isrContainer = new XvsmISRContainer();
    }

    @Override
    public void startBroking() throws ConnectionError {
        String transactionId;

        try {
            transactionId = XvsmUtil.createTransaction();
            takeISRs(transactionId);

            XvsmUtil.commitTransaction(transactionId);

            isrContainer.subscribe(this,null);

        } catch (MzsCoreException e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void entryOperationFinished(Notification source, Operation operation, List<? extends Serializable> entries) {

        String transactionId;

        try {
            transactionId = XvsmUtil.createTransaction();
            takeISRs(transactionId);

            XvsmUtil.commitTransaction(transactionId);
        } catch (Exception  e) {
            System.out.println("FATAL: CONNECTION ERROR ON LISTENING.");
        }
    }

    private void takeISRs(String transactionId) throws ConnectionError {

//        try {
            List<IssueStockRequest> isrs = isrContainer.takeIssueStockRequests(transactionId);

            // get container for market values
            ContainerReference marketValuesContainer = XvsmUtil.getContainer(XvsmUtil.Container.MARKET_VALUES);

            for (final IssueStockRequest isr : isrs) {
//                // check if stock of company is already in market values container
//                try {
//                    ArrayList<Entry> stockMarketValue = xc.getCapi().read(marketValuesContainer, KeyCoordinator.newSelector(isr.getCompany().getId()), XvsmUtil.ACTION_TIMEOUT, XvsmUtil.getTransaction(transactionId));
//                } catch (MzsCoreException e) {
//                    e.printStackTrace();
//                }
//
//                if (stockMarketValue != null && stockMarketValue.size() > 0) {
//                    // market value for this company already existing, reject price of ISR and take the price from market value container
//                    Double marketValue = (Double) stockMarketValue.get(0); // there should only be one object associated to the company id as key
//                    isr.setPrice(marketValue);
//                } else {
//                    // stock is new, insert new field for it's market value in market value container
//                    Entry marketValueForStock = new Entry(isr.getPrice(), new CoordinationData() {
//                        @Override
//                        public String getName() {
//                            return isr.getCompany().getId();
//                        }
//                    });
//                    xc.getCapi().write(marketValuesContainer, XvsmUtil.ACTION_TIMEOUT, XvsmUtil.getTransaction(transactionId), marketValueForStock);
//                }

                //TODO add order to TRADEABLE_ORDERS container

                System.out.println(isr.toString());
            }

        }
//    catch (MzsCoreException e) {
//                throw new ConnectionError(e);
//        }
//    }
}
