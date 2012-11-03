package com.breakersoft.plow.test.thrift.dao;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;

import com.breakersoft.plow.event.JobLaunchEvent;
import com.breakersoft.plow.service.JobService;
import com.breakersoft.plow.test.AbstractTest;
import com.breakersoft.plow.thrift.JobSpecT;
import com.breakersoft.plow.thrift.dao.ThriftLayerDao;

public class ThriftLayerDaoTests extends AbstractTest {

    @Resource
    JobService jobService;

    @Resource
    ThriftLayerDao thriftLayerDao;

    @Test
    public void getLayers() {
        JobSpecT jobSpec = getTestJobSpec();
        JobLaunchEvent event = jobService.launch(jobSpec);
        assertEquals(1, thriftLayerDao.getLayers(event.getJob().getJobId()).size());
    }
}
