package com.breakersoft.plow.test.dispatcher.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.breakersoft.plow.ExitStatus;
import com.breakersoft.plow.Signal;
import com.breakersoft.plow.Task;
import com.breakersoft.plow.dispatcher.DispatchService;
import com.breakersoft.plow.dispatcher.NodeDispatcher;
import com.breakersoft.plow.dispatcher.dao.DispatchDao;
import com.breakersoft.plow.dispatcher.dao.DispatchTaskDao;
import com.breakersoft.plow.dispatcher.dao.ProcDao;
import com.breakersoft.plow.dispatcher.domain.DispatchJob;
import com.breakersoft.plow.dispatcher.domain.DispatchNode;
import com.breakersoft.plow.dispatcher.domain.DispatchProc;
import com.breakersoft.plow.dispatcher.domain.DispatchResult;
import com.breakersoft.plow.dispatcher.domain.DispatchTask;
import com.breakersoft.plow.event.JobLaunchEvent;
import com.breakersoft.plow.rnd.thrift.RunTaskCommand;
import com.breakersoft.plow.test.AbstractTest;
import com.breakersoft.plow.thrift.TaskState;

public class DispatchTaskDaoTests extends AbstractTest {

    @Resource
    DispatchTaskDao dispatchTaskDao;

    @Resource
    DispatchDao dispatchDao;

    @Resource
    ProcDao procDao;

    @Resource
    NodeDispatcher nodeDispatcher;

    @Resource
    DispatchService dispatchService;

    DispatchNode node;
    DispatchJob job;
    List<DispatchTask> tasks;

    @Before
    public void before() {
        node = dispatchDao.getDispatchNode(
                nodeService.createNode(getTestNodePing()).getName());
        JobLaunchEvent event = jobService.launch(getTestJobSpec());
        job = new DispatchJob(event.getJob());
        tasks = dispatchTaskDao.getDispatchableTasks(job, node, 10);
    }

    @Test
    public void testStart() {
        assertTrue(dispatchTaskDao.reserve(tasks.get(0)));
        DispatchProc proc = dispatchService.allocateProc(node, tasks.get(0));
        assertTrue(dispatchTaskDao.start(tasks.get(0), proc));
    }

    @Test
    public void testStop() {
        dispatchTaskDao.stop(tasks.get(0), TaskState.SUCCEEDED, ExitStatus.SUCCESS, Signal.NORMAL);
    }

    @Test
    public void testStopWithAbort() {
        dispatchTaskDao.reserve(tasks.get(0));
        DispatchProc proc = dispatchService.allocateProc(node, tasks.get(0));
        assertEquals(-1, jdbc().queryForInt("SELECT int_retry FROM plow.task WHERE pk_task=?", tasks.get(0).getTaskId()));
        assertTrue(dispatchTaskDao.start(tasks.get(0), proc));
        assertTrue(dispatchTaskDao.stop(tasks.get(0), TaskState.DEAD, ExitStatus.FAIL, Signal.ABORTED_TASK));
        // Check to ensure the try was rolled back
        assertEquals(-1, jdbc().queryForInt("SELECT int_retry FROM plow.task WHERE pk_task=?", tasks.get(0).getTaskId()));
    }

    @Test
    public void testReserve() {
        assertTrue(dispatchTaskDao.reserve(tasks.get(0)));
    }

    @Test
    public void testUnreserve() {
        assertFalse(dispatchTaskDao.unreserve(tasks.get(0)));
        assertTrue(dispatchTaskDao.reserve(tasks.get(0)));
        assertTrue(dispatchTaskDao.unreserve(tasks.get(0)));
    }

    @Test
    public void testIsAtMaxRetries() {
        assertFalse(dispatchTaskDao.isAtMaxRetries(tasks.get(0)));
    }

    @Test
    public void testGetRunTaskCommand() {

        DispatchResult result = new DispatchResult(node);
        result.isTest = true;
        nodeDispatcher.dispatch(result, node);

        assertTrue(result.procs > 0);

        DispatchProc proc = result.pairs.get(0).proc;
        Task t = jobService.getTask(proc.getTaskId());

        RunTaskCommand command = dispatchTaskDao.getRunTaskCommand(t);
        assertEquals(command.jobId, t.getJobId().toString());
        assertEquals(command.procId, proc.getProcId().toString());
        assertEquals(command.taskId, proc.getTaskId().toString());
        assertEquals(command.cores, proc.getIdleCores());
    }


}
