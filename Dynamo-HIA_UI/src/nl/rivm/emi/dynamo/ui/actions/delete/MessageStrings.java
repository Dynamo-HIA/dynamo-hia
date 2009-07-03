package nl.rivm.emi.dynamo.ui.actions.delete;

public class MessageStrings {
private String messageBoxText;
private String messagePart1;
private String messagePart2;
private String messageNoGo;
public MessageStrings(String messageBoxText, String messagePart1, String messagePart2,
		String messageNoGo) {
	super();
	this.messageBoxText = messageBoxText;
	this.messagePart1 = messagePart1;
	this.messagePart2 = messagePart2;
	this.messageNoGo = messageNoGo;
}
public String getMessageBoxText() {
	return messageBoxText;
}
public String getMessagePart1() {
	return messagePart1;
}
public String getMessagePart2() {
	return messagePart2;
}
public String getMessageNoGo() {
	return messageNoGo;
}

}
