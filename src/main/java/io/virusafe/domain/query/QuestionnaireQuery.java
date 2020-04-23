package io.virusafe.domain.query;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.virusafe.domain.dto.AnswerQueryDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class QuestionnaireQuery implements SearchQueryGenerator {
    private static final String FIELD_GEO_POINT = "geoPoint";

    private TimeSlot timeSlot;
    private AnswerQuery answerQuery;
    private List<Point> polygonPoints;

    /**
     * Generate a new ElasticSearch SearchSourceBuilder with the given conditions for timeslot,
     * location polygon points and questionnaire answers.
     *
     * @return the generated SearchSourceBuilder
     */
    public SearchSourceBuilder generateQuery() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (timeSlot != null) {
            queryBuilder.must().add(timeSlot.generateQuery());
        }
        if (polygonPoints != null && !polygonPoints.isEmpty()) {
            List<GeoPoint> geoPoints = polygonPoints.stream().map(point -> new GeoPoint(point.getLat(), point.getLon()))
                    .collect(Collectors.toList());
            queryBuilder.must().add(QueryBuilders.geoPolygonQuery(FIELD_GEO_POINT, geoPoints));

        }
        if (answerQuery != null) {
            queryBuilder.must().add(answerQuery.generateQuery());
        }
        return new SearchSourceBuilder().query(queryBuilder);
    }

    public interface QueryGenerator {
        /**
         * Generate a new QueryBuilder using field properties.
         *
         * @return the generated QueryBuilder
         */
        QueryBuilder generateQuery();
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Point {
        private Double lat;
        private Double lon;
    }

    @Data
    @Builder
    public static class TimeSlot implements QueryGenerator {
        private static final String FIELD_SERVER_TIME = "serverTime";

        private String gt;
        private String lt;
        private String gte;
        private String lte;

        /**
         * Generate a time slot query builder, setting conditions on serverTime,
         * inclusive/exclusive depending on which of the lt/lte and gt/gte fields are set.
         *
         * @return the timeslot QueryBuilder
         */
        @Override
        public QueryBuilder generateQuery() {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(FIELD_SERVER_TIME);
            if (gt != null) {
                rangeQueryBuilder.gt(gt);
            }
            if (gte != null) {
                rangeQueryBuilder.gte(gte);
            }
            if (lt != null) {
                rangeQueryBuilder.lt(lt);
            }
            if (lte != null) {
                rangeQueryBuilder.lte(lte);
            }
            return rangeQueryBuilder;
        }
    }

    @Data
    @Builder
    @JsonDeserialize(using = AnswerQueryDeserializer.class)
    public static class AnswerQuery implements QueryGenerator {
        private Operation operation;

        /**
         * Generate a QuestionnaireAnswer query builder, with different implementations based on the underlying logic.
         *
         * @return the generated QueryBuilder
         */
        @Override
        public QueryBuilder generateQuery() {
            return operation.generateQuery();
        }
    }

    public interface Operation extends QueryGenerator {
        //Marker Interface
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class AndOperation implements Operation {
        private List<Operation> subOperations;

        /**
         * Generate an and-condition QueryBuilder, marking all sub-operations in a `must` array.
         *
         * @return the generated QueryBuilder
         */
        @Override
        public QueryBuilder generateQuery() {
            BoolQueryBuilder andBuilder = QueryBuilders.boolQuery();
            andBuilder.must()
                    .addAll(subOperations.stream().map(QueryGenerator::generateQuery).collect(Collectors.toList()));
            return andBuilder;
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class OrOperation implements Operation {
        private List<Operation> subOperations;
        private String minMatch;

        /**
         * Generate a or-condition QueryBuilder, marking all sub-operation in a `should` array.
         *
         * @return the generated QueryBuilder
         */
        @Override
        public QueryBuilder generateQuery() {
            BoolQueryBuilder orBuilder = QueryBuilders.boolQuery();
            orBuilder.should()
                    .addAll(subOperations.stream().map(QueryGenerator::generateQuery).collect(Collectors.toList()));
            if (minMatch != null) {
                orBuilder.minimumShouldMatch(minMatch);
            }
            return orBuilder;
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class AnswerOperation implements Operation {
        private static final String ANSWERS = "answers";
        private static final String ANSWERS_QUESTION_ID = "answers.questionId";
        private static final String ANSWERS_ANSWER = "answers.answer";

        private String questionId;
        private String answer;

        /**
         * Generate an answer query builder, applying the given questionId and answer conditions on the
         * nested answers field.
         *
         * @return the generated QueryBuilder
         */
        @Override
        public QueryBuilder generateQuery() {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            QueryBuilder matchQuestionId = QueryBuilders.termQuery(ANSWERS_QUESTION_ID, questionId);
            boolQueryBuilder.must().add(matchQuestionId);
            QueryBuilder matchAnswer = QueryBuilders.matchQuery(ANSWERS_ANSWER, answer);
            boolQueryBuilder.must().add(matchAnswer);
            return QueryBuilders.nestedQuery(ANSWERS, boolQueryBuilder, ScoreMode.None);
        }
    }

}