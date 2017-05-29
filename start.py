#!/usr/bin/python

"""
Example network of Quagga routers
(QuaggaTopo + QuaggaService)
"""

import sys
import atexit

# patch isShellBuiltin
import mininet.util
import mininext.util
mininet.util.isShellBuiltin = mininext.util.isShellBuiltin
sys.modules['mininet.util'] = mininet.util

from mininet.util import dumpNodeConnections
from mininet.node import OVSController
from mininet.log import setLogLevel, info

from mininext.cli import CLI
from mininext.net import MiniNExT

from topo import QuaggaTopo

net = None


def startNetwork():
    "instantiates a topo, then starts the network and prints debug information"

    info('** Creating Quagga network topology\n')
    topo = QuaggaTopo()

    info('** Starting the network\n')
    global net
    net = MiniNExT(topo, controller=OVSController)
    net.start()
    
    print("Arun testing Start")
    h1 = net.getNodeByName('H1')
    h2 = net.getNodeByName('H2')

    r1 = net.getNodeByName('R1')
    r1.cmdPrint('ip address add 192.0.2.1/24 dev R1-eth1')
    r1.cmdPrint('ip address add 192.0.3.1/24 dev R1-eth2')
    r2 = net.getNodeByName('R2')
    r2.cmdPrint('ip address add 192.0.4.1/24 dev R2-eth1')
    r4 = net.getNodeByName('R4')
    r4.cmdPrint('ip address add 192.0.5.2/24 dev R4-eth2')
    r4.cmdPrint('ip address add 192.0.4.2/24 dev R4-eth1')
    r3 = net.getNodeByName('R3')
    r3.cmdPrint('ip address add 192.0.5.1/24 dev R3-eth1')

    info('** Enabling Ip forwarding\n')
    for host in net.hosts:
        host.cmdPrint("echo 1 > /proc/sys/net/ipv4/ip_forward")
    
    info('**** Setting static routes*******')
    h1.cmdPrint('ip route add 192.0.6.0/24 via 192.0.1.2 dev H1-eth0')
    h1.cmdPrint('ip route add 192.0.2.0/24 via 192.0.1.2 dev H1-eth0')
    h1.cmdPrint('ip route add 192.0.4.0/24 via 192.0.1.2 dev H1-eth0')
    h1.cmdPrint('ip route add 192.0.3.0/24 via 192.0.1.2 dev H1-eth0')
    h1.cmdPrint('ip route add 192.0.5.0/24 via 192.0.1.2 dev H1-eth0')	
     
    r1.cmdPrint('ip route add 192.0.6.0/24 via 192.0.2.2 dev R1-eth1')
    r1.cmdPrint('ip route add 192.0.4.0/24 via 192.0.2.2 dev R1-eth1')
    r1.cmdPrint('ip route add 192.0.5.0/24 via 192.0.3.2 dev R1-eth2') 

    r2.cmdPrint('ip route add 192.0.6.0/24 via 192.0.4.2 dev R2-eth1')
    r2.cmdPrint('ip route add 192.0.4.0/24 via 192.0.4.2 dev R2-eth1')
    r2.cmdPrint('ip route add 192.0.1.0/24 via 192.0.2.1 dev R2-eth0')
    r2.cmdPrint('ip route add 192.0.3.0/24 via 192.0.2.1 dev R2-eth0')
    
    h2.cmdPrint('ip route add 192.0.1.0/24 via 192.0.6.1 dev H2-eth0')
    h2.cmdPrint('ip route add 192.0.2.0/24 via 192.0.6.1 dev H2-eth0')
    h2.cmdPrint('ip route add 192.0.3.0/24 via 192.0.6.1 dev H2-eth0')
    h2.cmdPrint('ip route add 192.0.4.0/24 via 192.0.6.1 dev H2-eth0')
    h2.cmdPrint('ip route add 192.0.5.0/24 via 192.0.6.1 dev H2-eth0')     

    r4.cmdPrint('ip route add 192.0.2.0/24 via 192.0.4.1 dev R4-eth1')
    r4.cmdPrint('ip route add 192.0.1.0/24 via 192.0.4.1 dev R4-eth1')
    r4.cmdPrint('ip route add 192.0.3.0/24 via 192.0.5.1 dev R4-eth2')
    
    r3.cmdPrint('ip route add 192.0.1.0/24 via 192.0.3.1 dev R3-eth0')
    r3.cmdPrint('ip route add 192.0.6.0/24 via 192.0.5.2 dev R3-eth1')       
    r3.cmdPrint('ip route add 192.0.2.0/24 via 192.0.3.1 dev R3-eth0')

    info('** Dumping host connections\n')
    dumpNodeConnections(net.hosts)

    info('** Testing network connectivity\n')
    net.ping(net.hosts)

    info('** Dumping host processes\n')
    for host in net.hosts:
        host.cmdPrint("ps aux")

    info('** Running CLI\n')
    CLI(net)


def stopNetwork():
    "stops a network (only called on a forced cleanup)"

    if net is not None:
        info('** Tearing down Quagga network\n')
        net.stop()

if __name__ == '__main__':
    # Force cleanup on exit by registering a cleanup function
    atexit.register(stopNetwork)

    # Tell mininet to print useful information
    setLogLevel('info')
    startNetwork()
