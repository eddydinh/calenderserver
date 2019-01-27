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

app.post('/addfile', (req, res) => {
   const {id} = req.body;
    if (!req.files) {

        res.json("File Not Found!");
    } else {
        let uploadFile = req.files.file;
        uploadFile.mv(`${__dirname}/public/files/${uploadFile.name}`, (err) => {
            if (err) {
                return res.status(500).send(err)
            }

            //DATABASE CODE GOES HERE
            db('users').where('id',id).update({filename:uploadFile.name}).then(console.log).catch(error => res.status(400).json('Unable to add file'));
            res.json('success');

        })
    }
});









app.listen(3000, ()=> {
  console.log(`app is running on port 3000`);
})