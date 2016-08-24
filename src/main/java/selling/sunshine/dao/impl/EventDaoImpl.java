package selling.sunshine.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.sunshine.dao.BaseDao;
import common.sunshine.model.selling.event.GiftEvent;
import common.sunshine.pagination.DataTablePage;
import common.sunshine.pagination.DataTableParam;
import common.sunshine.utils.IDGenerator;
import common.sunshine.utils.ResponseCode;
import common.sunshine.utils.ResultData;
import selling.sunshine.dao.EventDao;

public class EventDaoImpl extends BaseDao implements EventDao {

	private Logger logger = LoggerFactory.getLogger(EventDaoImpl.class);

	private Object lock = new Object();

	@Override
	public ResultData insertGiftEvent(GiftEvent event) {
		ResultData result = new ResultData();
		synchronized (lock) {
			try {
				event.setEventId(IDGenerator.generate("EVE"));
				sqlSession.insert("selling.event.insert", event);
				result.setData(event);
			} catch (Exception e) {
				logger.error(e.getMessage());
				result.setResponseCode(ResponseCode.RESPONSE_ERROR);
				result.setDescription(e.getMessage());
			} finally {
				return result;
			}
		}
	}

	@Override
	public ResultData queryGiftEvent(Map<String, Object> condition) {
		ResultData result = new ResultData();
		condition = handle(condition);
		try {
			List<GiftEvent> list = sqlSession.selectList("selling.event.query", condition);
			result.setData(list);
		} catch (Exception e) {
			logger.error(e.getMessage());
			result.setResponseCode(ResponseCode.RESPONSE_ERROR);
			result.setDescription(e.getMessage());
		} finally {
			return result;
		}
	}

	@Override
	public ResultData updateGiftEvent(GiftEvent event) {
		ResultData result = new ResultData();
		try {
			sqlSession.update("selling.event.update", event);
			result.setData(event);
		} catch (Exception e) {
			logger.error(e.getMessage());
			result.setResponseCode(ResponseCode.RESPONSE_ERROR);
			result.setDescription(e.getMessage());
		} finally {
			return result;
		}
	}

	@Override
	public ResultData queryGiftEventByPage(Map<String, Object> condition, DataTableParam param) {
		ResultData result = new ResultData();
		DataTablePage<GiftEvent> page = new DataTablePage<>(param);
		condition = handle(condition);
		ResultData total = queryGiftEvent(condition);
		if (total.getResponseCode() != ResponseCode.RESPONSE_OK) {
			result.setResponseCode(ResponseCode.RESPONSE_ERROR);
			result.setDescription(total.getDescription());
			return result;
		}
		page.setiTotalRecords(((List<GiftEvent>) total.getData()).size());
		page.setiTotalDisplayRecords(((List<GiftEvent>) total.getData()).size());
		List<GiftEvent> current = queryGiftEventByPage(condition, param.getiDisplayStart(), param.getiDisplayLength());
		if (current.isEmpty()) {
			result.setResponseCode(ResponseCode.RESPONSE_NULL);
		}
		page.setData(current);
		result.setData(page);
		return result;
	}

	private List<GiftEvent> queryGiftEventByPage(Map<String, Object> condition, int start, int length) {
		List<GiftEvent> result = new ArrayList<>();
		try {
			result = sqlSession.selectList("selling.event.query", condition, new RowBounds(start, length));
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			return result;
		}
	}

}