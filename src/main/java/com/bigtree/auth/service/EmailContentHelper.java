package com.bigtree.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.Locale;
import java.util.Map;

@Component
@Slf4j
public class EmailContentHelper {

    @Autowired
    SpringTemplateEngine springTemplateEngine;

    @Autowired
    @Qualifier("htmlTemplateResolver")
    ITemplateResolver templateResolver;

    private boolean templateEngineInitialized;

    public String build(String template, Map<String,Object> params) {
        if (! this.templateEngineInitialized){
            springTemplateEngine.setTemplateResolver(templateResolver);
            this.templateEngineInitialized = true;
        }
        Context context = new Context();
        context.setLocale(Locale.UK);
        if (!CollectionUtils.isEmpty(params)){
            context.setVariables(params);
        }
        return springTemplateEngine.process(template, context);
    }
}
