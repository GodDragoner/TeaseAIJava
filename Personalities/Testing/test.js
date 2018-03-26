sendMessage("Loaded file");

answer = sendInput("Hey %tree%");
while (true) {
    if (answer.matchesRegexLowerCase("hey([ ]|$)", "hello([ ]|$)", "hi([ ]|$)")) {
        break;
    } else {
        sendMessage("What?");
        answer.loop();
    }
}

function myFunction(p1, p2) {
    return p1 * p2;              // The function returns the product of p1 and p2
}