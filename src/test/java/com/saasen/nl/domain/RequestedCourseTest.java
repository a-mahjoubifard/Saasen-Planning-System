package com.saasen.nl.domain;

import static com.saasen.nl.domain.CourseTestSamples.*;
import static com.saasen.nl.domain.RequestedCourseTestSamples.*;
import static com.saasen.nl.domain.TrainingRequestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.saasen.nl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RequestedCourseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RequestedCourse.class);
        RequestedCourse requestedCourse1 = getRequestedCourseSample1();
        RequestedCourse requestedCourse2 = new RequestedCourse();
        assertThat(requestedCourse1).isNotEqualTo(requestedCourse2);

        requestedCourse2.setId(requestedCourse1.getId());
        assertThat(requestedCourse1).isEqualTo(requestedCourse2);

        requestedCourse2 = getRequestedCourseSample2();
        assertThat(requestedCourse1).isNotEqualTo(requestedCourse2);
    }

    @Test
    void trainingRequestTest() {
        RequestedCourse requestedCourse = getRequestedCourseRandomSampleGenerator();
        TrainingRequest trainingRequestBack = getTrainingRequestRandomSampleGenerator();

        requestedCourse.setTrainingRequest(trainingRequestBack);
        assertThat(requestedCourse.getTrainingRequest()).isEqualTo(trainingRequestBack);

        requestedCourse.trainingRequest(null);
        assertThat(requestedCourse.getTrainingRequest()).isNull();
    }

    @Test
    void courseTest() {
        RequestedCourse requestedCourse = getRequestedCourseRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        requestedCourse.setCourse(courseBack);
        assertThat(requestedCourse.getCourse()).isEqualTo(courseBack);

        requestedCourse.course(null);
        assertThat(requestedCourse.getCourse()).isNull();
    }
}
