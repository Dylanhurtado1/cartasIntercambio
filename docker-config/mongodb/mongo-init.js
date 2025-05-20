db = db.getSiblingDB("TACS_DB"); // trae la bd "tacsdb" que se crea al iniciar
db.createCollection("init");// se crea init para que haya una bd por defecto y verla desde mongo express

db.usuarios.insertMany([
    {
        _id: ObjectId("6828aad28b39423270e46c7f"),
        user: "vendedorX",
        nombre: "Vendedor X",
        correo: "x@ejemplo.com",
        password: "1234",
        tipo: "usuario"
    },
    {
        _id: ObjectId("6828ab2e8b39423270e46c80"),
        user: "ofertanteY",
        nombre: "Ofertante Y",
        correo: "y@ejemplo.com",
        password: "5678",
        tipo: "usuario"
    }
]);