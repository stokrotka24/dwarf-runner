package messages;

public interface IMessageBuilder {
    <T> void addField(final String label, final T value);

    Message<GenericMsgContent> get();

    void setType(final MessageType type);
}
