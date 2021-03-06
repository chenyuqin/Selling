package promotion.sunshine.controller;

import common.sunshine.model.selling.event.*;

import common.sunshine.utils.ResponseCode;
import common.sunshine.utils.ResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import promotion.sunshine.form.EventApplicationForm;
import promotion.sunshine.service.EventService;
import promotion.sunshine.service.FollowerService;
import promotion.sunshine.utils.Prompt;
import promotion.sunshine.utils.PromptCode;
import promotion.sunshine.utils.WechatConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunshine on 8/22/16.
 * @author wangmin
 */
@RestController
@RequestMapping("/event")
public class EventController {
    private Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private FollowerService followerService;

    /**
     * 赠送活动页面
     *
     * @param eventName
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{eventName}/{openId}")
    public ModelAndView view(@PathVariable("eventName") String eventName, @PathVariable("openId") String openId, HttpServletRequest request) {
        ModelAndView view = new ModelAndView();
        /*
         * 先从订阅号菜单链接获取openId，如果没有则从session中获取openId
         */
        HttpSession session = request.getSession();
        if (openId.equals("") || openId == null) {
            if (session.getAttribute("openId") == null || (session.getAttribute("openId")).equals("") || openId.equals("")) {
                Prompt prompt = new Prompt(PromptCode.WARNING, "提示", "超时,请重新从订阅号菜单进入活动", "");
                view.addObject("prompt", prompt);
                view.setViewName("/customer/event/prompt");
                return view;
            } else {
                openId = (String) session.getAttribute("openId");
            }
        } else {
            session.setAttribute("openId", openId);
        }
        /*
         * 先查询有没有该活动，然后查询用户是否已经填过表单，然后查询活动是否结束或未开始
		 */
        Map<String, Object> condition = new HashMap<>();
        condition.put("nickname", eventName);
        condition.put("blockFlag", false);
        ResultData fetchEventResponse = eventService.fetchGiftEvent(condition);
        if (fetchEventResponse.getResponseCode() != ResponseCode.RESPONSE_OK) {
            Prompt prompt = new Prompt(PromptCode.WARNING, "提示", "活动已结束", "");
            view.addObject("prompt", prompt);
            String url = "http://mp.weixin.qq.com/s?__biz=MzI1OTMyNTI1NQ==&mid=100000233&idx=1&sn=85b05c7a3dca6429e66ddf7762de06aa#wechat_redirect";
            WechatConfig.oauthWechat(view, "/event/" + eventName + "/" + openId, url);
            view.setViewName("/customer/event/prompt");
            return view;
        }
        GiftEvent event = ((List<GiftEvent>) fetchEventResponse.getData()).get(0);
		/*
		 * 查询用户是否已经填过表单
		 */
        condition.clear();
        condition.put("eventId", event.getEventId());
        condition.put("donorWechat", openId);
        condition.put("blockFlag", false);
        ResultData fetchEventApplicationResponse = eventService.fetchEventApplication(condition);
        if (fetchEventApplicationResponse.getResponseCode() == ResponseCode.RESPONSE_OK) {
            Prompt prompt = new Prompt(PromptCode.SUCCESS, "您已经提交过申请", "请从“活动”菜单中查询活动申请，若您还没有关注我们，请搜索“云草健康”公众号并关注", "");
            view.addObject("prompt", prompt);
            String url = "http://mp.weixin.qq.com/s?__biz=MzI1OTMyNTI1NQ==&mid=100000233&idx=1&sn=85b05c7a3dca6429e66ddf7762de06aa#wechat_redirect";
            WechatConfig.oauthWechat(view, "/event/" + eventName + "/" + openId, url);
            view.setViewName("/customer/event/prompt");
            return view;
        }
        /*
         * 查询活动是否开始
         */
        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (now.before(event.getStart())) {
            Prompt prompt = new Prompt(PromptCode.WARNING, "提示", "活动尚未开始", "");
            view.addObject("prompt", prompt);
            String url = "http://mp.weixin.qq.com/s?__biz=MzI1OTMyNTI1NQ==&mid=100000233&idx=1&sn=85b05c7a3dca6429e66ddf7762de06aa#wechat_redirect";
            WechatConfig.oauthWechat(view, "/event/" + eventName + "/" + openId, url);
            view.setViewName("/customer/event/prompt");
            return view;
        }
        /*
         * 活动已结束判断 
         */
        if (now.after(event.getEnd())) {
            Prompt prompt = new Prompt(PromptCode.WARNING, "提示", "活动已结束", "");
            view.addObject("prompt", prompt);
            String url = "http://mp.weixin.qq.com/s?__biz=MzI1OTMyNTI1NQ==&mid=100000233&idx=1&sn=85b05c7a3dca6429e66ddf7762de06aa#wechat_redirect";
            WechatConfig.oauthWechat(view, "/event/" + eventName + "/" + openId, url);
            view.setViewName("/customer/event/prompt");
            return view;
        }
        view.addObject("event", event);
        String url = "http://mp.weixin.qq.com/s?__biz=MzI1OTMyNTI1NQ==&mid=100000233&idx=1&sn=85b05c7a3dca6429e66ddf7762de06aa#wechat_redirect";
        WechatConfig.oauthWechat(view, "/event/" + eventName + "/" + openId, url);
        view.setViewName("/customer/event/apply");
        return view;
    }
    
    /**
     * 活动申请表单的post方法
     * @param form
     * @param result
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/giftapplication")
    public ModelAndView giftApplication(@Valid EventApplicationForm form, BindingResult result, HttpServletRequest request) {
        ModelAndView view = new ModelAndView();
        /*
         *先判断是否有openId 
         */
        HttpSession session = request.getSession();
        if (session.getAttribute("openId") == null || ((String) session.getAttribute("openId")).equals("")) {
            Prompt prompt = new Prompt(PromptCode.WARNING, "提示", "操作超时，请重新进入活动", "");
            view.addObject("prompt", prompt);
            view.setViewName("/customer/event/prompt");
            return view;
        }
        /*
         * 判断该活动是否因过期等原因被block
         */
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("eventId", form.getEvent_id());
        condition.put("blockFlag", false);
        ResultData fetchEventResponse = eventService.fetchGiftEvent(condition);
        Event event = ((List<Event>) fetchEventResponse.getData()).get(0);
        if (fetchEventResponse.getResponseCode() != ResponseCode.RESPONSE_OK) {
            Prompt prompt = new Prompt(PromptCode.WARNING, "提示", "未找到该活动", "");
            view.addObject("prompt", prompt);
            view.setViewName("/customer/event/prompt");
            return view;
        }
        String eventName = event.getNickname();
        String openId = (String) session.getAttribute("openId");
        /*
         * 判断是否已经提交过申请 
         */
        condition.clear();
        condition.put("eventId", event.getEventId());
        condition.put("donorWechat", (String) session.getAttribute("openId"));
        condition.put("blockFlag", false);
        ResultData fetchEventApplicationResponse = eventService.fetchEventApplication(condition);
        if (fetchEventApplicationResponse.getResponseCode() == ResponseCode.RESPONSE_OK) {
            Prompt prompt = new Prompt(PromptCode.SUCCESS, "您已经提交过申请", "请从“活动”菜单中查询活动申请，若您还没有关注我们，请搜索“云草健康”公众号并关注", "");
            view.addObject("prompt", prompt);
            String url = "http://mp.weixin.qq.com/s?__biz=MzI1OTMyNTI1NQ==&mid=100000233&idx=1&sn=85b05c7a3dca6429e66ddf7762de06aa#wechat_redirect";
            WechatConfig.oauthWechat(view, "/event/giftapplication", url);
            view.setViewName("/customer/event/prompt");
            return view;
        }
        /*
         * 数据库插入申请
         */
        EventApplication eventApplication = new EventApplication(event, form.getDonor_name(), form.getDonor_phone(), form.getDonee_name(), form.getDonee_phone(), form.getDonee_gender(), form.getDonee_address(), form.getDonee_age_range(), form.getRelation(), form.getWishes(), (String) session.getAttribute("openId"));
        ResultData insertEventApplicationResponse = eventService.insertEventApplication(eventApplication);
        if (insertEventApplicationResponse.getResponseCode() != ResponseCode.RESPONSE_OK) {
            Prompt prompt = new Prompt(PromptCode.WARNING, "提示", "申请失败", "");
            view.addObject("prompt", prompt);
            view.setViewName("/customer/event/prompt");
            return view;
        }
        /*
         * 对于中秋活动来说，有多选题，这里一个申请对应多个选择(option)，数据库插入这些选择(option)
         */
        if (form.getOption_id() != null) {
            String[] optionIds = form.getOption_id();
            //插入一个申请
            eventApplication = (EventApplication) insertEventApplicationResponse.getData();
            for (String optionId : optionIds) {
                condition.clear();
                condition.put("optionId", optionId);
                condition.put("blockFlag", false);
                //对每个传过来的回答选项id，查询选项
                ResultData fetchOption = eventService.fetchQuestionOption(condition);
                if (fetchOption.getResponseCode() == ResponseCode.RESPONSE_OK) {
                	//将每个申请的回答插入数据库
                    QuestionOption questionOption = ((List<QuestionOption>) fetchOption.getData()).get(0);
                    QuestionAnswer questionAnswer = new QuestionAnswer(eventApplication, questionOption.getQuestion().getContent(), questionOption.getValue(), questionOption.getQuestion().getRank());
                    eventService.insertQuestionAnswer(questionAnswer);
                }
            }
        }
        Prompt prompt = new Prompt(PromptCode.SUCCESS, "提示", "&nbsp;&nbsp;恭喜您申请成功！<br>结果将以短信形式告知，祝您中秋快乐，阖家团圆", "");
        String url = "http://mp.weixin.qq.com/s?__biz=MzI1OTMyNTI1NQ==&mid=100000233&idx=1&sn=85b05c7a3dca6429e66ddf7762de06aa#wechat_redirect";
        WechatConfig.oauthWechat(view, "/event/giftapplication", url);
        view.addObject("prompt", prompt);
        view.setViewName("/customer/event/prompt");
        return view;
    }
	/**
	 * 查询申请结果-查询页面
	 */
    @RequestMapping(method = RequestMethod.GET, value = "/consultapplication")
    public ModelAndView consultApplication() {
        ModelAndView view = new ModelAndView();
        view.setViewName("/customer/event/consult");
        return view;
    }
    
	/**
	 * 查询申请结果-结果页面
	 * @param request
	 * @param phone
	 * @return
	 */
    @RequestMapping(method = RequestMethod.GET, value = "/giftapplication/{phone}")
    public ModelAndView giftApplication(HttpServletRequest request, @PathVariable("phone") String phone) {
        ModelAndView view = new ModelAndView();
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("donorPhone", phone);
        condition.put("blockFlag", false);
        ResultData fetchEventApplicationResponse = eventService.fetchEventApplication(condition);
        if (fetchEventApplicationResponse.getResponseCode() != ResponseCode.RESPONSE_OK) {
            Prompt prompt = new Prompt(PromptCode.WARNING, "提示", "您还没有参加该活动", "");
            view.addObject("prompt", prompt);
            view.setViewName("/customer/event/prompt");
            return view;
        }
        EventApplication eventApplication = ((List<EventApplication>) fetchEventApplicationResponse.getData()).get(0);
        view.addObject("eventApplication", eventApplication);
        view.setViewName("/customer/event/check");
        return view;
    }
}
