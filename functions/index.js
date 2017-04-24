require("@google-cloud/debug-agent").start();
var functions = require("firebase-functions");
const admin = require("firebase-admin");
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
admin.initializeApp(functions.config().firebase);
/*exports.sendNotification = functions.database.ref("/users/{userId}/{userTasks}")
        .onWrite(event => {
 
        // Grab the current value of what was written to the Realtime Database.
        var eventSnapshot = event.data;
        var userId = event.params.userId;
        console.log("User: " + userId);
        });*/
exports.minute_job = functions.https.onRequest((req, res) => {
  console.log("Cron has been called, checking for notifications to send");
  var usersRef = admin.database().ref("/users");
  console.log("UsersRef: " + usersRef);
  usersRef.once("value", function (data) {
    dataVal = data.val();
    console.log(dataVal);
    /*var user = dataVal[Object.keys(dataVal)[0]];
    console.log(user);
    var userTasks = user.userTasks;
    console.log(userTasks);
    var children = userTasks.children;
    console.log(children);
    var json = JSON.parse(unescape(children));
    console.log(json[Object.keys(json)[0]]);*/
    for(var user in dataVal){
      console.log(user);
      var userTasks = dataVal[user].userTasks;
      console.log(userTasks);
      var children = userTasks.children;
      console.log(children);
      var json = JSON.parse(unescape(children));
      for(var task in json){
        console.log(json[task][3]);
      }
      //console.log(json[Object.keys(json)[3]]);
    }
  });
  res.send("Check completed");
  return;
});
