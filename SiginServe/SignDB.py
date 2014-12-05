# -*- coding: utf-8 -*-

import pymongo
import time
from pymongo import MongoClient
import string
#HOST,PORT = "192.168.39.127", 27017
#client = MongoClient(HOST,PORT)
#signco = client.dataDB.collection
def timespace(timelast, timecurrent):
    listlast = timelast.split()[1].split(":")
    listcurr = timecurrent.split()[1].split(":")
    seclast =  string.atoi(listlast[-3])*3600 + string.atoi(listlast[-2])*60 + string.atoi(listlast[-1])
    seccurr =  string.atoi(listcurr[-3])*3600 + string.atoi(listcurr[-2])*60 + string.atoi(listcurr[-1])
    return (seccurr - seclast)
def getCurrentTime():
    return time.strftime('%Y-%m-%d  %H:%M:%S',time.localtime(time.time()))

class SignDB(object):
    def __init__(self, HOST, PORT):
        self.client = MongoClient(HOST,PORT)
        self.signco = MongoClient(HOST,PORT).signDB.collection
        #self.signco.remove()

    def insertuserinfor(self, post):
        mypost = self.signco.find_one({"simID":post["simID"]})
        if mypost:
            print "员工文档已存在"
            return False
        else:
            self.signco.insert(post)
            #print self.signco.find_one({"simID":simID})
            print "成功存入员工信息"
            return True

    def find(self, simID):
        return self.signco.find_one({"simID":simID})

    def modifyinfor(self):
        pass

    def addsignrecord(self, simID, time):
        day = time.split()[0]

        self.signco.update({"simID":simID}, {"$push":
            {"SignToday": time}})
        self.signco.update({"simID":simID}, {"$push":
            {"SignHistory." + day: time}})

        record = self.find(simID)
        SignToday = record["SignToday"]
        firsttime = SignToday[0]
        if len(SignToday) == 1:
            lasttime = "无"
            dutyTime = "无"
        else:
            lasttime = SignToday[-1]
            dutyhour = timespace(firsttime, lasttime)/60/60
            dutymin = timespace(firsttime, lasttime)/60%60
            if dutyhour > 0:
                dutyhour = "%d"%dutyhour + "小时"
            else:
                dutyhour = ""
            if dutymin > 0 :
                dutymin = "%d"%dutymin + "分钟"
            else:
                dutymin = ""
            dutyTime = dutyhour + dutymin
        DutyToday = [firsttime, lasttime, dutyTime]
        self.signco.update({"simID":simID}, {"$set":
            {"DutyToday": DutyToday}})
        self.signco.update({"simID":simID}, {"$set":
            {"DutyHistory." + day: DutyToday}})
        return time

    def updatesignhistory(self, simID, record):
        if record is not list:
            print "请输入数组作为函数参数"
            return False
        else:
            self.signco.update({{"simID":simID}, {"$addtoset":
            {"SignHistory": record}}})
            return True

    def updateonoffdutyhistory(self, simID, dutytime):
        self.signco.update({{"simID":simID}, {"$addtoset":
            {"DutyHistory": dutytime}}})
        return True

    def remove(self):
        print "remove all the data on dataDB"
        self.signco.remove()
        return True
    def remove_one(self, name):
        print "remove one: name is " + name
        self.signco.remove({"name": name})
        return True

    def clearSignToady(self):
        print "clear all SignToday data"
        list = self.signco.find()
        for yuangong in list:
            self.signco.update({"name":yuangong["name"]},
                       {"$set":{"SignToday": []}}, True, True)
    def clearSignHistory(self):
        print "clear all SignHistory data"
        list = self.signco.find()
        for yuangong in list:
            self.signco.update({"name":yuangong["name"]},
                       {"$set":{"SignHistory": {}}}, True, True)
    def clearDutyToday(self):
        print "clear all DutyToday data"
        list = self.signco.find()
        for yuangong in list:
            self.signco.update({"name":yuangong["name"]},
                       {"$set":{"DutyToday": []}}, True, True)
    def clearDutyHistory(self):
        print "clear all DutyHistory data"
        list = self.signco.find()
        for yuangong in list:
            self.signco.update({"name":yuangong["name"]},
                       {"$set":{"DutyHistory": []}}, True, True)
    def clearData(self):
        print "clear all  data  keep name id and simid"
        self.clearSignToady()
        self.clearDutyHistory()
        self.clearDutyToday()
        self.clearSignHistory()


SignDB = SignDB("192.168.39.127", 27017)

if __name__ == '__main__':
    db = SignDB
    db.remove()
    db.insertuserinfor({"id":"22222", "simID":"10011", "name":"周星星"})
    db.addsignrecord("10011")
    x=db.find("10011")
    print x
    print x["name"]
    print x["_id"]
