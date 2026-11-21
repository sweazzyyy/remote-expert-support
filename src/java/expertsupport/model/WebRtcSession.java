package expertsupport.model;

import expertsupport.model.annotation.Annotation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Сущность SESSION — WebRTC-сеанс по назначению.
 * AR-метки принадлежат сеансу (композиция), протокол создаётся при закрытии.
 */
public class WebRtcSession {
    private final int id;
    private final ExpertAssignment assignment;
    private final String roomId;
    private final String quality;
    private final LocalDateTime establishedAt;
    private final List<Annotation> annotations = new ArrayList<>();
    private Protocol protocol;

    WebRtcSession(int id, ExpertAssignment assignment) {
        this.id = id;
        this.assignment = assignment;
        this.roomId = "room_webrtc_" + assignment.getIncident().getId() + "_" + id;
        this.quality = "HD_1080P_60FPS";
        this.establishedAt = LocalDateTime.now();
    }

    /**
     * Операция 5 «Нанести AR-метку на видеокадр».
     * В процедурной версии — функция process_ar_annotation().
     */
    public Annotation addAnnotation(int annotationId, String toolType, double x1, double y1, double x2, double y2,
                                    String color) {
        if (protocol != null) {
            throw new IllegalStateException("Сеанс " + id + " уже закрыт");
        }
        Annotation annotation = Annotation.create(annotationId, toolType, x1, y1, x2, y2, color);
        annotations.add(annotation);
        return annotation;
    }

    /**
     * Операция 6 «Оформить протокол заключения».
     * В процедурной версии — функция generate_consultation_protocol().
     */
    public Protocol close(int protocolId, String expertNotes) {
        if (protocol != null) {
            throw new IllegalStateException("Протокол по сеансу " + id + " уже оформлен");
        }
        protocol = new Protocol(protocolId, this, expertNotes);
        assignment.getIncident().markResolved();
        return protocol;
    }

    public int getId() {
        return id;
    }

    public ExpertAssignment getAssignment() {
        return assignment;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getQuality() {
        return quality;
    }

    public LocalDateTime getEstablishedAt() {
        return establishedAt;
    }

    public List<Annotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public Protocol getProtocol() {
        return protocol;
    }
}
