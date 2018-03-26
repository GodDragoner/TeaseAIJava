sendMessage('Hello');
delVar("test");
setTempVar("test", false);
if(getVar("test")) {
    sendMessage('Test');
}
setTempVar("test", true);
if(getVar("test")) {
    sendMessage('Test2');
}
run('test.js');
sendMessage(myFunction(1,2) + "");