package com.unicloud.liziyun.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author zhaoxiao 2020/3/2
 */
@Data
@Entity
@Table(name="device_agent",indexes = {
        @Index(name="device_agent_primary_key",columnList = "id",unique = true)
})
public class DeviceAgent implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "sncode")
    private String sncode;

    @Column(name = "account")
    private String account;

    @Column(name = "password")
    private String password;
}
