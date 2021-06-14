/******************** CONFIGURACIONES FIREBASE ****************/
var admin = require('firebase-admin');
const http = require('http');
var serverAccount = require('../fctdam-45f92-firebase-adminsdk-t4c1g-e91f1c48fd.json');
var cron = require('node-cron');
let dateOffset = (24 * 60 * 60 * 1000) * 14;
admin.initializeApp({
    credential: admin.credential.cert(serverAccount),
    databaseURL: "https://fctdam-45f92-default-rtdb.europe-west1.firebasedatabase.app/"
});
/***********************************************************/

const server = http.createServer(function (request, response) {
    if (request.method == "POST") {
        //console.log("Algo por POST");
        var body = [];
        request.on('data', function (data) {
            body.push(data);
        });
        request.on('close', () => {
            //console.log("Se acabo");
            var tokenStr = Buffer.concat(body).toString();
            //console.log(tokenStr);
            var message = {
                notification: {
                    title: 'Un contacto ha tenido COVID-19',
                    body: 'Uno de tus contactos ha dado positivo en COVID-19. Hazte la prueba para asegurarte de q>
                },
                token: tokenStr
            };
            admin.messaging().send(message).then((res) => {
                //console.log("Ok notificacion " + res);
            }).catch((err) => {
                //console.error("Fail notificacion " + err);
            });
        }
        );
    } else {
       //console.log("Otra cosa");
    }
});
var db = admin.database();
var ref = db.ref();
// para ejecucion diaria la string es '00 00 * * *'
cron.schedule('00 00 * * *', () => {
    ref.once('value', (snapshot) => {
        //coge todos los hijos
        let snap = snapshot.val();
        for (const keyMacRoot in snap) {
            if (Object.hasOwnProperty.call(snap, keyMacRoot)) {
                const childrenMacRoot = snap[keyMacRoot];
                //coge todos los valores de los hijos
                for (const keyMacChild in childrenMacRoot) {
                    if (Object.hasOwnProperty.call(childrenMacRoot, keyMacChild)) {
                        const valueMacChildren = childrenMacRoot[keyMacChild];
                        // console.log(valueMacChildren);
                        if (Date.parse(valueMacChildren)) {
                            var valDate = new Date(valueMacChildren);
                            var nowDate = new Date();
                            nowDate.setTime(nowDate.getTime() - dateOffset);
                            if (valDate <= nowDate) {
                                ref.child(keyMacRoot).child(keyMacChild).remove();
                            }
                        }
                    }
                }
            }
        }
    })
})
const port = 3000;
const host = "0.0.0.0";
try {
    server.listen(port, host)
} catch (error) {
  //console.log(error);
}
//console.log("Escuchando en " + host + ":" + port);