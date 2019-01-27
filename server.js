const express = require('express');
const bodyParser = require('body-parser');
const bcrypt = require('bcrypt-nodejs');
const fileupload = require("express-fileupload");
const cors = require('cors');
const knex = require('knex');

const register = require('./controllers/register');
const signin = require('./controllers/signin');
const profile = require('./controllers/profile');


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
app.get('/profile/:id', (req, res) => { profile.handleProfileGet(req, res, db)})

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


app.get('/unparsedfiles', (req,res)=>{
   db('users').returning('*').select('*').then(response =>{
       res.json(response);
   }).catch(error=>res.status(400).json('Unable to fetch markers information'))
})

app.post('/', (req,res)=>{
    const {filename} = req.body;

    if(!req.files){
        
        res.json("File Not Found!");
    }else{
    let uploadFile = req.files.file;
       uploadFile.mv(`${__dirname}/public/files/${filename}`,  (err) => {
        if (err) {
            return res.status(500).send(err)
        }
        
    //DATABASE CODE GOES HERE
        db('users').insert({
        
        filename:filename
    
    }).then(console.log).catch(error=>res.status(400).json('Unable to add marker'));
    res.json('success');

    }) 
}  
})


app.listen(process.env.PORT || 3000, ()=> {
  console.log(`app is running on port ${process.env.PORT}`);
})