package uiharu.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 客户端详细记录.
 * @author Egg Xiao Er Team
 * @date 2020-05-07T17:09:41.978+0800
 */
@Data
public class ClientRecordDetailDTO implements Serializable {

    /**
     * ID
     */
    private String id;
    /**
     * 设备
     */
    private String deviceId;
    /**
     * 名称
     */
    private String deviceName;
    /**
     * 型号
     */
    private String deviceModel;
    /**
     * 厂商
     */
    private String deviceManufacturer;
    /**
     * 系统名称
     */
    private String systemName;
    /**
     * 系统版本
     */
    private String systemVersion;
    /**
     * 系统位数
     */
    private String systemBits;
    /**
     * 应用
     */
    private String appId;
    /**
     * 名称
     */
    private String appName;
    /**
     * 类型
     */
    private String appType;
    /**
     * 版本
     */
    private String appVersion;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * 描述
     */
    private String description;

}
