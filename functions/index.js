require('@google-cloud/debug-agent').start({ allowExpressions: true });
var functions = require('firebase-functions');
const admin = require('firebase-admin');
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
admin.initializeApp(functions.config().firebase);
/*exports.sendNotification = functions.database.ref('/users/{userId}/{userTasks}')
        .onWrite(event => {
 
        // Grab the current value of what was written to the Realtime Database.
        var eventSnapshot = event.data;
        var userId = event.params.userId;
        console.log("User: " + userId);
        });*/
exports.minute_job = functions.https.onRequest((req, res) => {
  console.log("Cron has been called, checking for notifications to send");
  var usersRef = admin.database().ref('/users');
  console.log('UsersRef: ' + usersRef);
  usersRef.once('value',function(snapshot){
    console.log('snapshot: ' + snapshot);
    console.log('snapshotString: ' + JSON.stringify(snapshot));
  });
  res.send('Check completed');
  return;
});
