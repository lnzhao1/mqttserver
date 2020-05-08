package com.unicloud.liziyun.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by liujiawei on 2019-04-17.
 */
@Data
@Entity
@Table(name="receive_personnel_locus",indexes = {
        @Index(name="rpl_primary_key",columnList = "id",unique = true),
        @Index(name="rpl_device_id",columnList = "device_id"),
})
public class PersonnelLocus implements Serializable {

    /**
     *      主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    /**
     *      设备ID
     */
    @Column(name = "device_id")
    private String deviceId;

    /**
     *      经度
     */
    @Column(name = "lon")
    private double lon;

    /**
     *      纬度
     */
    @Column(name = "lat")
    private double lat;

    /**
     *      对地速度
     */
    @Column(name = "speed")
    private double speed;


    /**
     *      对地航向的角度
     */
    @Column(name = "heading")
    private double heading;


    /**
     *      海拔
     */
    @Column(name = "altitude")
    private double altitude;



    /**
     *      创建时间
     */
    @Column(name = "create_date")
    private Date createDate;


    /**
     *      对地速度
     */
    @Column(name = "acy")
    private double acy;

    /**
     *      对地速度
     */
    @Column(name = "bat")
    private double bat;

}
