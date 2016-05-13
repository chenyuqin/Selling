package selling.sunshine.service;

import selling.sunshine.model.ShipConfig;
import selling.sunshine.utils.ResultData;

import java.util.Map;

/**
 * Created by sunshine on 5/13/16.
 */
public interface ShipmentService {
    ResultData createShipmentConfig(ShipConfig config);

    ResultData fetchShipmentConfig(Map<String, Object> condition);
}
