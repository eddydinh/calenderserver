const express = require('express');
const bodyParser = require('body-parser');
const bcrypt = require('bcrypt-nodejs');
const fileupload = require("express-fileupload");
const cors = require('cors');
const knex = require('knex');

const register = require('./controllers/register');
const signin = require('./controllers/signin');
const profile = require('./controllers/profile');
const unparsed = require('./controllers/unparsed');
const parsedfile = require('./controllers/parsed');


const db = knex({
    client: 'pg',
  connection: {
    connectionString : process.env.DATABASE_URL,
    ssl: true
  }
});

const app = express();


app.use(bodyParser.json());
app.use(fileupload());
app.use(cors());
app.use(express.static('public'));

app.get('/', (req, res) => {
    res.json('Connected successfully');
})
app.post('/signin', signin.handleSignin(db, bcrypt))
app.post('/register', (req, res) => { register.handleRegister(req, res, db, bcrypt) })
app.post('/parsedfile', (req,res) => parsedfile.handleParsed(req,res,db))
app.get('/profile/:id', (req, res) => { profile.handleProfileGet(req, res, db)})
app.get('/unparsedfiles', (req, res) => { unparsed.handleUnparsedGet(req, res, db)})
app.get('/:fileName',function(req,res,next){
    
  const options = {
    root: __dirname + '/public/files/',
    dotfiles: 'deny',
    headers: {
        'x-timestamp': Date.now(),
        'x-sent': true
    }
  };
 const fileName = req.params.fileName;
    
    res.sendFile(fileName,options,(err)=>{
        if(err){
            next(err);
        }else{
            console.log('Sent: ', fileName);
        }
    })
});




app.listen(process.env.PORT || 3000, ()=> {
  console.log(`app is running on port ${process.env.PORT}`);
})