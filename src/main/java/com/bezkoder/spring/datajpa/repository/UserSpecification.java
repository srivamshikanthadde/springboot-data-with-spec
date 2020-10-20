package com.bezkoder.spring.datajpa.repository;


import com.bezkoder.spring.datajpa.model.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;

public class UserSpecification implements Specification<User> {

	private SpecSearchCriteria criteria;

	private final String TS = " 00:00:00";


	public UserSpecification(final SpecSearchCriteria criteria) {
		super();
		this.criteria = criteria;
	}

	public SpecSearchCriteria getCriteria() {
		return criteria;
	}

	@Override
	public Predicate toPredicate(final Root<User> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
		if(root.get(criteria.getKey()).getJavaType() == Timestamp.class){
			return toPredicateWithDate(root,query,builder);
		}
		switch (criteria.getOperation()) {
		case EQUALITY:
			return builder.equal(root.get(criteria.getKey()), criteria.getValue());
		case NEGATION:
			return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
		case GREATER_THAN:
			return builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
		case LESS_THAN:
			return builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
		case LIKE:
			return builder.like(root.get(criteria.getKey()), criteria.getValue().toString());
		case STARTS_WITH:
			return builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
		case ENDS_WITH:
			return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
		case CONTAINS:
			return builder.like(root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
		default:
			return null;
		}
	}

	public Predicate toPredicateWithDate(final Root<User> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
		switch (criteria.getOperation()) {
			case EQUALITY:
				return builder.equal(root.get(criteria.getKey()), Timestamp.valueOf(criteria.getValue().toString()+TS));
			case NEGATION:
				return builder.notEqual(root.get(criteria.getKey()), Timestamp.valueOf(criteria.getValue().toString()+TS));
			case GREATER_THAN:
				return builder.greaterThan(root.get(criteria.getKey()), Timestamp.valueOf(criteria.getValue().toString()+TS));
			case LESS_THAN:
				return builder.lessThan(root.get(criteria.getKey()), Timestamp.valueOf(criteria.getValue().toString()+TS));
			case BETWEEN:
				String[] dateArray = root.get(criteria.getValue().toString().split("/"));
				return builder.between(root.get(criteria.getKey()),
						Timestamp.valueOf(dateArray[0]+TS),Timestamp.valueOf(dateArray[1]+TS));
			default:
				return null;
		}
	}
	private Predicate toPredicateDateInBetween(final Root<User> root, final CriteriaQuery<?> query, final CriteriaBuilder builder){
		String[] dateArray = root.get(criteria.getKey().split("/");
		return builder.between(root.get(criteria.getKey()),
				Timestamp.valueOf(dateArray[0]+TS),Timestamp.valueOf(dateArray[1]+TS));
	}

}
