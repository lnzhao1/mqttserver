package com.unicloud.liziyun.dao;

import com.unicloud.liziyun.pojo.DeviceAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author zhaoxiao 2020/3/2
 */
public interface DeviceAgentDao extends JpaRepository<DeviceAgent,Integer>, JpaSpecificationExecutor<DeviceAgent> {

    List<DeviceAgent> findBySncode(String sncode);
}
