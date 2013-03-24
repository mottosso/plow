import os
import uuid

import rpc.ttypes as ttypes

from conn import PlowConnection

Conn = PlowConnection()

__all__ = [
    "get_projects",
    "get_project",
    "get_active_projects",
    "create_project",
    "set_project_active",
    "get_plow_time",
    "get_jobs",
    "get_active_job",
    "get_active_jobs",
    "get_job",
    "kill_job",
    "launch_job",
    "pause_job",
    "get_job_outputs",
    "get_layer",
    "get_layer_by_name",
    "get_layer_outputs",
    "get_task",
    "get_tasks",
    "get_task_log_path",
    "get_nodes",
    "get_node",
    "get_clusters",
    "get_cluster",
    "create_cluster",
    "delete_cluster",
    "lock_cluster",
    "set_cluster_name",
    "set_cluster_tags",
    "set_default_cluster",
    "create_quota",
    "get_quota",
    "set_quota_size",
    "set_quota_burst",
    "set_quota_locked",
    "set_layer_min_cores",
    "set_layer_max_cores",
    "set_layer_min_ram_mb",
    "set_layer_threadable",
    "set_layer_tags",
    "retry_tasks",
    "kill_tasks",
    "eat_tasks"
]

def is_uuid(identifier):
    try:
        uuid.UUID(identifier)
        return True
    except ValueError:
        return False

def get_plow_time():
    return Conn.service.getPlowTime()

# Projects
def get_projects():
    return Conn.service.getProjects()

def get_active_projects():
    return Conn.service.getActiveProjects()

def get_project(identifier):
    if is_uuid(identifier):
        return Conn.service.getProject(identifier)
    else:
        return Conn.service.getProjectByCode(identifier)

def create_project(title, code):
    return Conn.service.createProject(title, code)

def set_project_active(proj, active):
    return Conn.service.setProjectActive(proj.id, active)

# Quotas

def create_quota(project, cluster, size, burst):
    return Conn.service.createQuota(project.id, cluter.id, size, burst)

def get_quota(guid):
    return Conn.service.getQuota(guid)

def set_quota_size(quota, size):
    Conn.service.setQuotaSize(quota.id, size)

def set_quota_burst(quota, burst):
    Conn.service.setQuotaBurst(quota.id, burst)

def set_quota_locked(quota, locked):
    Conn.service.setQuotaLocked(quota.id, locked)

# Clusters

def create_cluster(name, tags):
    return Conn.service.createCluster(name, tags)

def get_cluster(name):
    return Conn.service.getCluster(name)

def delete_cluster(cluster):
    return Conn.service.deleteCluster(cluster.id)

def lock_cluster(cluster, value):
    return Conn.service.lockCluster(cluster.id, value)

def get_clusters(tag=None):
    if tag:
        return Conn.service.getClustersByTag(tag)
    else:
        return Conn.service.getClusters()

def set_cluster_name(cluster, name):
    Conn.service.setClusterName(cluster.id, name)

def set_cluster_tags(cluster, tags):
    Conn.service.setClusterTags(cluster.id, tags)

def set_default_cluster(cluster):
    Conn.service.setDefaultCluster(cluster.id)

# Jobs

def get_jobs(**kwargs):
    filt = ttypes.JobFilterT()
    for k, v in kwargs.items():
        setattr(filt, k, v)
    return Conn.service.getJobs(filt)

def get_active_jobs():
    filt = ttypes.JobFilterT()
    filt.states = [ttypes.JobState.RUNNING]
    return Conn.service.getJobs(filt)

def get_active_job(name):
    return Conn.service.getActiveJob(name)

def get_job(guid):
    return Conn.service.getJob(guid)

def kill_job(job):
    Conn.service.killJob(job.id, "Manually killed by UID: %d " % os.getuid())

def launch_job(jobspec):
    return Conn.service.launch(jobspec)

def pause_job(job, paused):
    Conn.service.pauseJob(job.id, paused)

def get_job_outputs(job):
    return Conn.service.getJobOutputs(job.id)

# Layers

def get_layer(guid):
    return Conn.service.getLayerById(guid)

def get_layer_by_name(job, name):
    return Conn.service.getLayer(job.id, name)

def get_layer_outputs(job):
    return Conn.service.getLayerOutputs(job.id)

def add_layer_output(layer, path, attrs=None):
    if attrs is None:
        attrs = dict()
    Conn.service.addOutput(str(layer.id), str(path), attrs)

def set_layer_min_cores(layer, cores):
    Conn.service.setLayerMinCoresPerTask(layer.id, cores)

def set_layer_max_cores(layer, cores):
    Conn.service.setLayerMaxCoresPerTask(layer.id, cores)

def set_layer_min_ram_mb(layer, ram):
    Conn.service.setLayerMinRamPerTask(layer.id, ram)

def set_layer_threadable(layer, enabled):
    Conn.service.setLayerThreadable(layer.id, enabled)

def set_layer_tags(layer, tags):
    Conn.service.setLayerTags(layer.id, tags)

# Tasks

def get_task(guid):
    return Conn.service.getTask(guid)

def get_task_log_path(task):
    return Conn.service.getTaskLogPath(task.id)

def retry_tasks(**kwargs):
    filt = ttypes.TaskFilterT()
    for k, v in kwargs.items():
        setattr(filt, k, v)
    return Conn.service.retryTasks(filt)

def kill_tasks(**kwargs):
    filt = ttypes.TaskFilterT()
    for k, v in kwargs.items():
        setattr(filt, k, v)
    return Conn.service.killTasks(filt)

def eat_tasks(**kwargs):
    filt = ttypes.TaskFilterT()
    for k, v in kwargs.items():
        setattr(filt, k, v)
    return Conn.service.eatTasks(filt)

def get_tasks(**kwargs):
    filt = ttypes.TaskFilterT()
    for k, v in kwargs.items():
        setattr(filt, k, v)
    return Conn.service.getTasks(filt)

# Nodes

def get_node(name):
    return Conn.service.getNode(name)

def get_nodes(**kwargs):
    filt = ttypes.NodeFilterT()
    for k, v in kwargs.items():
        setattr(filt, k, v)
    return Conn.service.getNodes(filt)
