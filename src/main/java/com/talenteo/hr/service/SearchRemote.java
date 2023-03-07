package com.talenteo.hr.service;

import com.talenteo.hr.client.SearchMsClient;
import com.talenteo.hr.model.entity.HumanResource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SearchRemote {

    private final SearchMsClient searchMsClient;

    public void indexHumanResource(HumanResource humanResource) {
        searchMsClient.indexHumanResource(humanResource);
    }

}
