package org.example.inventory_service.Util;

import jakarta.persistence.criteria.Predicate;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPRef;
import org.example.inventory_service.Entity.InventoryEntity;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class InventorySpecification {
    public static Specification<InventoryEntity> filter(
            String source,
            String destination,
            Date fromDate,
            Date toDate
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (source != null && !source.isBlank()) {
                predicates.add(
                        cb.equal(root.get("source"), source)
                );
            }

            if (destination != null && !destination.isBlank()) {
                predicates.add(
                        cb.equal(root.get("destination"), destination)
                );
            }

            if (fromDate != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("flightDate"), fromDate
                        )
                );
            }

            if (toDate != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("flightDate"), toDate
                        )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
