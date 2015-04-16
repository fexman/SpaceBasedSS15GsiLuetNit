package Model;

/**
 * Created by Felix on 16.04.2015.
 */
public class CompanyTradeOrder extends TradeOrder {

    public CompanyTradeOrder(Company investor, Company companyOfStocksToBuyOrSell, Integer totalAmount, Double priceLimit) {
        super(investor.getId(), companyOfStocksToBuyOrSell, TradeOrder.Type.SELL_ORDER, totalAmount, priceLimit);
    }

    public Company getInvestor() {
        return new Company(super.getInvestorId());
    }
}
