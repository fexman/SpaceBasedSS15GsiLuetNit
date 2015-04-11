package SInterface;

import Model.IssueStockRequest;

/**
 * Created by Felix on 06.04.2015.
 */
public interface ICompany extends Service {

    public void issueStocks(IssueStockRequest isr) throws ConnectionError;

}
