# -*- coding: utf-8 -*-

import SignDB
from  SignDB import SignDB,getCurrentTime
import  types
import encodings
import string
debug = True

SignToday = 'SignToday'

def timespace(timelast, timecurrent):
    listlast = timelast.split()[1].split(":")
    listcurr = timecurrent.split()[1].split(":")
    seclast =  string.atoi(listlast[-3])*3600 + string.atoi(listlast[-2])*60 + string.atoi(listlast[-1])
    seccurr =  string.atoi(listcurr[-3])*3600 + string.atoi(listcurr[-2])*60 + string.atoi(listcurr[-1])
    return (seccurr - seclast)



def register(data):
    if debug:
        print "begin fun register"
    post = {"name":data[0], "id":data[1], "simID":data[2],
            "SignToday":[], "SignHistory":{},
            "DutyToday":[], "DutyHistory":{}}
    if SignDB.insertuserinfor(post):
        return "register,ok"
    else:
        return "register,false"

def sign(data):
    if debug:
        print "begin fun sign"
    curr = getCurrentTime()
    record = SignDB.find(data[0])
    if record is None:
        print "员工文档不存在"
        return "sign,false"
    if len(record[SignToday]) == 0:
        return "sign,first," + SignDB.addsignrecord(data[0], curr)

    lastsign = record[SignToday][-1]
    space = timespace(lastsign, curr)
    if space >= 60:
        return "sign,many," + record[SignToday][0]\
                   + "," + SignDB.addsignrecord(data[0], curr)
    else:
        return "sign,space," + '%d'%space


def getinfor(data):
    if debug:
        print "begin fun getinfo"
    info = SignDB.find(data[0])
    if info is None:
        print "没有员工记录"
        return "FALSE"
    else :
        return str(SignDB.find(data[0]))

def request():
    return "conform"

operator ={'register':register, 'sign':sign, 'getinfo':getinfor}

def dataProcess(data):
    try:
        list = data.split(",")
    except:
        print "请输入正确格式的信息"
        return 1

    if list[0] != "begin" or list[-1] != "stop":
        print
        return 2

    operation = list[1]
    list = list[2:-1]

    if debug:
        print "operation is",operation
        print "data is:",list
    return operator.get(operation)(list)




