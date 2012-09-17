
import platform
import socket
import logging

import rndlib.netcode as netcode
import rndlib.conf as conf

from rndlib.rpc import ttypes

logger = logging.getLogger("profile.base")

class AbstractProfiler(object):
    def __init__(self):
        self.data = { "platform": platform.platform() }
        self.update()

    def sendPing(self, processes, isReboot=False):
        
        # Update the values (calls subclass impl)
        self.update()

        # Create the hardware profile
        hw = ttypes.Hardware()
        hw.physicalCpus = self.physicalCpus
        hw.logicalCpus = self.logicalCpus
        hw.totalRamMb = self.totalRamMb
        hw.freeRamMb = self.freeRamMb
        hw.totalSwapMb = self.totalSwapMb
        hw.freeSwapMb = self.freeSwapMb
        hw.cpuModel = self.cpuModel
        hw.platform = self.platform

        # Create a ping
        ping = ttypes.Ping()
        ping.hostname = socket.getfqdn()
        ping.ipAddr = socket.gethostbyname(socket.getfqdn())
        ping.isReboot = isReboot
        ping.bootTime = self.bootTime
        ping.hw = hw
        ping.processes = processes

        if conf.NETWORK_DISABLED:
            return
        # Send ping
        try:
            conn = netcode.getPlowConnection()
            conn.sendPing(ping)
        except Exception, e:
            logger.warn("Unable to send ping to plow server, %s" % e)

    def update(self):
        pass

    def __getattr__(self, k):
        return self.data[k]

    def __str__(self):
        return str(self.data)
