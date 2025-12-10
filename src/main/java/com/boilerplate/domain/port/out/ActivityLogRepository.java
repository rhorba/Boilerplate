package com.boilerplate.domain.port.out;

import com.boilerplate.domain.model.ActivityLog;
import java.util.List;

public interface ActivityLogRepository {
    ActivityLog save(ActivityLog log);

    List<ActivityLog> findAll();
}
