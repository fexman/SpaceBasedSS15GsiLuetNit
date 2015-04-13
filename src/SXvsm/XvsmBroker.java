package SXvsm;

import Model.IssueStockRequest;
import SInterface.ConnectionError;
import SInterface.IBroker;
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

    private ContainerReference issuedStocksContainer;

    public XvsmBroker(String uri) throws ConnectionError {
        super(uri);
        this.issuedStocksContainer = XvsmUtil.getContainer(XvsmUtil.Container.ISSUED_STOCK_REQUESTS);
    }

    @Override
    public void startBroking() throws ConnectionError {
        // Create notification
        NotificationManager notificationManager = new NotificationManager(xc.getCore());
        Set<Operation> operations = new HashSet<>();
        operations.add(Operation.WRITE);

        try {
            notificationManager.createNotification(issuedStocksContainer, this, operations, null, null);
        } catch (Exception e) {
            throw new ConnectionError(e);
        }
    }

    @Override
    public void entryOperationFinished(Notification source, Operation operation, List<? extends Serializable> entries) {
        try {
            takeISRs();
        } catch (ConnectionError e) {
            System.out.println("FATAL: CONNECTION ERROR ON LISTENING.");
        }
    }

    private void takeISRs() throws ConnectionError {

        TransactionReference tx = null;

        try {
            //Get company-depot container
            tx = xc.getCapi().createTransaction(XvsmUtil.ACTION_TIMEOUT, xc.getSpace());

            //Take all existing ISRs
            ArrayList<Selector> selectors = new ArrayList<>();
            selectors.add(FifoCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL));
            ArrayList<IssueStockRequest> resultEntries = xc.getCapi().take(issuedStocksContainer, selectors, XvsmUtil.ACTION_TIMEOUT, tx);

            // get container for market values
            ContainerReference marketValuesContainer = XvsmUtil.getContainer(XvsmUtil.Container.MARKET_VALUES);

            for (IssueStockRequest isr : resultEntries) {
                // check if stock of company is already in market values container
                KeyCoordinator.KeySelector marketValueSelector = KeyCoordinator.newSelector(isr.getCompany().getId());
                ArrayList<Serializable> stockMarketValue = xc.getCapi().read(marketValuesContainer, marketValueSelector, XvsmUtil.ACTION_TIMEOUT, tx);

                if (stockMarketValue != null && stockMarketValue.size() > 0) {
                    // market value for this company already existing, reject price of ISR and take the price from market value container
                    Double marketValue = (Double) stockMarketValue.get(0); // there should only be one object associated to the company id as key
                    isr.setPrice(marketValue);
                } else {
                    // stock is new, insert new field for it's market value in market value container
                    Entry marketValueForStock = new Entry(isr.getPrice(), new CoordinationData() {
                        @Override
                        public String getName() {
                            return isr.getCompany().getId();
                        }
                    });
                    xc.getCapi().write(marketValuesContainer, XvsmUtil.ACTION_TIMEOUT, tx, marketValueForStock);
                }

                // TODO price of ISR is now adjusted to the market value (not tested); write ISR to another container?
                // TODO or set a flag in every stock that says "tradeable" and leave it in the company depot?
                // TODO imo the idea of having a seperate container for that shit wasn't all that stupid after all
                // TODO (so the broker has to only observe one single container instead of every companies depot) - we rejected that idea IIRC

                System.out.println(isr.toString());
            }
            xc.getCapi().commitTransaction(tx);

        } catch (MzsCoreException e) {
            try {
                xc.getCapi().rollbackTransaction(tx);
                throw new ConnectionError(e);
            } catch (MzsCoreException ex) {
                throw new ConnectionError(ex);
            }
        }
    }
}
