const handleParsed = (req, res, db) => {
  const { id, parsed} = req.query;

  if (!parsed) {
    return res.status(400).json("no parsed string sent");
  }
    
   const jsonString = JSON.parse(parsed);
   let courseString = jsonString[0];
   let freetimesString = jsonString[1];
    db('users').where('id',id).update({
        courses:courseString,
        freetimes: freetimesString
        
    }).then(console.log).catch(error => res.status(400).json('Unable to add file'));
            res.json('success');
   
}

module.exports = {
  handleParsed: handleParsed
};
