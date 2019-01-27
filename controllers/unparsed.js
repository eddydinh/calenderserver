const handleUnparsedGet = (req, res, db) => {

  db.select('*').from('users').where('filename','ical.ics')
    .then(user => {
      if (user.length) {
        console.log(user);
        res.json(user[0])
      } else {
        res.status(400).json('Not found')
      }
    })
    .catch(err => res.status(400).json('error getting unparsed'))
}

module.exports = {
  handleUnparsedGet
}