
#######################
# ClusterCounts
#

cdef inline ClusterCounts initClusterCounts(ClusterCountsT& c):
    cdef ClusterCounts counts = ClusterCounts()
    counts.setClusterCounts(c)
    return counts

cdef class ClusterCounts:
    """
    Contains various status counts about a :class:`plow.Cluster`

    :var nodes: int
    :var upNodes: int
    :var downNodes: int
    :var repairNodes: int
    :var lockedNodes: int
    :var unlockedNodes: int
    :var cores: int
    :var upCores: int
    :var downCores: int
    :var repairCores: int
    :var lockedCores: int
    :var unlockedCores: int
    :var runCores: int
    :var idleCores: int

    """
    cdef:
        readonly int nodes, upNodes, downNodes, repairNodes
        readonly int lockedNodes, unlockedNodes, cores, upCores
        readonly int downCores, repairCores, lockedCores, unlockedCores
        readonly int runCores, idleCores

    cdef setClusterCounts(self, ClusterCountsT& c):
        self.nodes = c.nodes
        self.upNodes = c.upNodes
        self.downNodes = c.downNodes
        self.repairNodes = c.repairNodes
        self.lockedNodes = c.lockedNodes
        self.unlockedNodes = c.unlockedNodes
        self.cores = c.cores
        self.upCores = c.upCores
        self.downCores = c.downCores
        self.repairCores = c.repairCores
        self.lockedCores = c.lockedCores
        self.unlockedCores = c.unlockedCores
        self.runCores = c.runCores
        self.idleCores = c.idleCores



#######################
# Cluster
#

cdef inline Cluster initCluster(ClusterT& n):
    cdef Cluster cluster = Cluster()
    cluster.setCluster(n)
    return cluster


cdef class Cluster:
    """
    A Cluster 

    :var ``id``: str 
    :var name: str 
    :var isLocked: bool  
    :var isDefault: bool
    :var tags: set[str]
    :var total: :class:`plow.ClusterCounts` 
    
    """
    cdef: 
        ClusterT _cluster
        ClusterCounts _total

    def __init__(self):
        self._total = None

    def __repr__(self):
        return "<Cluster: %s>" % self.name

    cdef setCluster(self, ClusterT& c):
        self._cluster = c
        self._total = initClusterCounts(self._cluster.total)

    property id:
        def __get__(self): 
            return self._cluster.id

    property name:
        def __get__(self): return self._cluster.name

    property isLocked:
        def __get__(self): return self._cluster.isLocked

    property isDefault:
        def __get__(self): return self._cluster.isDefault

    property tags:
        def __get__(self): return self._cluster.tags

    property total:
        def __get__(self): return self._total

    cpdef refresh(self):
        """
        Refresh the attributes from the server
        """
        cdef ClusterT cluster
        getClient().proxy().getCluster(cluster, self._cluster.name)
        self.setCluster(cluster)

    def delete(self):
        """
        Delete the cluster

        :returns: bool - was deleted
        """
        cdef bint ret 
        ret = delete_cluster(self)
        return ret

    def lock(self, bint locked):
        """
        Lock the cluster 

        :param locked: bool - True to lock / False to unlock 
        :returns: bool - locked
        """
        cdef bint ret 
        ret = lock_cluster(self, locked)
        self._cluster.isLocked = locked
        return ret

    def set_tags(self, c_set[string] tags):
        """
        Set the tags for the cluster 

        :param tags: set - a set of string tags 
        """
        set_cluster_tags(self, tags)
        self._cluster.tags = tags

    def set_name(self, string name):
        """
        Set the name of the cluster 

        :param name: str - name
        """
        set_cluster_name(self, name)
        self._cluster.name = name

    def set_default(self):
        """
        Set this cluster to be the default cluster 
        """
        set_default_cluster(self)
        self._cluster.isDefault = True


cpdef inline Cluster get_cluster(string name):
    """
    Return a Cluster by name 

    :param name: str - name of a cluster 
    :returns: :class:`plow.Cluster`
    """
    cdef: 
        ClusterT clusterT 
        Cluster cluster 
    getClient().proxy().getCluster(clusterT, name)
    cluster = initCluster(clusterT)
    return cluster

def get_clusters():
    """
    Return a list of all Clusters 

    :returns: list of Clusters 
    """
    cdef: 
        ClusterT clusterT 
        vector[ClusterT] clusters 
        list ret = [] 
    getClient().proxy().getClusters(clusters)
    ret = [initCluster(clusterT) for clusterT in clusters]
    return ret    

def get_clusters_by_tag(string tag):
    """
    Return a list of Clusters matching a tag 

    :param tag: str - tag 
    :returns: list of Clusters 
    """
    cdef: 
        ClusterT clusterT 
        vector[ClusterT] clusters 
        list ret = [] 
    getClient().proxy().getClustersByTag(clusters, tag)
    ret = [initCluster(clusterT) for clusterT in clusters]
    return ret    

def create_cluster(str name, c_set[string] tags):
    """
    Create a Cluster with a name and set of tags 

    :param name: str - Name of Cluster 
    :param tags: set - Set of tags 
    """
    cdef:
        ClusterT clusterT
        Cluster cluster 

    getClient().proxy().createCluster(clusterT, name, tags)
    cluster = initCluster(clusterT)
    return cluster

cpdef inline bint delete_cluster(Cluster cluster):
    """
    Delete a Cluster 

    :param cluster: :class:`.Cluster`
    :returns: bool - True if deleted
    """
    cdef bint ret
    ret = getClient().proxy().deleteCluster(cluster.id)
    return ret

cpdef inline bint lock_cluster(Cluster cluster, bint locked):
    """
    Lock a Cluster 

    :param cluster: :class:`.Cluster`
    :param locked: bool - True to lock / False to unlock 
    :returns: bool - locked
    """
    cdef bint ret
    ret = getClient().proxy().lockCluster(cluster.id, locked)
    return ret

cpdef inline set_cluster_tags(Cluster cluster, c_set[string] tags):
    """
    Set the tags for a Cluster 

    :param cluster: :class:`.Cluster`
    :param tags: set - A set of tags for the Cluster 
    """
    getClient().proxy().setClusterTags(cluster.id, tags)

cpdef inline set_cluster_name(Cluster cluster, string name):
    """
    Set a name for a Cluster 

    :param cluster: :class:`.Cluster`
    :param name: str - Cluster name
    """
    getClient().proxy().setClusterName(cluster.id, name)

cpdef inline set_default_cluster(Cluster cluster):
    """
    Set a given Cluster to be the default Cluster

    :param cluster: :class:`.Cluster`
    :returns: bool - True if deleted
    """
    getClient().proxy().setDefaultCluster(cluster.id)



