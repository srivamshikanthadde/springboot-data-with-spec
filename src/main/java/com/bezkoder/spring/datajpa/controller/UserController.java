package com.bezkoder.spring.datajpa.controller;


import com.bezkoder.spring.datajpa.model.User;
import com.bezkoder.spring.datajpa.repository.*;
import com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@EnableSpringDataWebSupport
@Controller
@RequestMapping(value = "/auth/")
public class UserController {

    @Autowired
    private IUserDAO service;

    @Autowired
    private UserRepository dao;



    public UserController() {
        super();
    }

    // API - READ

    @RequestMapping(method = RequestMethod.GET, value = "/users")
    @ResponseBody
    public List<User> search(@RequestParam(value = "search", required = false) String search) {
        List<SearchCriteria> params = new ArrayList<SearchCriteria>();
        if (search != null) {
            //Pattern pattern = Pattern.compile("(\\p{Punct}?)(\\w+?)(:|<|>)(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");

           Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)([\\w/-]+),");
           // if(search.contains("createdDate")){
                //Pattern  pattern = Pattern.compile("(\\w+?)(:|<|>)([A-Za-z0-9_-]),");
           // }
            Matcher matcher = pattern.matcher(search + ",");
            while (matcher.find()) {
                params.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
            }
        }
        return service.searchUser(params);
    }



    @RequestMapping(method = RequestMethod.GET, value = "/users/spec")
    @ResponseBody
    public List<User> findAllBySpecification(@RequestParam(value = "search") String search) {
        UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
        String operationSetExper = Joiner.on("|")
            .join(SearchOperation.SIMPLE_OPERATION_SET);
        Pattern pattern = Pattern.compile("(\\w+?)(" + operationSetExper + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(4), matcher.group(3), matcher.group(5));
        }

        Specification<User> spec = builder.build();
        return dao.findAll(spec);
    }

    @GetMapping(value = "/users/espec")
    @ResponseBody
    public List<User> findAllByOrPredicate(@RequestParam(value = "search") String search) {
        Specification<User> spec = resolveSpecification(search);
        return dao.findAll(spec);
    }

    @GetMapping(value = "/users/spec/adv")
    @ResponseBody
    public List<User> findAllByAdvPredicate(@RequestParam(value = "search") String search) {
        Specification<User> spec = resolveSpecificationFromInfixExpr(search);
        return dao.findAll(spec);
    }

    protected Specification<User> resolveSpecificationFromInfixExpr(String searchParameters) {
        CriteriaParser parser = new CriteriaParser();
        GenericSpecificationsBuilder<User> specBuilder = new GenericSpecificationsBuilder<>();
        return specBuilder.build(parser.parse(searchParameters), UserSpecification::new);
    }

    protected Specification<User> resolveSpecification(String searchParameters) {

        UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
        String operationSetExper = Joiner.on("|")
            .join(SearchOperation.SIMPLE_OPERATION_SET);
        Pattern pattern = Pattern.compile("(\\p{Punct}?)(\\w+?)(" + operationSetExper + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");
        Matcher matcher = pattern.matcher(searchParameters + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(5), matcher.group(4), matcher.group(6));
        }
        return builder.build();
    }


}
