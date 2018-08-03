package pers.zxm.cache2j.subscribe;

import java.util.Objects;

/**
 * @Author
 * @Description
 * @Date Create in 上午 10:34 2018/8/3 0003
 */
public class Message<T> {
    private String messageId;
    private String tag;
    private Long timestamp;
    private T payload;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T t) {
        this.payload = t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message<?> message = (Message<?>) o;
        return Objects.equals(messageId, message.messageId) &&
                Objects.equals(tag, message.tag) &&
                Objects.equals(timestamp, message.timestamp) &&
                Objects.equals(payload, message.payload);
    }

    @Override
    public int hashCode() {

        return Objects.hash(messageId, tag, timestamp, payload);
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", tag='" + tag + '\'' +
                ", timestamp=" + timestamp +
                ", payload=" + payload +
                '}';
    }
}
