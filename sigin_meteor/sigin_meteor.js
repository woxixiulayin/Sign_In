
Tasks = new Mongo.Collection("collection");

if (Meteor.isClient) {
	
	var viewchoice = ["viewall","viewhistory"];
	
	Session.set("view", viewchoice[0]);
	
	Template.body.helpers({
	
	
	});
	
	// This code only runs on the client
  Template.view.helpers({
	view: function(){
		return Session.get("view");
	}
  });
	
  // This code only runs on the client
	Template.viewall.helpers({
	tasks: function(){
	return Tasks.find({});
	}
	});
  
    Template.viewhistory.helpers({
	name:function(){
	return Session.get("employee").name;
	},
	DutyHistory:function(){
	var DutyHistory = new Array();
	var doc = Session.get("employee").DutyHistory;
	for(var p in doc){
		var ele = new Array();
		var day = p;
		//alert(p);
		var onduty = doc[p][0].split("  ")[1]
		var offduty = doc[p][1].split("  ")[1]
		var space = doc[p][2]
		
		ele.push(day, onduty, offduty, space);
		DutyHistory.unshift(ele);
	}
	//alert(DutyHistory.lenth);
	return DutyHistory;
	}
	
	});
	
	Template.task.events({
	"click .checkhistory":function(event){
	Session.set("view", viewchoice[1]);
	Session.set("employee", Tasks.findOne({simID:this.simID}));
	}
	});
	
	Template.viewhistory.events({
	"click .back":function(event){
	Session.set("view", viewchoice[0]);
	}
	});
}

if (Meteor.isServer) {
  Meteor.startup(function () {
    // code to run on server at startup
  });
}
