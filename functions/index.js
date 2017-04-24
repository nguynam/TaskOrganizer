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
      var json = JSON.parse(unescape(children));
      for(var task in json){
        var reminderText = json[task][3];
        var dueDateText = json[task][1];
        console.log(reminderText);
        if(reminderText.includes("Before")){
            reminderText = reminderText.substring(reminderText.indexOf(":")+2);
            var dueMoment = moment.tz(dueDateText,"M/DD/YYYY hh:mm A","America/New_York");
            var currentTime = moment().tz("America/New_York");
            var reminderValue = reminderText.substring(0,1);
            var reminderUnit = reminderText.substring(2,reminderText.indexOf("B")-1).toLowerCase();
            console.log("ReminderUnit: " + reminderUnit + " Reminder Value: " + reminderValue);
            var reminderMoment = moment.tz(dueMoment,"America/New_York").subtract(reminderValue, reminderUnit);
            if(reminderMoment.isSame(currentTime,"minute")){
              //Same time
              console.log("Same Time");
            }else{
              //Time differs
              console.log("Time Differs");
            }
            console.log(currentTime);
            console.log(reminderMoment);
            console.log(dueMoment);
        }
      }
    }
  });
  res.send("Check completed");
  return;
});
