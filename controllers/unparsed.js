const handleUnparsedGet = (req, res, db) => {
    
  db.select('filename').from('users').whereNotNull('filename')
    .then(filename => {
      if (filename.length) {
        res.json(filename[0])
      } else {
        res.status(400).json('Not found')
      }
    })
    .catch(err => res.status(400).json('error getting unparsed'))
}

module.exports = {
  handleProfileGet
}