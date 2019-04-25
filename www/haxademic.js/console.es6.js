console.todo = function(msg){
  console.log( '%c %s %s %s ', 'color: yellow; background-color: black;', '--', msg, '--');
};

console.important = function(msg){
  console.log( '%c%s %s %s', 'color: brown; font-weight: bold; text-decoration: underline;', '--', msg, '--');
};
