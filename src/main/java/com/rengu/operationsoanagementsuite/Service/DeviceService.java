package com.rengu.operationsoanagementsuite.Service;

import com.rengu.operationsoanagementsuite.Entity.DeviceEntity;
import com.rengu.operationsoanagementsuite.Exception.CustomizeException;
import com.rengu.operationsoanagementsuite.Repository.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DeviceService {

    // 引入日志记录类
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DeviceRepository deviceRepository;

    // 新增设备
    @Transactional
    public DeviceEntity saveDevice(DeviceEntity deviceEntity) throws MissingServletRequestParameterException {
        // 检查设备名称参数是否存在
        if (StringUtils.isEmpty(deviceEntity.getName())) {
            logger.info("请求参数解析异常：device.name不存在，保存失败。");
            throw new MissingServletRequestParameterException("device.name", "String");
        }
        // 检查设备ip参数是否存在
        if (StringUtils.isEmpty(deviceEntity.getIp())) {
            logger.info("请求参数解析异常：device.ip不存在，保存失败。");
            throw new MissingServletRequestParameterException("device.ip", "String");
        }
        // 检查Ip是否已经存在
        if (deviceRepository.findByIp(deviceEntity.getIp()) != null) {
            logger.info("设备IP：" + deviceEntity.getIp() + "已经存在，保存失败。");
            throw new CustomizeException("设备IP：" + deviceEntity.getIp() + "已经存在，保存失败。");
        }
        deviceEntity.setLastModified(new Date());
        return deviceRepository.save(deviceEntity);
    }

    // 删除设备
    @Transactional
    public void deleteDevice(String deviceId) throws MissingServletRequestParameterException {
        // 检查设备id参数是否存在
        if (StringUtils.isEmpty(deviceId)) {
            logger.info("请求参数解析异常：device.id不存在，删除失败。");
            throw new MissingServletRequestParameterException("device.id", "String");
        }
        // 检查设备id是否存在
        if (!deviceRepository.exists(deviceId)) {
            logger.info("请求参数不正确：id为：" + deviceId + "的设备不存在，删除失败。");
            throw new CustomizeException("请求参数不正确：id为：" + deviceId + "的设备不存在，删除失败。");
        }
        deviceRepository.delete(deviceId);
    }

    // 更新设备
    @Transactional
    public DeviceEntity updateDevice(String deviceId, DeviceEntity deviceArgs) throws MissingServletRequestParameterException {
        // 检查设备id参数是否存在
        if (StringUtils.isEmpty(deviceId)) {
            logger.info("请求参数解析异常：device.id不存在，更新失败。");
            throw new MissingServletRequestParameterException("device.id", "String");
        }
        // 检查设备id是否存在
        if (!deviceRepository.exists(deviceId)) {
            logger.info("请求参数不正确：id为：" + deviceId + "的设备不存在，更新失败。");
            throw new CustomizeException("请求参数不正确：id为：" + deviceId + "的设备不存在，更新失败。");
        }
        DeviceEntity deviceEntity = deviceRepository.findOne(deviceId);
        BeanUtils.copyProperties(deviceArgs, deviceEntity, "id");
        deviceEntity.setLastModified(new Date());
        return deviceRepository.save(deviceEntity);
    }

    // 查询设备
    public DeviceEntity getDevice(String deviceId) throws MissingServletRequestParameterException {
        // 检查设备id参数是否存在
        if (StringUtils.isEmpty(deviceId)) {
            logger.info("请求参数解析异常：device.id不存在，查询失败。");
            throw new MissingServletRequestParameterException("device.id", "String");
        }
        return deviceRepository.findOne(deviceId);
    }

    // 查询设备
    public List<DeviceEntity> getDevices(DeviceEntity deviceArgs) {
        return deviceRepository.findAll((root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (deviceArgs.getName() != null) {
                predicateList.add(cb.like(root.get("name"), deviceArgs.getName()));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        });
    }
}