const handleUnparsedGet = (req, res, db) => {
 
  db.select('filename','parsedfile','id').from('users').whereNotNull("filename")
    .then(filename => {
      if (filename.length) {
          let responseString = '';
          for(i=0; i<filename.length; i++){
              responseString +=filename[i].filename;
              responseString += "," + filename[i].parsedfile;
              responseString += "," + filename[i].id;
              responseString += "|";
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