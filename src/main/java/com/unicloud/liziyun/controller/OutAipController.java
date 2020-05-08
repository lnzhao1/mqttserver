package com.unicloud.liziyun.controller;

import com.alibaba.fastjson.JSONObject;
import com.unicloud.liziyun.service.PersonnelLocusService;
import com.unicloud.liziyun.service.ReceiveWarnServcie;
import com.unicloud.liziyun.vo.SearchDataVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @author zhaoxiao 2020/3/11
 */
@Controller
public class OutAipController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PersonnelLocusService personnelLocusService;

    @Autowired
    private ReceiveWarnServcie receiveWarnServcie;

    /**
     * description: 查询设备定位
     * @Param params:
     * @return org.springframework.http.ResponseEntity:
     * @author: zhaoxiao
     * @createTime: 2020/3/11 17:43
    */
    @ResponseBody
    @RequestMapping(value = "/searchPersonLoc",method = RequestMethod.POST)
    public ResponseEntity searchPersonLoc(@RequestBody SearchDataVo params ){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content",personnelLocusService.searchPersonLoc(params));
            jsonObject.put("status", HttpStatus.OK);
        }catch (Exception e){
            jsonObject.put("status", "error");
            logger.warn(e.getMessage());
        }
        return ResponseEntity.ok(jsonObject);
    }

    /**
     * description: 查询设备报警
     * @Param params:
     * @return org.springframework.http.ResponseEntity:
     * @author: zhaoxiao
     * @createTime: 2020/3/11 17:45
    */
    @ResponseBody
    @RequestMapping(value = "/searchPersonWarn",method = RequestMethod.POST)
    public ResponseEntity searchPersonWarn(@RequestBody SearchDataVo params ){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content",receiveWarnServcie.searchReceiveWarnGroup());
            jsonObject.put("status", HttpStatus.OK);
        }catch (Exception e){
            jsonObject.put("status", "error");
            logger.warn(e.getMessage());
        }
        return ResponseEntity.ok(jsonObject);
    }
}
