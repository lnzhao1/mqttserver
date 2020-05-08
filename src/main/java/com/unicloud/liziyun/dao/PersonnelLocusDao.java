package com.unicloud.liziyun.dao;

import com.unicloud.liziyun.pojo.PersonnelLocus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author zhaoxiao 2020/3/11
 */
public interface PersonnelLocusDao extends JpaRepository<PersonnelLocus,Integer>, JpaSpecificationExecutor<PersonnelLocus> {
}
