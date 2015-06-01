package Model;

import java.io.Serializable;

/**
 * Created by Felix on 20.04.2015.
 */
public class MarketValue implements Serializable {

    private static final long serialVersionUID = 6475447917214042055L;

    private String id;
    private Double price;
    private Integer tradeVolume;
    private boolean priceChanged;

    public MarketValue(Company company, Double price, Integer tradeVolume) {
        this.id = company.getId();
        this.price = price;
        this.tradeVolume = tradeVolume;
        this.priceChanged = true;
    }

    public MarketValue(Investor investor, Double price, Integer tradeVolume) {
        if (!investor.isFonds()) {
            //TODO: Investor is not fonds?
        }
        this.id = investor.getId();
        this.price = price;
        this.tradeVolume = tradeVolume;
        this.priceChanged = true;
    }

    public String getId() {
        return id;
    }

    public void setCompany(Company comp) {
        this.id = comp.getId();
    }

    public void setInvestor(Investor inv) {
        if (!inv.isFonds()) {
            //TODO: Investor is not fonds?
        }
        this.id = inv.getId();
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.priceChanged = true;
        this.price = price;
    }

    public boolean isPriceChanged() {
        return priceChanged;
    }

    public void setPriceChanged(boolean priceChanged) {
        this.priceChanged = priceChanged;
    }

    public Integer getTradeVolume() {
        return tradeVolume;
    }

    public void setTradeVolume(Integer tradeVolume) {
        this.tradeVolume = tradeVolume;
    }

    public String toString() {
        return "MarketValue of "+id+" ("+tradeVolume+"): $"+price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MarketValue that = (MarketValue) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
