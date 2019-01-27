const handleUnparsedGet = (req, res, db) => {
 
  db.select('filename').from('users').where({filename:!null,parsedfile:null})
    .then(filename => {
      if (filename.length) {
        res.json(filename)
      } else {
        res.status(400).json('Not found')
      }
    })
    .catch(err => res.status(400).json('error getting user'))
}

module.exports = {
  handleUnparsedGet:handleUnparsedGet
}