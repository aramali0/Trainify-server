package e_learning.services.ServiceImpl;

import e_learning.DTO.QuestionDto;
import e_learning.DTO.QuizDto;
import e_learning.DTO.ResponseDto;
import e_learning.entity.*;
import e_learning.mappers.mappersImpl.QuestionMapper;
import e_learning.mappers.mappersImpl.QuizMapper;
import e_learning.mappers.mappersImpl.ResponseMapper;
import e_learning.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final SectionRepository sectionRepository;
    private final QuizMapper quizMapper;
    private final QuestionMapper questionMapper;
    private final QuestionRepository questionRepository;
    private final ParticipantRepository participantRepository;
    private final ResponseMapper responseMapper;
    private final ResponseRepository responseRepository;
    private final EvaluationRepository evaluationRepository;
    private final ActionApprovalRepository actionApprovalRepository;

    public QuizDto addQuizToSection(Long sectionId, QuizDto quizDto,UserApp userApp) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new EntityNotFoundException("Section not found"));


        Quiz quiz = quizMapper.toEntity(quizDto);
        quiz.setSection(section);
        quiz.setApproved(true);
        Quiz savedQuiz = quizRepository.save(quiz);


        if(userApp instanceof ChargeFormation)
        {

            ActionApproval actionApproval = new ActionApproval();
            actionApproval.setApproved(false);
            actionApproval.setObjectId(section.getId());
            actionApproval.setActionType("QUIZ");
            actionApproval.setCreatedDate(LocalDateTime.now());
            actionApproval.setChargeFormationId(userApp.getUserId());
            actionApproval.setEntrepriseId(section.getSession().getCour().getEntreprise().getId());

            actionApprovalRepository.save(actionApproval);
            quiz.setApproved(false);
            quizRepository.save(quiz);

        }

        return quizMapper.toDto(savedQuiz);
    }

    public List<QuizDto> getQuizzesBySection(Long sectionId) {
        List<Quiz> quizzes = quizRepository.findBySectionId(sectionId);
        return quizzes.stream().filter(Quiz::isApproved).map(quizMapper::toDto).collect(Collectors.toList());
    }

    public QuizDto updateQuiz(Long quizId, QuizDto quizDto) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));

        quiz.setTitle(quizDto.title());
        quiz.setType(quizDto.type());
        // Update any other fields as necessary
        Quiz updatedQuiz = quizRepository.save(quiz);

        return quizMapper.toDto(updatedQuiz);
    }

    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));

        quizRepository.delete(quiz);
    }

    public QuestionDto addQuestionToQuiz(Long quizId, QuestionDto questionDto) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));
        Question question = questionMapper.toEntity(questionDto);
        Question newQuestion =  questionRepository.save(question);
        quiz.getQuestions().add(newQuestion);
        quizRepository.save(quiz);
        newQuestion.setQuiz(quiz);
        Question question1 = questionRepository.save(newQuestion);
        return questionMapper.toDto(question1);
    }



    @Transactional
    public void submitQuiz(Long quizId, List<ResponseDto> responseDtos, Long participantId,Long timeTaken) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));

        Evaluation evaluation = new Evaluation();
        evaluation.setParticipant(participant);
        evaluation.setType("Quiz");
        evaluation.setCreatedAt(new Date());
        evaluation.setTimeTaken(timeTaken);
        evaluation.setQuiz(quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found")));
        evaluationRepository.save(evaluation);

        int correctAnswers = 0;
        int totalQuestions = responseDtos.size();

        for (ResponseDto responseDto : responseDtos) {
            Response response = responseMapper.toEntity(responseDto);
            response.setEvaluation(evaluation);

            // Check if the selected option is correct
            if (response.getOption() != null && response.getOption().stream().allMatch(Option::isCorrect)) {
                correctAnswers++;
            }

            // Save the response
            responseRepository.save(response);
        }

        // Calculate the score out of 100
        long score = Math.round((correctAnswers / (double) totalQuestions) * 100);

        evaluation.setScore(score);
        evaluationRepository.save(evaluation);
    }


    public boolean hasParticipantSubmittedQuiz(Long quizId, Long participantId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Section not found"));
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));

        Optional<Evaluation> evaluation = evaluationRepository.findByQuizIdAndParticipantUserId(quizId, participantId);

        return evaluation.isPresent();
    }

    public List<QuizDto> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        return quizzes.stream().filter(Quiz::isApproved).map(quizMapper::toDto).collect(Collectors.toList());
    }
}
