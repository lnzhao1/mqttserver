package com.unicloud.liziyun.service;

import com.unicloud.liziyun.dao.DeviceAgentDao;
import com.unicloud.liziyun.pojo.DeviceAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhaoxiao 2020/3/2
 */
@Service
public class DeviceAgentService {

    @Autowired
    private DeviceAgentDao deviceAgentDao;

    public List<DeviceAgent> findBySN(String sncode){
        return deviceAgentDao.findBySncode(sncode);
    }

    public DeviceAgent save(DeviceAgent deviceAgent){
        return deviceAgentDao.save(deviceAgent);
    }

    public void deleteAll(List<DeviceAgent> deviceAgents){
         deviceAgentDao.deleteAll(deviceAgents);
    }
}
