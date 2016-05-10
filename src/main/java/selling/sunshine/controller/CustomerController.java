package selling.sunshine.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import selling.sunshine.form.CustomerForm;
import selling.sunshine.model.Agent;
import selling.sunshine.model.Customer;
import selling.sunshine.model.User;
import selling.sunshine.pagination.DataTablePage;
import selling.sunshine.pagination.DataTableParam;
import selling.sunshine.service.CustomerService;
import selling.sunshine.utils.ResponseCode;
import selling.sunshine.utils.ResultData;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunshine on 4/11/16.
 */
@RequestMapping("/customer")
@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @RequestMapping(method = RequestMethod.GET, value = "/overview")
    public ModelAndView overview() {
        ModelAndView view = new ModelAndView();
        view.setViewName("/backend/customer/overview");
        return view;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/overview")
    public DataTablePage<Customer> overview(DataTableParam param) {
        DataTablePage<Customer> result = new DataTablePage<Customer>();
        if (StringUtils.isEmpty(result)) {
            return result;
        }
        Map<String, Object> condition = new HashMap<String, Object>();
        ResultData fetchResponse = customerService.fetchCustomer(condition, param);
        if (fetchResponse.getResponseCode() == ResponseCode.RESPONSE_OK) {
            result = (DataTablePage<Customer>) fetchResponse.getData();
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/add")
    public ResultData addCustomer(@Valid CustomerForm customerForm,
                                  BindingResult result) {
        ResultData resultData = new ResultData();
        if (result.hasErrors()) {
            resultData.setResponseCode(ResponseCode.RESPONSE_ERROR);
            return resultData;
        }
        Subject subject = SecurityUtils.getSubject();
        Agent agent = null;
        if (subject != null) {
            Session session = subject.getSession();
            User user = (User) session.getAttribute("current");
            agent = user.getAgent();
        }
        Customer customer = new Customer(customerForm.getName(),
                customerForm.getAddress(), customerForm.getPhone(), agent);
        resultData = customerService.createCustomer(customer);
        return resultData;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/modify")
    public ResultData updateCustomer(@Valid CustomerForm customerForm,
            BindingResult result) {
        ResultData resultData = new ResultData();
        if (result.hasErrors()) {
            resultData.setResponseCode(ResponseCode.RESPONSE_ERROR);
            return resultData;
        }
        Subject subject = SecurityUtils.getSubject();
        Agent agent = null;
        if (subject != null) {
            Session session = subject.getSession();
            User user = (User) session.getAttribute("current");
            agent = user.getAgent();
        }
        Customer customer = new Customer(customerForm.getName(),
                customerForm.getAddress(), customerForm.getPhone(), agent);
        resultData =  customerService.updateCustomer(customer);
        System.out.println("-----------------------------------------------------------------test----------------------------------------------------------");
        Customer customer2=(Customer)resultData.getData();
        System.out.println(customer2.getAddress().getAddress());
        System.out.println(customer2.getPhone().getPhone());
        return resultData;
    }
}
