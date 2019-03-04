package org.sunbird.akka.core;

/**
 * An actionable message that can be routed from the Router actor
 * The targetActorName is the actor name.
 */
public final class Message {
    /**
     * The source actor from where this message is originating
     */
    private String sourceActorName;

    /**
     * The particular actor to which this is intended
     */
    private String targetActorName;

    /**
     * The operation we want the actor to perform with this payload
     */
    private String performOperation = null;

    /**
     * Use this optional field if you want to track the message
     */
    private String id = "";

    /**
     * Whether the message requires a response
     */
    private MessageOption msgOption = MessageOption.SEND_AND_FORGET;

    public enum MessageOption {
        SEND_AND_FORGET ("tell"),
        GET_BACK_RESPONSE ("ask");

        private String optionType;

        MessageOption(String s) {
            optionType = s;
        }

        public MessageOption getOptionType() {
            return MessageOption.valueOf(optionType);
        }
    }

    /**
     * The payload to send to the consumer
     */
    private Object payload = null;

    private Message() {}

    public Message(String msgForActorName) {
        targetActorName = msgForActorName;
    }

    public Message(String srcAbsName, String tgtAbsoluteName) {
        sourceActorName = srcAbsName;
        targetActorName = tgtAbsoluteName;
    }

    public void setSourceActorName(String srcAbsName) { this.sourceActorName = srcAbsName; }

    public String getSourceActorName() { return sourceActorName; }

    public void setTargetActorName(String tgtAbsName) { this.targetActorName = tgtAbsName; }

    public String getTargetActorName() {
        return targetActorName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String getPerformOperation() {
        return performOperation;
    }

    public void setPerformOperation(String performOperation) {
        this.performOperation = performOperation;
    }

    public MessageOption getMsgOption() {
        return msgOption;
    }

    public void setMsgOption(MessageOption msgOption) {
        this.msgOption = msgOption;
    }

    @Override
    public String toString() {
        StringBuilder formattedStr = new StringBuilder();
        formattedStr.append("Actor:").append(getTargetActorName());
        formattedStr.append(" Operation:").append(getPerformOperation());
        if (getPayload() != null) {
            formattedStr.append(" Payload:").append(getPayload().toString());
        }
        return formattedStr.toString();
    }
}
