//var result1 = httpLoader.loadInBackground("http://localhost:33333/test.xml?_ox", 300, cache("test_key", 300));
var result2 = httpLoader.load("http://localhost:33333/test.xml?_ox", 100);
var i = result2.evaluateToString("/PAGE","ERROR");
//write(escape(result1));
write(i);
//writeXml(result2);
//httpLoader.wait(result1);
