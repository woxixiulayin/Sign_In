# -*- coding: utf-8 -*-
__author__ = 'qinzhonghua'

import socket
import SocketServer
import SignDB
import  dataPro
import  types
import time
import  threading
import string
from multiprocessing import Process

def clearIntheMorning():
    while True:
        curr = time.strftime('%Y-%m-%d  %H:%M:%S',time.localtime(time.time()))
        hour = string.atoi(curr.split()[1].split(":")[0])
        min = string.atoi(curr.split()[1].split(":")[1])
        if (hour*60 + min) == 360:
            print "start clearIntheMorning() have not set duty time"
            SignDB.SignDB.clearSignToady()
            SignDB.SignDB.clearDutyToday()
        time.sleep(20)


class  MyTCPHandler(SocketServer.BaseRequestHandler):
    def handle(self):
        if dataPro.debug:
            print "客户端IP： ", self.client_address[0]
        try:
            self.data = self.request.recv(100)
            if dataPro.debug:
                print  "receive data is: ", self.data
            self.request.send(dataPro.dataProcess(self.data))
            print "operation complete"
            self.request.close()
        except StandardError, e:
            print "服务器通讯出错: ",e

def startServer():
    #HOST, PORT = "192.168.137.1", 8888
    HOST, PORT = "192.168.39.127", 8888
    try:
        server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)
        print "begin serve @",HOST, ":", PORT
        server.serve_forever()
    except StandardError, e:
        print "服务器出错: ",e
if __name__ == '__main__':
    #SignDB.SignDB.remove()
    t1 = Process(target=clearIntheMorning)
    t2 = Process(target=startServer)
    t1.start()
    t2.start()