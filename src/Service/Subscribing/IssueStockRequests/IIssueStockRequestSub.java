package Service.Subscribing.IssueStockRequests;

import Model.IssueStockRequest;

import java.util.List;

/**
 * Created by Felix on 16.04.2015.
 */
public interface IIssueStockRequestSub {

    void pushNewISRs(List<IssueStockRequest> newISRs);
}
