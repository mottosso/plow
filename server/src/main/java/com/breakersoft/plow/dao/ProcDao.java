package com.breakersoft.plow.dao;

import java.util.List;
import java.util.UUID;

import com.breakersoft.plow.Job;
import com.breakersoft.plow.Task;
import com.breakersoft.plow.Proc;
import com.breakersoft.plow.dispatcher.domain.DispatchProc;
import com.breakersoft.plow.dispatcher.domain.DispatchTask;

public interface ProcDao {

    Proc getProc(Task frame);

    Proc getProc(UUID procId);

    void create(DispatchProc proc);

    boolean delete(Proc proc);

    void update(DispatchProc proc, DispatchTask task);

    List<Proc> getProcs(Job job);

    boolean setProcUnbooked(Proc proc, boolean unbooked);

}
