db = db.getSiblingDB("TACS_DB"); // trae la bd "tacsdb" que se crea al iniciar
db.createCollection("init");// se crea init para que haya una bd por defecto y verla desde mongo express

db.createUser(
    {
        user: "admin",
        pwd: "admin123",
        roles:[
            {
                role: "readWrite",
                db: "TACS_DB"
            }
        ]
    }
);