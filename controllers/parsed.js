const handleParsed = (req, res, db) => {
  const {parsed,id} = req.body;

  if (!parsed) {
    return res.status(400).json("no parsed string sent");
  }
    
   const jsonString = JSON.parse(parsed);
   let courseString = jsonString[0];
   let freetimesString = jsonString[1];
  console.log(courseString, freetimesString);
   
}

module.exports = {
  handleParsed: handleParsed
};
