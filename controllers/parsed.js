const handleParsed = (req, res, db) => {
  const { id, parsed} = req.query;

  if (!parsed) {
    return res.status(400).json("no parsed string sent");
  }
    
  
    db('users').where('id',id).update({
        parsedfile:parsed
        
    }).then(console.log).catch(error => res.status(400).json('Unable to add file'));
            res.json('success');
   
}

module.exports = {
  handleParsed: handleParsed
};
