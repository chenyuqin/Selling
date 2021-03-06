package selling.sunshine.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import common.sunshine.dao.BaseDao;
import selling.sunshine.dao.FollowerDao;
import common.sunshine.utils.ResponseCode;
import common.sunshine.utils.ResultData;
import selling.wechat.model.Follower;

/**
 * 微信服务号关注用户与持久层交互接口
 * Created by sunshine on 5/25/16.
 */
@Repository
public class FollowerDaoImpl extends BaseDao implements FollowerDao {
    private Logger logger = LoggerFactory.getLogger(FollowerDaoImpl.class);

    private Object lock = new Object();

    /**
     * 添加关注用户信息记录
     *
     * @param follower
     * @return
     */
    @Override
    public ResultData insertFollower(Follower follower) {
        ResultData result = new ResultData();
        synchronized (lock) {
            try {
                sqlSession.insert("selling.wechat.insert", follower);
                result.setData(follower);
            } catch (Exception e) {
                logger.error(e.getMessage());
                result.setResponseCode(ResponseCode.RESPONSE_ERROR);
                result.setDescription(e.getMessage());
            } finally {
                return result;
            }
        }
    }

    /**
     * 用户取消关注时,删除用户信息(标记删除)
     *
     * @param openId
     * @return
     */
    @Override
    public ResultData blockFollower(String openId) {
        ResultData result = new ResultData();
        synchronized (lock) {
            try {
                sqlSession.update("selling.wechat.block", openId);
            } catch (Exception e) {
                logger.error(e.getMessage());
                result.setResponseCode(ResponseCode.RESPONSE_ERROR);
                result.setDescription(e.getMessage());
            } finally {
                return result;
            }
        }
    }
}
