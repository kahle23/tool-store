package uiharu.service;

import uiharu.pojo.dto.ClientRecordDTO;
import uiharu.pojo.dto.ClientRecordDetailDTO;

/**
 * ClientRecordService.
 * @author Kahle
 * @date 2020-05-24T22:43:33.621+0800
 */
public interface ClientRecordService {

    /**
     * 通过设备ID增加或编辑客户端信息
     */
    void addOrEditByDeviceId(ClientRecordDTO clientRecordDTO);

    /**
     * 客户端详细信息注册
     */
    void register(ClientRecordDetailDTO clientRecordDetailDTO);

}
