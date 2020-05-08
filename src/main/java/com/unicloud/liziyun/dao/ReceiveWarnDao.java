package com.unicloud.liziyun.dao;


import com.unicloud.liziyun.pojo.ReceiveWarn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author zhaoxiao 2020/3/10
 */
public interface ReceiveWarnDao  extends JpaRepository<ReceiveWarn,Integer>, JpaSpecificationExecutor<ReceiveWarn>{
    /**
     * description: 查询活动报警
     * @Param :
     * @return java.util.List<com.unicloud.liziyun.pojo.ReceiveWarn>:
     * @author: zhaoxiao
     * @createTime: 2020/3/13 13:47
    */
    @Query(value = "select max(s.id) as id,s.sn_code as sncode,max(s.create_time)as createtime,s.type,s.is_fixed " +
            "as isFixed  from receive_warn s where  s.is_fixed <>1 group by s.sn_code,s.type,s.is_fixed",nativeQuery = true)
    List<Map> searchReceiveWarnGroup();
}
