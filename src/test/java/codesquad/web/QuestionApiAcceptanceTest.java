package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class QuestionApiAcceptanceTest extends AcceptanceTest{
    private User loginUser;

    @Before
    public void setUp() {
        loginUser = defaultUser();
    }

    private QuestionDto createQuestionDto() {
        return new QuestionDto("새로운 질문입니다.", "새로운 질문 내용입니다.");
    }

    @Test
    public void create() {
        QuestionDto questionDto = createQuestionDto();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/api/question", questionDto, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        String location = response.getHeaders().getLocation().getPath();

        QuestionDto dbQuestion = basicAuthTemplate(loginUser).getForObject(location, QuestionDto.class);
        assertThat(dbQuestion, is(questionDto));

    }

    @Test
    public void showQuestion() {
        QuestionDto createQuestion = createQuestionDto();
        ResponseEntity<QuestionDto> response = basicAuthTemplate(loginUser).postForEntity("/api/question", createQuestion, QuestionDto.class);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = response.getHeaders().getLocation().getPath();

        QuestionDto insertedQuestion = template().getForObject(location, QuestionDto.class);

        assertThat(insertedQuestion, is(createQuestion));

    }

    @Test
    public void update() {
        QuestionDto newQuestion = createQuestionDto();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/api/question", newQuestion, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        String location = response.getHeaders().getLocation().getPath();

        QuestionDto insertedQuestion = template().getForObject(location, QuestionDto.class);

        QuestionDto updateQuestion = new QuestionDto(insertedQuestion.getId(), "수정된 제목", "수정된 내용");

        basicAuthTemplate(loginUser).put(location, updateQuestion);

        QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);

        assertThat(dbQuestion, is(updateQuestion));
    }

    @Test
    public void update_다른사람() {
        User anotherUser = findByUserId("sanjigi");
        QuestionDto newQuestion = createQuestionDto();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/api/question", newQuestion, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        String location = response.getHeaders().getLocation().getPath();

        QuestionDto insertedQuestion = template().getForObject(location, QuestionDto.class);

        QuestionDto updateQuestion = new QuestionDto(insertedQuestion.getId(), "수정된 제목", "수정된 내용");

        basicAuthTemplate(anotherUser).put(location, updateQuestion);

        QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);

        assertThat(dbQuestion, is(insertedQuestion));
    }

    @Test
    public void delete() {
        QuestionDto newQuestion = createQuestionDto();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/api/question", newQuestion, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        String location = response.getHeaders().getLocation().getPath();

        template().getForObject(location, QuestionDto.class);

        basicAuthTemplate(loginUser).delete(location);
        QuestionDto questionDto = template().getForObject(location, QuestionDto.class);
        assertNull(questionDto);

    }

    @Test
    public void delete_다른사람() {
        User anotherUser = findByUserId("sanjigi");
        QuestionDto newQuestion = createQuestionDto();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/api/question", newQuestion, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        String location = response.getHeaders().getLocation().getPath();

        template().getForObject(location, QuestionDto.class);

        basicAuthTemplate(anotherUser).delete(location);

        QuestionDto questionDto = template().getForObject(location, QuestionDto.class);
        assertThat(questionDto, is(newQuestion));
    }
}
