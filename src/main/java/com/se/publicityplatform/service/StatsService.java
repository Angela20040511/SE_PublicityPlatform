package com.se.publicityplatform.service;

import com.se.publicityplatform.mapper.PublicityAssetMapper;
import com.se.publicityplatform.mapper.PublicityRequestMapper;
import com.se.publicityplatform.mapper.PublicityTaskMapper;
import com.se.publicityplatform.vo.StatsSummary;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

    private final PublicityRequestMapper requestMapper;
    private final PublicityTaskMapper taskMapper;
    private final PublicityAssetMapper assetMapper;

    public StatsService(PublicityRequestMapper requestMapper, PublicityTaskMapper taskMapper, PublicityAssetMapper assetMapper) {
        this.requestMapper = requestMapper;
        this.taskMapper = taskMapper;
        this.assetMapper = assetMapper;
    }

    public StatsSummary summary() {
        StatsSummary summary = new StatsSummary();
        summary.setRequestCount(requestMapper.countAll());
        summary.setTaskCount(taskMapper.countAll());
        summary.setCompletedTaskCount(taskMapper.countByStatus("completed"));
        summary.setOverdueTaskCount(taskMapper.countOverdueOpenTasks());
        summary.setRevisionTaskCount(taskMapper.countByStatus("revision_required"));
        summary.setArchivedAssetCount(assetMapper.countAll());
        return summary;
    }
}
