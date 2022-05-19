db = new Mongo().getDB("meter");
db.createCollection("temp");