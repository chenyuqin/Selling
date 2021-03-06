package selling.sunshine.service;

import java.util.Map;

import selling.sunshine.model.BackOperationLog;
import common.sunshine.pagination.MobilePageParam;
import common.sunshine.utils.ResultData;

public interface LogService {
	ResultData fetchBackOperationLog(Map<String, Object> condition);

	ResultData fetchBackOperationLog(Map<String, Object> condition, MobilePageParam param);

	ResultData createbackOperationLog(BackOperationLog backOperationLog);
}
