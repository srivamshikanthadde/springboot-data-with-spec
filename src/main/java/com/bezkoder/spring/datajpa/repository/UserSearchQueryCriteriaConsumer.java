package com.bezkoder.spring.datajpa.repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.function.Consumer;

public class UserSearchQueryCriteriaConsumer implements Consumer<SearchCriteria>{

    private Predicate predicate;
    private CriteriaBuilder builder;
    private Root r;
    private final String TS = " 00:00:00";
    
    public UserSearchQueryCriteriaConsumer(Predicate predicate, CriteriaBuilder builder, Root r) {
        super();
        this.predicate = predicate;
        this.builder = builder;
        this.r= r;
    }

    @Override
    public void accept(SearchCriteria param) {
        if (r.get(param.getKey()).getJavaType() == Timestamp.class) {
            acceptWithDate(param);
        }
        else {
            if (param.getOperation().equalsIgnoreCase(">")) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(r.get(param.getKey()), param.getValue().toString()));
            } else if (param.getOperation().equalsIgnoreCase("<")) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(r.get(param.getKey()), param.getValue().toString()));
            } else if (param.getOperation().equalsIgnoreCase(":")) {
                if (r.get(param.getKey()).getJavaType() == String.class) {
                    predicate = builder.and(predicate, builder.like(r.get(param.getKey()), "%" + param.getValue() + "%"));
                } else {
                    predicate = builder.and(predicate, builder.equal(r.get(param.getKey()), param.getValue()));
                }
            }
        }
    }

    private void acceptWithDate(SearchCriteria param){
        if (param.getOperation().equalsIgnoreCase(">")) {
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(r.get(param.getKey()), Timestamp.valueOf(param.getValue().toString()+TS)));
        } else if (param.getOperation().equalsIgnoreCase("<")) {
            predicate = builder.and(predicate, builder.lessThanOrEqualTo(r.get(param.getKey()), Timestamp.valueOf(param.getValue().toString()+TS)));
        } else if (param.getOperation().equalsIgnoreCase(":")) {
                predicate = builder.and(predicate, builder.equal(r.get(param.getKey()), Timestamp.valueOf(param.getValue().toString()+TS)));

        }
    }

    public Predicate getPredicate() {
        return predicate;
    }
}