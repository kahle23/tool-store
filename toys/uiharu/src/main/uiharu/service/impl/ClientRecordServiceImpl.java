package uiharu.service.impl;

import artoria.beans.BeanUtils;
import artoria.exception.VerifyUtils;
import artoria.user.UserInfo;
import artoria.user.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uiharu.persistence.entity.ClientRecord;
import uiharu.persistence.mapper.ClientRecordMapper;
import uiharu.pojo.dto.ClientRecordDTO;
import uiharu.pojo.dto.ClientRecordDetailDTO;
import uiharu.pojo.dto.DeviceRecordDTO;
import uiharu.service.ClientRecordService;
import uiharu.service.DeviceRecordService;

import java.util.Date;

import static artoria.common.Constants.*;
import static uiharu.common.UiharuErrorCode.*;

/**
 * ClientRecordServiceImpl.
 * @author Kahle
 * @date 2020-05-24T22:43:33.621+0800
 */
@Slf4j
@Service
public class ClientRecordServiceImpl implements ClientRecordService {

    @Autowired
    private ClientRecordMapper clientRecordMapper;
    @Autowired
    private DeviceRecordService deviceRecordService;

    @Override
    public void addOrEditByDeviceId(ClientRecordDTO clientRecordDTO) {
        VerifyUtils.notNull(clientRecordDTO, CLIENT021);
        String deviceId = clientRecordDTO.getDeviceId();
        VerifyUtils.notBlank(deviceId, DEVICE021);
        ClientRecord clientRecord =
                BeanUtils.beanToBean(clientRecordDTO, ClientRecord.class);
        UserInfo userInfo = UserUtils.getUserInfo();
        String loginId = userInfo != null ? userInfo.getId() : SYSTEM;
        Date nowDate = new Date();
        // 判断是否存在
//        ClientRecord countQuery = new ClientRecord();
//        countQuery.setDeviceId(deviceId);
//        int count = clientRecordMapper.countSelective(countQuery);
        // 注册时间
        clientRecord.setRegisterTime(nowDate);
        // 更新人、更新时间
        clientRecord.setUpdateUser(loginId);
        clientRecord.setUpdateDate(nowDate);
//        if (count > ZERO) {
//            // 已存在，走更新
//            clientRecord.setCreateUser(null);
//            clientRecord.setCreateDate(null);
//            int update = clientRecordMapper.updateByDeviceId(clientRecord);
//            VerifyUtils.isTrue(update == ONE, CLIENT023);
//        }
//        else {
        // 不存在，走新增
        // 创建人、创建时间
        clientRecord.setCreateUser(loginId);
        clientRecord.setCreateDate(nowDate);
        int insert = clientRecordMapper.insertSelective(clientRecord);
        VerifyUtils.isTrue(insert == ONE, CLIENT022);
//        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(ClientRecordDetailDTO clientRecordDetailDTO) {
        VerifyUtils.notNull(clientRecordDetailDTO, CLIENT021);
        // 数据
        ClientRecordDTO clientRecordDTO =
                BeanUtils.beanToBean(clientRecordDetailDTO, ClientRecordDTO.class);
        UserInfo userInfo = UserUtils.getUserInfo();
        String userName = userInfo != null ? (String) userInfo.get("kpName") : SYSTEM;
        String loginId = userInfo != null ? userInfo.getId() : SYSTEM;
        Date nowDate = new Date();
        // 处理设备信息
        DeviceRecordDTO deviceRecordDTO = new DeviceRecordDTO();
        deviceRecordDTO.setId(clientRecordDetailDTO.getDeviceId());
        deviceRecordDTO.setName(clientRecordDetailDTO.getDeviceName());
        deviceRecordDTO.setModel(clientRecordDetailDTO.getDeviceModel());
        deviceRecordDTO.setManufacturer(clientRecordDetailDTO.getDeviceManufacturer());
        deviceRecordService.addOrEditByDeviceId(deviceRecordDTO);
        // 处理客户端信息
        clientRecordDTO.setUserId(SYSTEM.equals(loginId) ? EMPTY_STRING : loginId);
        clientRecordDTO.setUserName(SYSTEM.equals(userName) ? EMPTY_STRING : userName);
        addOrEditByDeviceId(clientRecordDTO);
    }


}
