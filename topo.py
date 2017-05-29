"""
Example topology of Quagga routers

Edit : April 7, 2017
Edit-Author : Arun Rajan, Stony Brook University
Purpose: CSE 534, Assignment 3
"""

import inspect
import os
from mininext.topo import Topo
from mininext.services.quagga import QuaggaService

from collections import namedtuple

QuaggaHost = namedtuple("QuaggaHost", "name ip loIP")
net = None


class QuaggaTopo(Topo):

    "Creates a topology of Quagga routers"

    def __init__(self):
        """Initialize a Quagga topology with 5 routers, configure their IP
           addresses, loop back interfaces, and paths to their private
           configuration directories."""
        Topo.__init__(self)

        # Directory where this file / script is located"
        selfPath = os.path.dirname(os.path.abspath(
            inspect.getfile(inspect.currentframe())))  # script directory

        # Initialize a service helper for Quagga with default options
        quaggaSvc = QuaggaService(autoStop=False)

        # Path configurations for mounts
        quaggaBaseConfigPath = selfPath + '/configs/'

        # List of Quagga host configs
        quaggaHosts = []
        quaggaHosts.append(QuaggaHost(name='H1', ip='192.0.1.1/24',
                                      loIP='10.0.1.1/24'))
        quaggaHosts.append(QuaggaHost(name='R1', ip='192.0.1.2/24',
                                      loIP=None))
        quaggaHosts.append(QuaggaHost(name='R2', ip='192.0.2.2/24',
                                      loIP=None))
        quaggaHosts.append(QuaggaHost(name='R3', ip='192.0.3.2/24',
                                      loIP='10.0.3.1/24'))
        quaggaHosts.append(QuaggaHost(name='R4', ip='192.0.6.1/24',
                                      loIP='10.0.4.1/24'))
        quaggaHosts.append(QuaggaHost(name='H2', ip='192.0.6.2/24',
                                      loIP=None))

        hostList = []
	print("Arun :Creating a list of hosts ")
        for host in quaggaHosts:

            # Create an instance of a host and append it to the hostList
            hostList.append(self.addHost(name=host.name,
                                           ip=host.ip,
                                           hostname=host.name,
                                           privateLogDir=True,
                                           privateRunDir=True,
                                           inMountNamespace=True,
                                           inPIDNamespace=True,
                                           inUTSNamespace=True))

            # Add a loopback interface with an IP in router's announced range
            #self.addNodeLoopbackIntf(node=host.name, ip=host.loIP)

            # Configure and setup the Quagga service for this node
            quaggaSvcConfig = \
                {'quaggaConfigPath': quaggaBaseConfigPath + host.name}
            self.addNodeService(node=host.name, service=quaggaSvc,
                                nodeConfig=quaggaSvcConfig)
	self.addLink(hostList[0],hostList[1])
	self.addLink(hostList[1],hostList[2])
	self.addLink(hostList[1],hostList[3])
        self.addLink(hostList[4],hostList[5])
        self.addLink(hostList[2],hostList[4])    
	self.addLink(hostList[3],hostList[4])
	
	
