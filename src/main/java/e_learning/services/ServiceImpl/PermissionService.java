package e_learning.services.ServiceImpl;

import e_learning.DTO.QuestionDto;
import e_learning.DTO.QuizDto;
import e_learning.entity.*;
import e_learning.mappers.mappersImpl.QuestionMapper;
import e_learning.mappers.mappersImpl.QuizMapper;
import e_learning.repositories.*;
import e_learning.security.SecurityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PermissionService {

    private final CourRepository courRepository;
    private final SectionRepository sectionRepository;
    private final SessionRepository sessionRepository;
    private final QuestionRepository questionRepository;
    private final ResponseRepository responseRepository;
    private final LibraryRepository libraryRepository;
    private final QuestionMapper questionMapper;
    private final QuizMapper quizMapper;


    public boolean canCreateSection(Long sessionId, UserApp currentUser) {
        Session session = sessionRepository.findById(sessionId).orElse(null);

        if (session == null) {
            return false;
        }
        Cour cour = session.getCour();
        // Check if the current user is the responsableFormation or a formateur of the course
        return cour.getEntreprise().getResponsableFormations().stream().anyMatch( (r) -> r.getUserId().equals(currentUser.getUserId())) ||
                cour.getFormateurs().stream().anyMatch(f -> f.getUserId().equals(currentUser.getUserId()));
    }


    public boolean canAddResourceOrEvaluation(Long sectionId,UserApp currentUser) {
        Section section = sectionRepository.findById(sectionId).orElse(null);

        if (section == null) {
            return false;
        }

        Session session = section.getSession();
        Cour cour = session.getCour();
        // Check if the current user is the responsableFormation or a formateur of the course
        return cour.getEntreprise().getResponsableFormations().stream().anyMatch( (r) -> r.getUserId().equals(currentUser.getUserId())) ||
                cour.getFormateurs().stream().anyMatch(f -> f.getUserId().equals(currentUser.getUserId()));
    }


    public boolean canManageQuestion(Long questionId,UserApp currentUser) {
        Question question = questionRepository.findById(questionId).orElse(null);

        if (question == null) {
            return false;
        }

        Quiz quiz = question.getQuiz();
        Section section = quiz.getSection();
        Session session = section.getSession();
        Cour cour = session.getCour();
        // Check if the current user is the responsableFormation or a formateur of the course
        return cour.getEntreprise().getResponsableFormations().stream().anyMatch( (r) -> r.getUserId().equals(currentUser.getUserId())) ||
                cour.getFormateurs().stream().anyMatch(f -> f.getUserId().equals(currentUser.getUserId()));
    }

    public boolean canManageResponse(Long responseId,UserApp currentUser) {
        Response response = responseRepository.findById(responseId).orElse(null);
        if (response == null) {
            return false;
        }

        Question question = response.getQuestion();
        Quiz quiz = question.getQuiz();
        Section section = quiz.getSection();
        Session session = section.getSession();
        Cour cour = session.getCour();
        // Check if the current user is the responsableFormation or a formateur of the course
        assert cour.getClasses() != null;
        return cour.getClasses().stream().map(f -> f.getParticipants().stream().anyMatch(p -> p.getUserId().equals(currentUser.getUserId()))).findFirst().orElse(false);
    }

    public boolean canCreateOption(Long questionId, UserApp currentUser) {
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null) {
            return false;
        }

        Quiz quiz = question.getQuiz();
        Section section = quiz.getSection();
        Session session = section.getSession();
        Cour cour = session.getCour();
        // Check if the current user is the responsableFormation or a formateur of the course
        return cour.getEntreprise().getResponsableFormations().stream().anyMatch( (r) -> r.getUserId().equals(currentUser.getUserId())) ||
                cour.getFormateurs().stream().anyMatch(f -> f.getUserId().equals(currentUser.getUserId()));
    }

    public boolean canAddResourceOrEvaluationToLibrary(Long libraryId, UserApp currentUser) {

        Library library = libraryRepository.findById(libraryId).orElse(null);
        if (library == null) {
            return false;
        }

        Cour cour = library.getCour();
        // Check if the current user is the responsableFormation or a formateur of the course
        return cour.getEntreprise().getResponsableFormations().stream().anyMatch( (r) -> r.getUserId().equals(currentUser.getUserId())) ||
                cour.getFormateurs().stream().anyMatch(f -> f.getUserId().equals(currentUser.getUserId())) || (currentUser.getRoles().stream()
                .anyMatch(role -> role != null && "ADMIN".equalsIgnoreCase(String.valueOf(role.getRole())))) ||
                currentUser.getRoles().stream().anyMatch(role -> role != null && "CHARGE".equalsIgnoreCase(String.valueOf(role.getRole())));
    }
}
