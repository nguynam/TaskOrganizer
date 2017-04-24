var functions = require("firebase-functions");
const admin = require("firebase-admin");
var moment = require("moment-timezone");
admin.initializeApp(functions.config().firebase);
exports.minute_job = functions.https.onRequest((req, res) => {
  console.log("Cron has been called, checking for notifications to send");
  var usersRef = admin.database().ref("/users");
  usersRef.once("value", function (data) {
    dataVal = data.val();
    for(var user in dataVal){
      var userTasks = dataVal[user].userTasks;
      var children = userTasks.children;
      var token = userTasks.token;
      var json = JSON.parse(unescape(children));
      for(var task in json){
        var reminderText = json[task][3];
        var dueDateText = json[task][1];
        console.log(reminderText);
        if(reminderText.includes("Before")){
            var reminderTextClean = reminderText.substring(reminderText.indexOf(":")+2);
            var dueMoment = moment.tz(dueDateText,"M/DD/YYYY hh:mm A","America/New_York");
            var currentTime = moment().tz("America/New_York");
            var reminderValue = reminderTextClean.substring(0,1);
            var reminderUnit = reminderTextClean.substring(2,reminderTextClean.indexOf("B")-1).toLowerCase();
            console.log("ReminderUnit: " + reminderUnit + " Reminder Value: " + reminderValue);
            var reminderMoment = moment.tz(dueMoment,"America/New_York").subtract(reminderValue, reminderUnit);
            if(reminderMoment.isSame(currentTime,"minute")){
              //Same time
              //Send notification
              console.log("Same Time");
              sendNotification(token);
            }else{
              //Time differs
              console.log("Time Differs");
            }
            console.log(currentTime);
            console.log(reminderMoment);
            console.log(dueMoment);
        }
        if(reminderText.includes("At time")){
          //send immediatly if current time matches due time
            var dueMoment = moment.tz(dueDateText,"M/DD/YYYY hh:mm A","America/New_York");
            var currentTime = moment().tz("America/New_York");
            if(dueMoment.isSame(currentTime,"minute")){
              console.log("Same Time");
              sendNotification(token);
            }
        }
      }
    }
  });
  res.send("Check completed");
  return;
  function sendNotification(receivedToken){
    if(receivedToken!=null && receivedToken!=undefined){
      console.log('Sending notification');
      console.log(receivedToken);
      var options = {
        priority: "high"
      };
      const payload = {
      notification: {
        title: 'Task Is Due Soon',
        body: 'You have a remnder'
      }
    };
    console.log("receivedToken: " + receivedToken);
      admin.messaging().sendToDevice(receivedToken,payload, options);
      console.log("attempted to send notification");
    }
  }
});
