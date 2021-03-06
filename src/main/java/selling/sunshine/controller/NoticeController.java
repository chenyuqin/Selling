package selling.sunshine.controller;

import com.alibaba.fastjson.JSON;
import common.sunshine.model.selling.notice.Notice;
import common.sunshine.utils.ResponseCode;
import common.sunshine.utils.ResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import selling.sunshine.form.notice.NoticeForm;
import selling.sunshine.service.NoticeService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 顾客商城的相关公告接口
 * Created by sunshine on 2016/12/17.
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {
    private Logger logger = LoggerFactory.getLogger(NoticeController.class);

    @Autowired
    private NoticeService noticeService;

    @RequestMapping(method = RequestMethod.GET, value = "/create")

    /**
     * 跳转到创建公告界面（未完成）
     */
    public ModelAndView create() {
        ModelAndView view = new ModelAndView();
        view.setViewName("");
        return view;
    }

    /**
     * 创建公告（未完成）
     * @param form
     * @param result
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public ModelAndView create(@Valid NoticeForm form, BindingResult result) {
        ModelAndView view = new ModelAndView();
        if (result.hasErrors()) {
            logger.error(JSON.toJSONString(result.getAllErrors()));
        }
        Notice notice = new Notice(form.getContent(), form.getLink());
        ResultData response = noticeService.createNotice(notice);
        if (response.getResponseCode() == ResponseCode.RESPONSE_OK) {

        } else {

        }
        view.setViewName("");
        return view;
    }

    /**
     * 跳转到公告概览界面（未完成）
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/overview")
    public ModelAndView overview() {
        ModelAndView view = new ModelAndView();
        view.setViewName("/backend/notice/overview");
        return view;
    }

    /**
     * 得到公告列表
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/list")
    public ResultData list() {
        ResultData result = new ResultData();
        Map<String, Object> condition = new HashMap<>();
        ResultData response = noticeService.fetchNotice(condition);
        if (response.getResponseCode() == ResponseCode.RESPONSE_OK) {
            result.setData(response.getData());
        } else if (response.getResponseCode() == ResponseCode.RESPONSE_NULL) {
            result.setResponseCode(ResponseCode.RESPONSE_NULL);
            result.setDescription("当前暂无相关公告");
        } else {
            result.setResponseCode(ResponseCode.RESPONSE_ERROR);
            result.setDescription(response.getDescription());
        }
        return result;
    }
}
