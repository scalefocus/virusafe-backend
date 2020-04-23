package io.virusafe.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireQueryDTO {

    private TimeSlotDTO timeSlot;
    private AnswerQueryDTO answerQuery;
    private List<PointDTO> polygonPoints;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PointDTO {
        private Double lat;
        private Double lon;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlotDTO {
        private String gt;
        private String lt;
        private String gte;
        private String lte;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonDeserialize(using = AnswerQueryDeserializer.class)
    public static class AnswerQueryDTO {
        private OperationDTO operation;
    }

    public interface OperationDTO {
        //Marker Interface
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AndOperationDTO implements ComposableOperationDTO {
        private List<OperationDTO> subOperations = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrOperationDTO implements ComposableOperationDTO {
        private List<OperationDTO> subOperations = new ArrayList<>();
        private String minMatch;
    }

    public interface ComposableOperationDTO extends OperationDTO {
        /**
         * Get all sub operations of the composable operation.
         *
         * @return the list of sub operations
         */
        List<OperationDTO> getSubOperations();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerDTO implements OperationDTO {
        private String questionId;
        private String answer;
    }

}