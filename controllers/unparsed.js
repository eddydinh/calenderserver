const handleUnparsedGet = (req, res, db) => {
 
  db.select('filename','parsedfile').from('users').whereNotNull("filename")
    .then(filename => {
      if (filename.length) {
        res.json(filename)
      } else {
        res.status(400).json('Not found')
      }
    })
    .catch(err => res.status(400).json('error getting filename'))
}

module.exports = {
  handleUnparsedGet:handleUnparsedGet
}