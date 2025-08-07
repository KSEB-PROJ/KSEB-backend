package com.kseb.collabtool.domain.log.repository;

import com.kseb.collabtool.domain.log.entity.ActionType;
import com.kseb.collabtool.domain.log.entity.ActivityLog;
import com.kseb.collabtool.domain.user.entity.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ActivityLogSpecification {

    public static Specification<ActivityLog> withFilter(
            String actorName,
            List<ActionType> actionTypes,
            LocalDate startDate,
            LocalDate endDate) {

        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (StringUtils.hasText(actorName)) {
                Join<ActivityLog, User> userJoin = root.join("actor");
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(userJoin.get("name"), "%" + actorName + "%"));
            }

            if (actionTypes != null && !actionTypes.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, root.get("actionType").in(actionTypes));
            }

            if (startDate != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate.atStartOfDay()));
            }

            if (endDate != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate.atTime(LocalTime.MAX)));
            }
            
            // 최신순으로 정렬
            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));

            return predicate;
        };
    }
}
