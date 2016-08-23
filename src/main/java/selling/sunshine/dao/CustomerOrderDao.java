package selling.sunshine.dao;

import common.sunshine.model.selling.order.CustomerOrder;
import common.sunshine.pagination.DataTableParam;
import common.sunshine.pagination.MobilePageParam;
import common.sunshine.utils.ResultData;

import java.util.Map;

/**
 * Created by sunshine on 6/14/16.
 */
public interface CustomerOrderDao {
    ResultData insertOrder(CustomerOrder order);

    ResultData updateOrder(CustomerOrder order);

    ResultData queryOrder(Map<String, Object> condition);

    ResultData queryOrderByPage(Map<String, Object> condition, MobilePageParam param);

    ResultData queryOrderByPage(Map<String, Object> condition, DataTableParam param);
}
