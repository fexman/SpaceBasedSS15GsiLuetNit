package MarketEntities;

import MarketEntities.XVSM.ISubscribeable;
import Model.HistoryEntry;
import Service.ConnectionErrorException;

import java.util.List;

/**
 * Created by j0h1 on 27.04.2015.
 */
public abstract class TransactionHistoryContainer implements ISubscribeable {

    public abstract void addHistoryEntry(HistoryEntry historyEntry, String transactionId) throws ConnectionErrorException;

    public abstract List<HistoryEntry> getTransactionHistory(String transactionId) throws ConnectionErrorException;

}