package selling.sunshine.controller;

import com.alibaba.fastjson.JSONObject;
import com.pingplusplus.model.Event;
import com.pingplusplus.model.Webhooks;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import selling.sunshine.form.AgentForm;
import selling.sunshine.form.AgentLoginForm;
import selling.sunshine.form.OrderItemForm;
import selling.sunshine.form.SortRule;
import selling.sunshine.model.*;
import selling.sunshine.pagination.DataTablePage;
import selling.sunshine.pagination.DataTableParam;
import selling.sunshine.service.*;
import selling.sunshine.utils.Prompt;
import selling.sunshine.utils.PromptCode;
import selling.sunshine.utils.ResponseCode;
import selling.sunshine.utils.ResultData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * Created by sunshine on 4/8/16.
 */
@RequestMapping("/agent")
@RestController
public class AgentController {
    private Logger logger = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private AgentService agentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommodityService commodityService;

    @Autowired
    private ToolService toolService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private BillService billService;

    @Autowired
    private ShipmentService shipmentService;

    @RequestMapping(method = RequestMethod.GET, value = "/me/index")
    public ModelAndView index() {
        ModelAndView view = new ModelAndView();
        view.setViewName("/agent/me/index");
        return view;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/order/place")
    public ModelAndView placeOrder() {
        ModelAndView view = new ModelAndView();
        //获取当前登录的用户
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        //创建查询的删选条件集合
        Map<String, Object> condition = new HashMap<>();
        //查询商品信息
        condition.put("blockFlag", false);
        ResultData fetchGoodsResponse = commodityService.fetchCommodity(condition);
        if (fetchGoodsResponse.getResponseCode() == ResponseCode.RESPONSE_OK) {
            view.addObject("goods", fetchGoodsResponse.getData());
        }
        //查询代理商的客户列表
        condition.clear();
        condition.put("agentId", user.getAgent().getId());
        ResultData fetchCustomerResponse = customerService.fetchCustomer(condition);
        if (fetchCustomerResponse.getResponseCode() == ResponseCode.RESPONSE_OK) {
            view.addObject("customer", fetchCustomerResponse.getData());
        }
        //查询平台当前配置的发货日期
        condition.clear();
        ResultData fetchShipmentResponse = shipmentService.fetchShipmentConfig(condition);
        if (fetchShipmentResponse.getResponseCode() == ResponseCode.RESPONSE_OK) {
            List<ShipConfig> shipmentList = (List<ShipConfig>) fetchShipmentResponse.getData();
            int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            shipmentList.sort(new Comparator<ShipConfig>() {
                @Override
                public int compare(ShipConfig ship1, ShipConfig ship2) {
                    return Integer.valueOf(ship1.getDate()).compareTo(Integer.valueOf(ship2.getDate()));
                }
            });
            int shipDay = 0;
            boolean isNextMonth = false;
            for (ShipConfig ship : shipmentList) {
                if (ship.getDate() > currentDay) {
                    shipDay = ship.getDate();
                    break;
                }
            }
            if (shipDay == 0) {
                shipDay = shipmentList.get(0).getDate();
                isNextMonth = true;
            }
            view.addObject("shipDay", shipDay);
            view.addObject("isNextMonth", isNextMonth);
        }
        //根据代理商的ID查询代理商的详细信息
        condition.clear();
        condition.put("agentId", user.getAgent().getId());
        ResultData queryAgentResponse = agentService.fetchAgent(condition);
        Agent agent = ((List<Agent>) queryAgentResponse.getData()).get(0);
        if (agent.isGranted()) {
            view.setViewName("/agent/order/place");
            return view;
        }
        Prompt prompt = new Prompt(PromptCode.WARNING, "提示", "尊敬的代理商，您的资料现在正在审核中，只有当审核通过后才能代客下单，请耐心等待！", "/agent/login");
        view.addObject("prompt", prompt);
        view.setViewName("/agent/prompt");
        return view;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/order/modify/{orderId}")
    public ModelAndView modifyOrder(@PathVariable("orderId") String orderId) {
        ModelAndView view = new ModelAndView();
        //获取当前登陆的用户
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        Map<String, Object> condition = new HashMap<>();
        //获取商品列表
        condition.put("blockFlag", false);
        ResultData fetchGoodsResponse = commodityService.fetchCommodity(condition);
        if (fetchGoodsResponse.getResponseCode() == ResponseCode.RESPONSE_OK) {
            view.addObject("goods", fetchGoodsResponse.getData());
        }
        //获取当前代理的客户列表
        condition.clear();
        condition.put("agentId", user.getAgent().getId());
        ResultData fetchCustomerResponse = customerService.fetchCustomer(condition);
        if (fetchCustomerResponse.getResponseCode() == ResponseCode.RESPONSE_OK) {
            view.addObject("customer", fetchCustomerResponse.getData());
        }
        //获取订单
        condition.clear();
        condition.put("orderId", orderId);
        ResultData fetchOrderResponse = orderService.fetchOrder(condition);
        if (fetchOrderResponse.getResponseCode() == ResponseCode.RESPONSE_OK) {
            Order order = ((List<Order>) fetchOrderResponse.getData()).get(0);
            view.addObject("order", order);
            view.addObject("status", order.getStatus());
        }
        view.addObject("operation", "MODIFY");
        //查询代理的详细信息
        condition.clear();
        condition.put("agentId", user.getAgent().getId());
        ResultData fetchAgentResponse = agentService.fetchAgent(condition);
        Agent agent = ((List<Agent>) fetchAgentResponse.getData()).get(0);
        if (agent.isGranted()) {
            view.setViewName("/agent/order/modify");
            return view;
        }
        Prompt prompt = new Prompt(PromptCode.WARNING, "提示", "尊敬的代理商，您的资料现在正在审核中，只有当审核通过后才能代客下单，请耐心等待！", "/agent/login");
        view.addObject("prompt", prompt);
        view.setViewName("/agent/prompt");
        return view;
    }


    @RequestMapping(method = RequestMethod.POST, value = "/order/place/{type}")
    public ModelAndView placeOrder(@Valid OrderItemForm form, BindingResult result, RedirectAttributes attr, @PathVariable("type") String type) {
        ModelAndView view = new ModelAndView();
        if (result.hasErrors()) {
            view.setViewName("redirect:/agent/order/place");
            return view;
        }
        Subject subject = SecurityUtils.getSubject();
        List<OrderItem> orderItems = new ArrayList<OrderItem>();
        int length = form.getCustomerId().length;
        Order order = new Order();
        Agent agent = new Agent();
        User user = (User) subject.getPrincipal();
        agent.setAgentId(user.getAgent().getId());
        order.setAgent(agent);
        //创建订单和订单项
        double order_price = 0;
        for (int i = 0; i < length; i++) {
            String goodsId = form.getGoodsId()[i];//商品ID
            String customerId = form.getCustomerId()[i];//顾客ID
            int goodsQuantity = Integer.parseInt(form.getGoodsQuantity()[i]);//商品数量
            double orderItemPrice = 0;//OrderItem总价
            Map<String, Object> goodsCondition = new HashMap<String, Object>();//查询商品价格
            goodsCondition.put("goodsId", goodsId);
            ResultData goodsData = commodityService.fetchCommodity(goodsCondition);
            Goods goods = null;
            if (goodsData.getResponseCode() == ResponseCode.RESPONSE_OK) {
                List<Goods> goodsList = (List<Goods>) goodsData.getData();
                if (goodsList.size() != 1) {
                    Prompt prompt = new Prompt(PromptCode.WARNING, "提示", "商品不唯一或未找到", "/agent/order/place");
                    attr.addFlashAttribute("prompt", prompt);
                    view.setViewName("redirect:/agent/prompt");
                    return view;
                }
                goods = goodsList.get(0);
            } else {
                Prompt prompt = new Prompt(PromptCode.WARNING, "提示", "商品信息异常", "/agent/order/place");
                attr.addFlashAttribute("prompt", prompt);
                view.setViewName("redirect:/agent/prompt");
                return view;
            }
            orderItemPrice = goods.getPrice() * goodsQuantity;//得到一个OrderItem的总价
            order_price += orderItemPrice;//累加Order总价
            OrderItem orderItem = new OrderItem(customerId, goodsId, goodsQuantity, orderItemPrice);//构造OrderItem
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);//构造Order
        order.setPrice(order_price);
        switch (type) {
            case "save":
                order.setStatus(OrderStatus.SAVED);
                break;
            case "submit":
                order.setStatus(OrderStatus.SUBMITTED);
                break;
            default:
                order.setStatus(OrderStatus.SAVED);
        }

        ResultData fetchResponse = orderService.placeOrder(order);
        if (fetchResponse.getResponseCode() == ResponseCode.RESPONSE_OK) {
            if (type.equals("save")) {
                Prompt prompt = new Prompt("提示", "保存成功", "/agent/order/manage/0");
                attr.addFlashAttribute("prompt", prompt);
                view.setViewName("redirect:/agent/prompt");
            } else if (type.equals("submit")) {
                view.setViewName("redirect:/order/pay/" + order.getOrderId());
            }
            return view;
        }
        Prompt prompt = new Prompt(PromptCode.WARNING, "提示", "失败", "/agent/order/manage/0");
        attr.addFlashAttribute("prompt", prompt);
        view.setViewName("redirect:/agent/prompt");
        return view;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/order/manage/{type}")
    public ModelAndView manageOrder(@PathVariable("type") String type) {
        ModelAndView view = new ModelAndView();
        view.addObject("type", type);
        view.setViewName("/agent/order/manage");
        return view;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/order/list/{type}")
    public ResultData viewOrderList(@PathVariable("type") String type) {
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        ResultData result = new ResultData();
        List<SortRule> orderBy = new ArrayList<SortRule>();
        orderBy.add(new SortRule("create_time", "desc"));
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("agentId", user.getAgent().getId());
        condition.put("status", type);
        condition.put("sort", orderBy);
        ResultData fetchResponse = orderService.fetchOrder(condition);
        if (fetchResponse.getResponseCode() == ResponseCode.RESPONSE_OK) {
            result.setData(fetchResponse.getData());
        } else {
            result.setResponseCode(fetchResponse.getResponseCode());
            result.setDescription(fetchResponse.getDescription());
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/order/detail/{orderId}")
    public ModelAndView viewOrder(@PathVariable("orderId") String orderId) {
        ModelAndView view = new ModelAndView();
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("agentId", user.getAgent().getId());
        condition.put("orderId", orderId);
        ResultData fetchOrderResponse = orderService.fetchOrder(condition);
        Order order = ((List<Order>) fetchOrderResponse.getData()).get(0);
        view.addObject("order", order);
        view.addObject("status", order.getStatus());
        view.addObject("operation", "VIEW");
        view.setViewName("/agent/order/modify");
        return view;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/customer/manage")
    public ModelAndView manageCustomer() {
        ModelAndView view = new ModelAndView();
        Subject subject = SecurityUtils.getSubject();
        User user = null;
        //获取Agent详细信息
        Map<String, Object> condition = new HashMap<>();
        condition.put("agentId", user.getAgent().getId());
        condition.put("blockFlag", false);
        ResultData fetchResposne = agentService.fetchAgent(condition);
        if (fetchResposne.getResponseCode() == ResponseCode.RESPONSE_OK) {
            Agent agent = ((List<Agent>) fetchResposne.getData()).get(0);
            view.addObject("agent", agent);
        }
        view.setViewName("/agent/customer/manage");
        return view;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/customer/list")
    public ResultData viewCustomerList() {
        ResultData result = new ResultData();
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        Map<String, Object> condition = new HashMap<>();
        condition.put("agentId", user.getAgent().getId());
        ResultData fetchResponse = customerService.fetchCustomer(condition);
        if (fetchResponse.getResponseCode() == ResponseCode.RESPONSE_OK) {
            result.setData(fetchResponse.getData());
        } else {
            result.setResponseCode(fetchResponse.getResponseCode());
            result.setDescription(fetchResponse.getDescription());
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/statement")
    public ModelAndView statement() {
        ModelAndView view = new ModelAndView();
        view.setViewName("/agent/account/statement");
        return view;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/register")
    public ModelAndView register() {
        ModelAndView view = new ModelAndView();
        view.setViewName("/agent/register");
        return view;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/register")
    public ModelAndView register(@Valid AgentForm form, BindingResult result, RedirectAttributes attr) {
        ModelAndView view = new ModelAndView();
        if (result.hasErrors()) {
            view.setViewName("redirect:/agent/register");
            return view;
        }
        try {
            Agent agent = new Agent(form.getName(), form.getGender(), form.getPhone(), form.getAddress(), form.getPassword(), form.getWechat());
            ResultData createResponse = agentService.createAgent(agent);
            if (createResponse.getResponseCode() == ResponseCode.RESPONSE_OK) {
                Prompt prompt = new Prompt("提示", "您已成功提交申请,待审核后即可使用", "/agent/login");
                attr.addFlashAttribute("prompt", prompt);
                view.setViewName("redirect:/agent/prompt");
                return view;
            } else {
                view.setViewName("redirect:/agent/register");
                return view;
            }
        } catch (Exception e) {
            view.setViewName("redirect:/agent/register");
            return view;
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public ModelAndView login() {
        ModelAndView view = new ModelAndView();
        view.setViewName("/agent/login");
        return view;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/login")
    public ModelAndView login(@Valid AgentLoginForm form, BindingResult result) {
        ModelAndView view = new ModelAndView();
        if (result.hasErrors()) {
            view.setViewName("redirect:/agent/login");
            return view;
        }
        try {
            Subject subject = SecurityUtils.getSubject();
            if (subject.isAuthenticated()) {
                view.setViewName("redirect:/agent/order/place");
                return view;
            }
            subject.login(new UsernamePasswordToken(form.getPhone(), form.getPassword()));
        } catch (Exception e) {
            view.setViewName("redirect:/agent/login");
            return view;
        }
        view.setViewName("redirect:/agent/order/place");
        return view;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/prompt")
    public ModelAndView prompt() {
        ModelAndView view = new ModelAndView();
        view.setViewName("/agent/prompt");
        return view;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/check")
    public ModelAndView check() {
        ModelAndView view = new ModelAndView();
        view.setViewName("/backend/agent/check");
        return view;
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/check")
    public DataTablePage<Agent> check(DataTableParam param) {
        DataTablePage<Agent> result = new DataTablePage<Agent>(param);
        if (StringUtils.isEmpty(param)) {
            return result;
        }
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("granted", false);
        ResultData fetchResponse = agentService.fetchAgent(condition, param);
        if (fetchResponse.getResponseCode() == ResponseCode.RESPONSE_OK) {
            result = (DataTablePage<Agent>) fetchResponse.getData();
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/grant")
    public ModelAndView grant(String agentId) {
        ModelAndView view = new ModelAndView();
        if (StringUtils.isEmpty(agentId)) {
            view.setViewName("redirect:/agent/check");
            return view;
        }
        Agent agent = new Agent();
        agent.setAgentId(agentId);
        agent.setGranted(true);
        ResultData updateResponse = agentService.updateAgent(agent);
        if (updateResponse.getResponseCode() != ResponseCode.RESPONSE_OK) {
            view.setViewName("redirect:/agent/check");
            return view;
        }
        view.setViewName("redirect:/agent/check");
        return view;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/overview")
    public ModelAndView overview() {
        ModelAndView view = new ModelAndView();
        view.setViewName("/backend/agent/overview");
        return view;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/overview")
    public DataTablePage<Agent> overview(DataTableParam param) {
        DataTablePage<Agent> result = new DataTablePage<>(param);
        if (StringUtils.isEmpty(param)) {
            return result;
        }
        Map<String, Object> condition = new HashMap<>();
        condition.put("granted", true);
        List<SortRule> rule = new ArrayList<>();
        rule.add(new SortRule("create_time", "desc"));
        condition.put("sort", rule);
        logger.debug(JSONObject.toJSONString(condition));
        ResultData fetchResponse = agentService.fetchAgent(condition, param);
        if (fetchResponse.getResponseCode() == ResponseCode.RESPONSE_OK) {
            result = (DataTablePage<Agent>) fetchResponse.getData();
        }
        return result;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reward")
    @ResponseBody
    public ResultData reward(HttpServletRequest request, HttpServletResponse response) throws IOException {

        ResultData resultData = new ResultData();
        JSONObject webhooks = toolService.getParams(request);
        logger.debug("webhooks info == " + webhooks);
        JSONObject charge = webhooks.getJSONObject("data").getJSONObject("object");
        logger.debug("charge info == " + charge);
        String dealId = charge.getString("order_no");
        logger.debug("deal id: " + dealId);


        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("billId", dealId);
        resultData = billService.fetchDepositBill(condition);
        DepositBill depositBill = ((List<DepositBill>) resultData.getData()).get(0);
        //获取代理商信息
        condition.clear();
        condition.put("agentId", depositBill.getAgent().getId());
        condition.put("blockFlag", false);
        ResultData fetchAgentResponse = agentService.fetchAgent(condition);
        Agent agent = ((List<Agent>) fetchAgentResponse.getData()).get(0);
        agent.setCoffer(agent.getCoffer() + depositBill.getBillAmount());
        agentService.updateAgent(agent);
        billService.updateDepositBill(depositBill);

        Event event = Webhooks.eventParse(webhooks.toString());
        if ("charge.succeeded".equals(event.getType())) {
            response.setStatus(200);
        } else if ("refund.succeeded".equals(event.getType())) {
            response.setStatus(200);
        } else {
            response.setStatus(500);
        }
        return resultData;
    }
}
