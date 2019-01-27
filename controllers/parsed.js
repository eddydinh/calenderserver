const handleParsed = (req, res, db) => {
  const {parsed} = req.body;

  if (!parsed) {
    return res.status(400).json("no parsed string sent");
  }
 
    res.json(parsed);
}

module.exports = {
  handleParsed: handleParsed
};
