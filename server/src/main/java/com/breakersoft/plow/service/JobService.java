package com.breakersoft.plow.service;

import com.breakersoft.plow.Job;
import com.breakersoft.plow.Layer;
import com.breakersoft.plow.Task;
import com.breakersoft.plow.dispatcher.domain.DispatchProc;
import com.breakersoft.plow.thrift.TaskState;

public interface JobService {

    Task getTask(String id);

    boolean setTaskState(Task task, TaskState currentState, TaskState newState);

    boolean hasWaitingFrames(Job job);

    Job getJob(String id);

    boolean hasPendingFrames(Job job);

    Task getTask(Layer layer, int number);

    Layer getLayer(Job job, String layer);

    boolean startTask(Task task, DispatchProc proc);

    boolean stopTask(Task task, TaskState state);

    boolean reserveTask(Task task);

    boolean unreserveTask(Task task);
}
