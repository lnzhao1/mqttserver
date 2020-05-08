package com.unicloud.liziyun.controller;


import com.alibaba.fastjson.JSONObject;
import com.unicloud.liziyun.pojo.DeviceAgent;
import com.unicloud.liziyun.service.DeviceAgentService;
import com.unicloud.liziyun.util.PassWordUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 此类为定制化需求http做的用户注册等，无关mqtt
 * @author zhaoxiao 2020/2/27
 */
@Controller
public class registerController {

    @Autowired
    private DeviceAgentService deviceAgentService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * description: 1、	创建请求
     * @Param :
     * @return com.alibaba.fastjson.JSONObject:
     * @author: zhaoxiao
     * @createTime: 2020/3/2 17:04
    */
    @ResponseBody
    @RequestMapping(value = "/devicecontrol/newDeviceRequests",method = RequestMethod.POST)
    public ResponseEntity newDeviceRequests(@RequestBody Map param){
        JSONObject jsonObject = new JSONObject();
        try {
            if(null != param.get("id")){
                List<DeviceAgent> deviceAgents = deviceAgentService.findBySN(param.get("id").toString());
                if(deviceAgents.size() >0){
                    deviceAgentService.deleteAll(deviceAgents);
                }
            }
            jsonObject.put("id", param.get("id"));
            jsonObject.put("self","");
            jsonObject.put("status","WAITING_FOR_CONNECTION");
            jsonObject.put("tenantId","pjjh");
        }catch (Exception e){
            logger.warn(e.getMessage());
        }
        return  new ResponseEntity(jsonObject,HttpStatus.CREATED);
    }
    /**
     * description: 2、	创建设备凭证
     * @Param :
     * @return com.alibaba.fastjson.JSONObject:
     * @author: zhaoxiao
     * @createTime: 2020/3/2 17:05
    */
    @ResponseBody
    @RequestMapping(value = "/devicecontrol/deviceCredentials",method = RequestMethod.POST)
    public ResponseEntity deviceCredentials(@RequestBody Map param){
        //// TODO: 2020/3/2 查询数据库里有无数据  没有：返404 有 返值//建张表搞定帐号密码，登录时验证
        List<DeviceAgent> deviceAgents = deviceAgentService.findBySN(param.get("id").toString());
        JSONObject jsonObject = new JSONObject();
        try {
            if(deviceAgents.size() == 0){
                DeviceAgent deviceAgent = new DeviceAgent();
                deviceAgent.setPassword(PassWordUtil.genRandomPwd(7));
                deviceAgent.setAccount("device_"+param.get("id").toString());
                deviceAgent.setSncode(param.get("id").toString());
                deviceAgentService.save(deviceAgent);
                jsonObject.put("error","devicecontrol/Not Found");
                jsonObject.put("info","");
                jsonObject.put("message","Credentials for device id "+ param.get("id").toString() +" are not available. Device" +
                        " is in state PENDING_ACCEPTANCE, (not ACCEPTED)).");
                return  new ResponseEntity(jsonObject,HttpStatus.NOT_FOUND);
            }else {
                DeviceAgent deviceAgent = deviceAgents.get(0);
                jsonObject.put("id",deviceAgent.getSncode());
                jsonObject.put("password",deviceAgent.getPassword());
                jsonObject.put("self",""+param.get("id"));
                jsonObject.put("tenantId","pjjh");
                jsonObject.put("username",deviceAgent.getAccount());
                return  new ResponseEntity(jsonObject,HttpStatus.CREATED);
            }
        }catch (Exception e){
            logger.warn(e.getMessage());
        }
        return  new ResponseEntity(jsonObject,HttpStatus.NOT_FOUND);
    }

    /**
     * description:3、	更新状态
     * @Param :
     * @return com.alibaba.fastjson.JSONObject:
     * @author: zhaoxiao
     * @createTime: 2020/3/2 17:05
     */
    @ResponseBody
    @RequestMapping(value = "/devicecontrol/newDeviceRequests/{id}",method = RequestMethod.PUT)
    public ResponseEntity newDeviceRequests(@PathVariable String id, @RequestBody Map status){
        JSONObject jsonObject = new JSONObject();
        try {
            if("ACCEPTED".equals(status.get("status"))){
                jsonObject.put("status","ACCEPTED");
            }
            return  new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            logger.warn(e.getMessage());
        }
        return  new ResponseEntity(HttpStatus.OK);
    }
    
    /**
     * description: 5、	创建Agent：（使用 获取的用户名和密码）
     * @Param params:
     * @return org.springframework.http.ResponseEntity:
     * @author: zhaoxiao
     * @createTime: 2020/3/2 18:21
    */
    @ResponseBody
    @RequestMapping(value = "/inventory/managedObjects",method = RequestMethod.POST)
    public ResponseEntity managedObjects(@RequestBody Map params){
        try {
            String snCode  =  params.get("name").toString();
            List<DeviceAgent> deviceAgent = deviceAgentService.findBySN(snCode);
            /// TODO: 2020/3/2 生成系统内唯一id 2048
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",deviceAgent.get(0).getId()+"");
            return  new ResponseEntity(jsonObject,HttpStatus.CREATED);
        }catch (Exception e){
            logger.warn(e.getMessage());
        }
        return  new ResponseEntity(HttpStatus.CREATED);
    } 
    
    /**
     * description: 6、	创建外部ID
     * @Param sysId: "externalId": "355910044336974","type": "c8y_WorkCard"
     * @Param params:
     * @return org.springframework.http.ResponseEntity:
     * @author: zhaoxiao
     * @createTime: 2020/3/2 18:25
    */
    @ResponseBody
    @RequestMapping(value = "/identity/globalIds/{sysId}/externalIds",method = RequestMethod.POST)
    public ResponseEntity externalIds(@PathVariable String sysId,@RequestBody Map params){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("externalId",params.get("externalId"));
            HashMap<String,String> hashMap = new HashMap();
            hashMap.put("id",sysId);
            hashMap.put("self",""+sysId);
            jsonObject.put("managedObject",hashMap);
            jsonObject.put("self",""+params.get("externalId"));
            jsonObject.put("type",hashMap.get("type"));
            return  new ResponseEntity(jsonObject,HttpStatus.CREATED);
        }catch (Exception e){
            jsonObject.put("msg",e.getMessage());
            logger.warn(e.getMessage());
        }
        return  new ResponseEntity(HttpStatus.OK);
    }
}
