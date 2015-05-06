package Model;

import org.mozartspaces.capi3.Queryable;

import java.io.Serializable;

/**
 * Created by j0h1 on 29.04.2015.
 */
@Queryable(autoindex = true)
public class StockStats implements Serializable {

    protected static final long serialVersionUID = 3119234320068709432L;

    private String stockName;
    private int amount;
    private Double marketValue;
    private Double totalValue;

    public StockStats() {
    }

    public StockStats(String stockName, int amount, Double marketValue, Double totalValue) {
        this.stockName = stockName;
        this.amount = amount;
        this.marketValue = marketValue;
        this.totalValue = totalValue;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(Double marketValue) {
        this.marketValue = marketValue;
    }

    public Double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }
}
