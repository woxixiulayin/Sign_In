#-*- coding: utf-8 -*-

import  socket
import time
import string
import SignDB
def timespace(timelast, timecurrent):
    listlast = timelast.split()[1].split(":")
    print listlast
    listcurr = timecurrent.split()[1].split(":")
    print listcurr
    seclast =  string.atoi(listlast[-3])*3600 + string.atoi(listlast[-2])*60 + string.atoi(listlast[-1])
    print seclast
    seccurr =  string.atoi(listcurr[-3])*3600 + string.atoi(listcurr[-2])*60 + string.atoi(listcurr[-1])
    print seccurr
    return (seccurr - seclast)

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#s.connect(('192.168.137.1', 8888))

#post = 'begin,register,刘老师,123456,654321,stop'
#post = 'begin,getinfo,654321,stop'
#post = "begin,sign,333333,stop"

#s.send(post)
#print s.recv(8 * 1024)

#while True:
  #  time.sleep(3)
 #   print  "1"
#SignDB.SignDB.clearSignToady()
#SignDB.SignDB.clearDutyToday()
#SignDB.SignDB.remove()
print SignDB.SignDB.signco.find_one({"name":"陈浩君"})
#print timespace(u"2014-11-29  14:05:07", "2014-11-29  14:13:34")
#print time.localtime(time.time())
#print "asdf    asdf ".split()
#print a
   # print s.recv(20)