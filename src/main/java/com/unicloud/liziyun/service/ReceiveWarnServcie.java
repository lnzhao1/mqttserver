package com.unicloud.liziyun.service;

import com.unicloud.liziyun.dao.ReceiveWarnDao;
import com.unicloud.liziyun.pojo.ReceiveWarn;
import com.unicloud.liziyun.util.ObjectUtils;
import com.unicloud.liziyun.vo.SearchDataVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zhaoxiao 2020/3/10
 */
@Service(value = "receiveWarnServcie")
public class ReceiveWarnServcie {

    @Autowired
    private ReceiveWarnDao receiveWarnDao;

    public void save(ReceiveWarn receiveData){
        receiveData.setIsFixed(0);
        receiveWarnDao.save(receiveData);
    }

    public Page<ReceiveWarn> searchReceiveWarn(SearchDataVo searchDataVo){
        int page = 0;
        int size = 10;
        Pageable pageable ;
        Specification<ReceiveWarn> specification = new Specification<ReceiveWarn>() {
            @Override
            public Predicate toPredicate(Root<ReceiveWarn> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                Predicate p;
                if(!ObjectUtils.allFieldIsNULL(searchDataVo)){
                    if(StringUtils.isNotBlank(searchDataVo.getDeviceId())){
                        p = criteriaBuilder.equal(root.get("sncode").as(String.class),searchDataVo.getDeviceId());
                        predicates.add(p);
                    }
                    if(searchDataVo.getStartTime()!=null){
                        p = criteriaBuilder.greaterThan(root.get("createtime").as(Date.class),searchDataVo.getStartTime());
                        predicates.add(p);
                    }
                    if(searchDataVo.getEndTime()!=null){
                        p = criteriaBuilder.lessThan(root.get("createtime").as(Date.class),searchDataVo.getEndTime());
                        predicates.add(p);
                    }
                    if(searchDataVo.getIsFixed()!= null){
                        p = criteriaBuilder.equal(root.get("isFixed").as(Integer.class),searchDataVo.getIsFixed());
                        predicates.add(p);
                    }
                }
                return  criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        if(!ObjectUtils.allFieldIsNULL(searchDataVo)){
            if(searchDataVo.getPage()>0&&searchDataVo.getPageSize()>0){
                pageable = PageRequest.of(searchDataVo.getPage()-1,searchDataVo.getPageSize(),sort);
            }else{
                pageable = PageRequest.of(page,size,sort);
            }
        }else{
            pageable = PageRequest.of(page,size,sort);
        }
        return receiveWarnDao.findAll(specification,pageable);
    }
    /**
     * description: 查询报警
     * @Param null:
     * @return :
     * @author: zhaoxiao
     * @createTime: 2020/3/13 13:49
    */
    public List<Map> searchReceiveWarnGroup(){
        return receiveWarnDao.searchReceiveWarnGroup();
    }
}
