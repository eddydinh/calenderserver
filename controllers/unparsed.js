const handleUnparsedGet = (req, res, db) => {

  db.where({filename: 'ical.ics'}).select('filename')
    .then(filename => {
       console.log(filename);
      if (filename.length) {
       
        res.json(filename[0])
      } else {
        res.status(400).json('Not found')
      }
    })
    .catch(err => res.status(400).json('error getting unparsed'))
}

module.exports = {
  handleUnparsedGet
}