package uiharu.service;

import uiharu.pojo.dto.DeviceRecordDTO;

/**
 * DeviceRecordService.
 * @author Kahle
 * @date 2020-05-24T22:43:47.965+0800
 */
public interface DeviceRecordService {

    /**
     * 判断设备是否存在
     */
    boolean existDevice(DeviceRecordDTO deviceRecordDTO);

    /**
     * 通过设备ID增加或编辑设备记录
     */
    void addOrEditByDeviceId(DeviceRecordDTO deviceRecordDTO);

}
