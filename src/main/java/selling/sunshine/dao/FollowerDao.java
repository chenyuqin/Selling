package selling.sunshine.dao;

import common.sunshine.utils.ResultData;
import selling.wechat.model.Follower;

/**
 * Created by sunshine on 5/25/16.
 */
public interface FollowerDao {
    ResultData insertFollower(Follower follower);

    ResultData blockFollower(String openId);
}
