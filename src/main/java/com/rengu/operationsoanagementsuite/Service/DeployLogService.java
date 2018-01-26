package com.rengu.operationsoanagementsuite.Service;


import com.rengu.operationsoanagementsuite.Entity.DeployLogEntity;
import com.rengu.operationsoanagementsuite.Entity.UserEntity;
import com.rengu.operationsoanagementsuite.Repository.DeployLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DeployLogService {

    @Autowired
    private DeployLogRepository deployLogRepository;
    // 根据查询类型type和关键字key查询日志记录
    @Transactional
    public List<DeployLogEntity> getLogs(UserEntity loginUser,Date stime, Date etime, DeployLogEntity deployLogArgs) {
        return deployLogRepository.findAll(new Specification<DeployLogEntity>() {
            @Override
            public Predicate toPredicate(Root<DeployLogEntity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                // 根据工程名称查询
                if(deployLogArgs.getProjectEntity().getName() != null)
                {
                    predicates.add(criteriaBuilder.like(root.get("projectEntity").get("name").as(String.class),"%"+deployLogArgs.getProjectEntity().getName()+"%"));
                }

                // 根据时间查询
                if(stime != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.<Date>get("create_time").as(Date.class), stime));
                }
                if(etime != null){
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.<Date>get("create_time").as(Date.class), etime));
                }

                // 根据设备IP地址判断
                if(deployLogArgs.getDeviceEntity().getIp() != null)
                {
                    predicates.add(criteriaBuilder.like(root.get("deviceEntity").get("ip").as(String.class),"%"+deployLogArgs.getDeviceEntity().getIp()+"%"));
                }

                // 根据设备名称查询
                if(deployLogArgs.getDeviceEntity().getName() != null)
                {
                    predicates.add(criteriaBuilder.like(root.get("deviceEntity").get("name").as(String.class),"%"+deployLogArgs.getDeviceEntity().getName()+"%"));
                }

                // 根据软件名称查询
                if( deployLogArgs.getComponentEntity().getName() != null)
                {
                    predicates.add(criteriaBuilder.like(root.get("componentEntity").get("name").as(String.class),"%"+deployLogArgs.getComponentEntity().getName()+"%"));
                }

                // 联表查询
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }
}
