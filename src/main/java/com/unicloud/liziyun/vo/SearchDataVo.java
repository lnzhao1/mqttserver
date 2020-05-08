package com.unicloud.liziyun.vo;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author zhaoxiao 2020/3/11
 */
@Data
public class SearchDataVo {

    /**
     * 设备sn
     */
    private String deviceId;

    private Date startTime;

    private Date endTime;

    /**
     *       当前页数
     */
    private int page;

    /**
     *      每页记录数
     */
    private int pageSize;

    /**
     *      是否处理
     */
    private Integer isFixed;
}
