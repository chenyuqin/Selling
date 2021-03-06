package selling.sunshine.dao.impl;

import common.sunshine.utils.IDGenerator;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import selling.sunshine.dao.AdminDao;
import common.sunshine.dao.BaseDao;
import common.sunshine.model.selling.admin.Admin;
import common.sunshine.model.selling.user.Role;
import common.sunshine.model.selling.user.User;
import common.sunshine.pagination.DataTablePage;
import common.sunshine.pagination.DataTableParam;
import common.sunshine.utils.ResponseCode;
import common.sunshine.utils.ResultData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 管理员与持久层交互接口实现类
 * Created by sunshine on 4/21/16.
 */
@Repository
public class AdminDaoImpl extends BaseDao implements AdminDao {
    private Logger logger = LoggerFactory.getLogger(AdminDaoImpl.class);

    private Object lock = new Object();

    /**
     * 根据查询的条件查找符合条件的管理员列表
     *
     * @param condition
     * @return
     */
    @Override
    public ResultData queryAdmin(Map<String, Object> condition) {
        ResultData result = new ResultData();
        try {
            List<Admin> list = sqlSession.selectList("selling.admin.query", condition);
            result.setData(list);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.setResponseCode(ResponseCode.RESPONSE_ERROR);
            result.setDescription(e.getMessage());
        } finally {
            return result;
        }
    }

    /**
     * 分页根据查询的条件查找符合条件的管理员的账号信息
     *
     * @param condition
     * @param param
     * @return
     */
    @Override
    public ResultData queryAdminByPage(Map<String, Object> condition, DataTableParam param) {
        ResultData result = new ResultData();
        DataTablePage<Admin> page = new DataTablePage<>(param);
        condition = handle(condition);
        ResultData total = queryAdmin(condition);
        if (total.getResponseCode() != ResponseCode.RESPONSE_OK) {
            result.setResponseCode(ResponseCode.RESPONSE_ERROR);
            result.setDescription(total.getDescription());
            return result;
        }
        page.setiTotalRecords(((List<Admin>) total.getData()).size());
        page.setiTotalDisplayRecords(((List<Admin>) total.getData()).size());
        List<Admin> current = queryAdminByPage(condition, param.getiDisplayStart(), param.getiDisplayLength());
        if (current.isEmpty()) {
            result.setResponseCode(ResponseCode.RESPONSE_NULL);
        }
        page.setData(current);
        result.setData(page);
        return result;
    }

    /**
     * 添加管理员记录,同时添加用户表中的相应记录
     *
     * @param admin
     * @return
     */
    @Transactional
    @Override
    public ResultData insertAdmin(Admin admin, Role role) {
        ResultData result = new ResultData();
        synchronized (lock) {
            try {
                admin.setAdminId(IDGenerator.generate("MNG"));
                sqlSession.insert("selling.admin.insert", admin);
                User user = new User(admin.getUsername(), admin.getPassword());
                user.setUserId(IDGenerator.generate("USR"));
                user.setRole(role);
                user.setAdmin(admin);
                sqlSession.insert("selling.user.insert", user);
                result.setData(admin);
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
     * 更新管理员记录,同时更新用户表
     *
     * @param admin
     * @return
     */
    @Transactional
    @Override
    public ResultData updateAdmin(Admin admin) {
        ResultData result = new ResultData();
        synchronized (lock) {
            try {
                sqlSession.update("selling.admin.update", admin);
                User user = new User(admin.getUsername(), admin.getPassword());
                user.setAdmin(admin);
                sqlSession.update("selling.user.update", user);
                result.setData(admin);
            } catch (Exception e) {
                logger.debug(e.getMessage());
                result.setResponseCode(ResponseCode.RESPONSE_ERROR);
                result.setDescription(e.getMessage());
            } finally {
                return result;
            }
        }
    }

    /**
     * 删除管理员账户信息
     *
     * @param admin
     * @return
     */
    @Transactional
    @Override
    public ResultData deleteAdmin(Admin admin) {
        ResultData result = new ResultData();
        synchronized (lock) {
            try {
                User user = new User(admin.getUsername(), admin.getPassword());
                user.setAdmin(admin);
                sqlSession.delete("selling.user.delete", user);
                sqlSession.delete("selling.admin.delete", admin);
                result.setData(admin);
            } catch (Exception e) {
                logger.debug(e.getMessage());
                result.setResponseCode(ResponseCode.RESPONSE_ERROR);
                result.setDescription(e.getMessage());
            } finally {
                return result;
            }
        }
    }

    /**
     * 查询某一页的管理员信息
     *
     * @param condition
     * @param start
     * @param length
     * @return
     */
    private List<Admin> queryAdminByPage(Map<String, Object> condition, int start, int length) {
        List<Admin> result = new ArrayList<>();
        try {
            result = sqlSession.selectList("selling.admin.query", condition, new RowBounds(start, length));
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            return result;
        }
    }
}
