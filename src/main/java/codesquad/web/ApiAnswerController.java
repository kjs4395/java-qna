package codesquad.web;

import codesquad.DeleteException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.security.LoginUser;
import codesquad.service.AnswerService;
import codesquad.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @Resource(name = "answerService")
    private AnswerService answerService;

    @GetMapping("/{id}")
    public AnswerDto show(@PathVariable long questionId, @PathVariable long id) {

        try {
            return answerService.findById(id);
        } catch (DeleteException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionId, @RequestBody AnswerDto answerDto) {
        Answer answer = qnaService.addAnswer(questionId, answerDto.toAnswer());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + answer.generateUrl()));

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public void update(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long id, @RequestBody AnswerDto answerDto) {
        qnaService.updateAnswer(loginUser, id, answerDto.toAnswer());
    }

    @DeleteMapping("/{id}")
    public void delete(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long id) {
        qnaService.deleteAnswer(loginUser, id);
    }
}
