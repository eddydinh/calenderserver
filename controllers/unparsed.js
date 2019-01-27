const handleUnparsedGet = (req, res, db) => {
 
  db.select('filename','courses','id').from('users').whereNotNull("filename")
    .then(filename => {
      if (filename.length) {
          let responseString = '';
          for(i=0; i<filename.length; i++){
              responseString +=filename[i].filename;
              responseString += "," + filename[i].courses;
              responseString += "," + filename[i].id;
              responseString += "#";
          }
          
          res.send(responseString);
       
      } else {
        res.status(400).json('Not found')
      }
    })
    .catch(err => res.status(400).json('error getting filename'))
}

module.exports = {
  handleUnparsedGet:handleUnparsedGet
}