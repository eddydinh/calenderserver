const handleUnparsedGet = (req, res, db) => {
 
  db.whereNotNull('filename')
    .then(user => {
      if (user.length) {
        res.json(user[0])
      } else {
        res.status(400).json('Not found')
      }
    })
    .catch(err => res.status(400).json('error getting user'))
}

module.exports = {
  handleUnparsedGet:handleUnparsedGet
}