package selling.sunshine.service;

import common.sunshine.model.selling.admin.Admin;
import common.sunshine.model.selling.user.Role;
import common.sunshine.pagination.DataTableParam;
import common.sunshine.utils.ResultData;

import java.util.Map;

/**
 * Created by sunshine on 4/17/16.
 */
public interface AdminService {
    ResultData login(Admin admin);

    ResultData createAdmin(Admin admin, Role role);

    ResultData fetchAdmin(Map<String, Object> condition);

    ResultData fetchAdmin(Map<String, Object> condition, DataTableParam param);

    ResultData updateAdmin(Admin admin);

    ResultData deleteAdmin(Admin admin);
}
