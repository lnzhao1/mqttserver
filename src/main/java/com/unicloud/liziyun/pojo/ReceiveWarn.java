package com.unicloud.liziyun.pojo;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author zhaoxiao 2020/3/10
 */

@Data
@Entity
@Table(name="receive_warn",indexes = {
        @Index(name="receive_warn_primary_key",columnList = "id",unique = true),
        @Index(name="receive_warn_sn_code_index",columnList = "sn_code")
})
public class ReceiveWarn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "status")
    private String status;

    @Column(name = "type")
    private String type;

    @Column(name = "warn_type_desc")
    private String warnTypeDesc;

    @Column(name = "key_name")
    private String keyname;

    @Column(name = "real_name")
    private String realname;

    @Column(name = "value")
    private String value;

    @Column(name = "sn_code")
    private String sncode;

    @Column(name = "create_time")
    private Date createtime;
    /**
     * 0未处理，1已处理
     */
    @Column(name = "is_fixed")
    private Integer isFixed;
}
