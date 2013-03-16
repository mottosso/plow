package com.breakersoft.plow.dispatcher;

import java.util.List;
import java.util.UUID;

import com.breakersoft.plow.JobId;
import com.breakersoft.plow.Node;
import com.breakersoft.plow.Task;
import com.breakersoft.plow.dispatcher.domain.DispatchJob;
import com.breakersoft.plow.dispatcher.domain.DispatchNode;
import com.breakersoft.plow.dispatcher.domain.DispatchProc;
import com.breakersoft.plow.dispatcher.domain.DispatchProject;
import com.breakersoft.plow.dispatcher.domain.DispatchResource;
import com.breakersoft.plow.dispatcher.domain.DispatchableTask;
import com.breakersoft.plow.rnd.thrift.RunTaskCommand;

/**
 * A DAO for obtaining Dispatch brand objects.
 *
 * @author chambers
 *
 */
public interface DispatchDao {

    List<DispatchProject> getSortedProjectList(Node node);

    DispatchNode getDispatchNode(String name);

    DispatchProc getDispatchProc(UUID id);

    List<DispatchableTask> getDispatchableTasks(JobId job, DispatchResource resource);

    void decrementDispatchTotals(DispatchProc proc);

    void incrementDispatchTotals(DispatchProc proc);

    RunTaskCommand getRunTaskCommand(Task task);

	List<DispatchJob> getDispatchJobs(DispatchProject project, DispatchNode node);

	DispatchJob getDispatchJob(UUID id);
}