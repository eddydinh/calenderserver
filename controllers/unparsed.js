const handleUnparsedGet = (req, res, db) => {
 
  db.select('*').from('users').where('id',1)
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