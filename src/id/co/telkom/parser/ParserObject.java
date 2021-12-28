package id.co.telkom.parser;

@SuppressWarnings("rawtypes")
public class ParserObject {
	private String cynapseComment;
	private Class parser;
	private Class initiator;
	
	public ParserObject(Class parser, Class initiator, String comment){
		setCynapseComment(comment);
		setInitiator(initiator);
		setParser(parser);
	}
	
	public String getComment() {
		return cynapseComment;
	}
	public void setCynapseComment(String cynapseComment) {
		this.cynapseComment = cynapseComment;
	}
	public Class getParser() {
		return parser;
	}
	public void setParser(Class parser) {
		this.parser = parser;
	}
	public Class getInitiator() {
		return initiator;
	}
	public void setInitiator( Class initiator) {
		this.initiator = initiator;
	}
	
}
