package SInterface;

import Model.IssueStockRequest;
import Model.Stock;

/**
 * Created by Felix on 06.04.2015.
 */
public interface ICompany {

    public void issueStocks(IssueStockRequest isr) throws ConnectionError;

}
