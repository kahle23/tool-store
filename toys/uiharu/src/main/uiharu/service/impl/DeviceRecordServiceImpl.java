package uiharu.service.impl;

import artoria.beans.BeanUtils;
import artoria.data.Device;
import artoria.exception.VerifyUtils;
import artoria.user.UserInfo;
import artoria.user.UserUtils;
import artoria.util.StringUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import misaka.device.DeviceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uiharu.persistence.entity.DeviceRecord;
import uiharu.persistence.mapper.DeviceRecordMapper;
import uiharu.pojo.dto.DeviceRecordDTO;
import uiharu.service.DeviceRecordService;

import java.util.Date;

import static artoria.common.Constants.*;
import static uiharu.common.UiharuErrorCode.*;

/**
 * DeviceRecordServiceImpl.
 * @author Kahle
 * @date 2020-05-24T22:43:47.965+0800
 */
@Slf4j
@Service
public class DeviceRecordServiceImpl implements DeviceRecordService {

    @Autowired
    private DeviceRecordMapper deviceRecordMapper;

    @Override
    public boolean existDevice(DeviceRecordDTO deviceRecordDTO) {
        VerifyUtils.notNull(deviceRecordDTO, DEVICE020);
        String id = deviceRecordDTO.getId();
        VerifyUtils.notBlank(id, DEVICE021);
        DeviceRecord count = new DeviceRecord();
        count.setId(id);
        return deviceRecordMapper.countSelective(count) > ZERO;
    }

    @Override
    public void addOrEditByDeviceId(DeviceRecordDTO deviceRecordDTO) {
        VerifyUtils.notNull(deviceRecordDTO, DEVICE020);
        UserInfo userInfo = UserUtils.getUserInfo();
        String loginId = userInfo != null ? userInfo.getId() : SYSTEM;
        Date nowDate = new Date();
        DeviceRecord deviceRecord =
                BeanUtils.beanToBean(deviceRecordDTO, DeviceRecord.class);
        // 校验
        String id = deviceRecord.getId();
        VerifyUtils.notBlank(id, DEVICE021);
        String model = deviceRecord.getModel();
        VerifyUtils.notBlank(model, DEVICE022);
        // 根据型号查询信息
        Device device = DeviceUtils.findByDeviceModel(model);
        if (device != null) {
            log.info("Device info: {}", JSON.toJSONString(device));
            DeviceRecord record = BeanUtils.beanToBean(device, DeviceRecord.class);
            record.setId(deviceRecord.getId());
            record.setModel(deviceRecord.getModel());
            if (StringUtils.isBlank(record.getName())) {
                record.setName(deviceRecord.getName());
            }
            if (StringUtils.isBlank(record.getManufacturer())) {
                record.setManufacturer(deviceRecord.getManufacturer());
            }
            deviceRecord = record;
        }
        // 更新人、更新时间
        deviceRecord.setUpdateUser(loginId);
        deviceRecord.setUpdateDate(nowDate);
        // 注册时间
        deviceRecord.setRegisterTime(nowDate);
        if (existDevice(deviceRecordDTO)) {
            // 更新逻辑
            deviceRecord.setCreateUser(null);
            deviceRecord.setCreateDate(null);
            int update = deviceRecordMapper.updateByPrimaryKeySelective(deviceRecord);
            VerifyUtils.isTrue(update == ONE, DEVICE024);
        }
        else {
            // 新增逻辑
            deviceRecord.setCreateUser(loginId);
            deviceRecord.setCreateDate(nowDate);
            int insert = deviceRecordMapper.insertSelective(deviceRecord);
            VerifyUtils.isTrue(insert == ONE, DEVICE023);
        }
    }

}
