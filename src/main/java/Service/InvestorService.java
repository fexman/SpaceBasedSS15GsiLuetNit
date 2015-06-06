package Service;

import Factory.IFactory;
import Factory.RmiFactory;
import Factory.XvsmFactory;
import MarketEntities.*;
import MarketEntities.Subscribing.TradeOrders.ITradeOrderSub;
import Model.*;
import RMIServer.EntityProviders.Impl.FondsIndexProvider;
import Util.TransactionTimeout;

import java.util.List;

/**
 * Created by j0h1 on 24.04.2015.
 */
public class InvestorService extends Service implements ITradeOrderSub {

    private Investor investor;
    private DepotInvestor depotInvestor;
    private TradeOrderContainer tradeOrderContainer;
    private IssueRequestContainer irContainer;
    private FondsIndexContainer fiContainer;
    private StockPricesContainer spContainer;

    private static final long MULTI_MARKET_TRADEVOLUME_TIMEOUT = 2000l;

    public InvestorService(IFactory factory) {
        super(factory);
    }

    public InvestorService(IFactory factory, Investor investor) {
        super(factory);
        this.investor = investor;
        this.tradeOrderContainer = factory.newTradeOrdersContainer();
        this.irContainer = factory.newIssueRequestContainer();
        this.fiContainer = factory.newFondsIndexContainer();
        this.spContainer = factory.newStockPricesContainer();
        try {
            this.depotInvestor = factory.newDepotInvestor(investor,null);
        } catch (ConnectionErrorException e)  {
            throw new RuntimeException("COULD NOT CREATE DEPOT FOR INVESTOR SERVICE");
        }

    }

    @Override
    public void pushNewTradeOrders(TradeOrder tradeOrder) {
        String transactionId = "";

        try {
            transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);

            tradeOrderContainer.addOrUpdateOrder(tradeOrder, transactionId);

            factory.commitTransaction(transactionId);
            System.out.println("Committed: " + tradeOrder);
        } catch (ConnectionErrorException e) {
            try {
                factory.rollbackTransaction(transactionId);
                throw e;
            } catch (ConnectionErrorException ex) {
                System.out.println("Error on tradeOrders push");
                ex.printStackTrace();
            }
        }
    }

    public void addToBudget(double amountToBeAdded) throws ConnectionErrorException {
        String transactionId = "";

        try {
            transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);

            depotInvestor.addToBudget(amountToBeAdded, transactionId);

            factory.commitTransaction(transactionId);

            System.out.println("Added " + amountToBeAdded + " to " + investor.getId() + "'s  budget.");
        } catch (ConnectionErrorException e) {
            factory.rollbackTransaction(transactionId);
            throw e;
        }
    }

    public void issueFonds(int amount) throws ConnectionErrorException {
        if (investor.isFonds()) {
            String transactionId = "";

            try {
                transactionId = factory.createTransaction(TransactionTimeout.DEFAULT);

                IssueFondsRequest ifr = new IssueFondsRequest(investor,amount);

                //Write to investor-depot
                System.out.print("Writing new fonds to depot ... ");
                depotInvestor.addTradeObjects(ifr.toTradeObjects(), transactionId);
                System.out.println("done.");

                //Issue Stocks
                System.out.print("Writing IF-request to container ... ");
                irContainer.addIssueRequest(ifr, transactionId);
                System.out.println("done.");

                //Wait for and get Fonds Markets
                System.out.println("Wating "+MULTI_MARKET_TRADEVOLUME_TIMEOUT+" miliseconds for market to register requests");
                try {
                    Thread.sleep(MULTI_MARKET_TRADEVOLUME_TIMEOUT);
                } catch (InterruptedException e) {
                    //Nvm
                }
                List<AddressInfo> addresses = fiContainer.getMarkets(investor,transactionId);

                //Acumulate TradeVolume
                boolean multiMarket = false;
                int accumulatedTradeVolume = 0;
                System.out.println("Accumulating trade volume ...");
                for (AddressInfo addressInfo : addresses) { //Non-multi market
                    if (addressInfo.equals(factory.getAddressInfo())) {
                        accumulatedTradeVolume += amount;
                        System.out.println("\tAdded local: "+amount);
                    } else {
                        multiMarket = true; //Multi-market mode
                        IFactory remoteFactory;
                        if (addressInfo.getProtocol().equals(AddressInfo.Protocol.XVSM)) {
                            remoteFactory = new XvsmFactory(addressInfo.getAddress());
                        } else {
                            remoteFactory = new RmiFactory(addressInfo.getAddress());
                        }

                        MarketValue remoteMarketValue = remoteFactory.newStockPricesContainer().getMarketValue(investor.getId(),null);

                        if (remoteMarketValue != null) {
                            accumulatedTradeVolume += remoteMarketValue.getTradeVolume();
                            System.out.println("\tAdded remote ("+addressInfo.getAddress()+" - "+addressInfo.getProtocol()+"): "+amount);
                        }
                    }
                }

                //Set accumulated TradeVolume at every market
                System.out.println("Accumlated trade volume: "+accumulatedTradeVolume);
                System.out.println("Setting acummulated trade Volume ...");
                if (multiMarket) {
                    for (AddressInfo addressInfo : addresses) {
                        StockPricesContainer currentSpConainer;
                        String subTransactionId;
                        IFactory remoteFactory;
                        if (addressInfo.equals(factory.getAddressInfo())) {
                            remoteFactory = factory;
                            currentSpConainer = spContainer;
                            subTransactionId = transactionId;
                            System.out.println("\tSet local.");
                        } else {
                            if (addressInfo.getProtocol().equals(AddressInfo.Protocol.XVSM)) {
                                remoteFactory = new XvsmFactory(addressInfo.getAddress());
                            } else {
                                remoteFactory = new RmiFactory(addressInfo.getAddress());
                            }
                            currentSpConainer = remoteFactory.newStockPricesContainer();
                            subTransactionId = remoteFactory.createTransaction(TransactionTimeout.DEFAULT);
                            System.out.println("\tSet remote (" + addressInfo.getAddress() + " - " + addressInfo.getProtocol() + ").");
                        }

                        MarketValue currentMw = currentSpConainer.getMarketValue(investor.getId(),subTransactionId);
                        if (currentMw != null) {
                            currentMw.setTradeVolume(accumulatedTradeVolume);
                            currentSpConainer.addOrUpdateMarketValue(currentMw, subTransactionId);

                            if (subTransactionId != transactionId) {
                                remoteFactory.commitTransaction(subTransactionId);
                            }
                        } else {
                            if (subTransactionId != transactionId) {
                                remoteFactory.rollbackTransaction(subTransactionId);
                            }
                        }


                    }
                } else {
                    System.out.println("Dropped accumulated tradeVolume since this fondsmanager is not multimarket (yet).");
                }

                factory.commitTransaction(transactionId);
            } catch (ConnectionErrorException e) {
                factory.rollbackTransaction(transactionId);
                throw e;
            }
        } else {
            System.out.println("Investor is no fondmanager, cannot issue Fonds.");
        }
    }

}
