package Model;

/**
 * Created by Felix on 16.04.2015.
 */
public class InvestorTradeOrder extends TradeOrder {

    public InvestorTradeOrder(Investor investor, Company companyOfStocksToBuyOrSell, Type type, Integer totalAmount, Double priceLimit) {
        super(investor.getId(), companyOfStocksToBuyOrSell, type, totalAmount, priceLimit);
    }

    public Investor getInvestor() {
        return new Investor(super.getInvestorId());
    }
}
