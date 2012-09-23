package com.breakersoft.plow.service;

import com.breakersoft.plow.Job;
import com.breakersoft.plow.event.JobLaunchEvent;
import com.breakersoft.plow.json.Blueprint;

public interface JobLauncherService {

    JobLaunchEvent launch(Blueprint blueprint);
    void shutdown(Job job);
}
