package e_learning.services.ServiceImpl;

import e_learning.DTO.EvaluationDto;
import e_learning.entity.*;
import e_learning.mappers.mappersImpl.EvaluationMapper;
import e_learning.repositories.EvaluationRepository;
import e_learning.repositories.ResponseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.kernel.pdf.PdfDocument;
import java.io.ByteArrayOutputStream;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@AllArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final EvaluationMapper evaluationMapper;
    private final PermissionService permissionService;
    private final ResponseRepository responseRepository;

    @Transactional
    public EvaluationDto createOrUpdateEvaluation(EvaluationDto evaluationDto, UserApp userApp) throws AccessDeniedException {
        Evaluation evaluation = evaluationMapper.toEntity(evaluationDto);
        evaluation = evaluationRepository.save(evaluation);

        // Calculate the score
        double scorePercentage = calculateScore(evaluation);
        evaluation.setScore((long) scorePercentage);

        // Save the updated evaluation with the score
        evaluation = evaluationRepository.save(evaluation);

        return evaluationMapper.toDto(evaluation);
    }

    public EvaluationDto getEvaluationById(Long id) {
        Evaluation evaluation = evaluationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Evaluation not found"));
           // Calculate the score
        double scorePercentage = calculateScore(evaluation);
        evaluation.setScore((long) scorePercentage);

        // Save the updated evaluation with the score
        evaluation = evaluationRepository.save(evaluation);
        return evaluationMapper.toDto(evaluation);
    }

    @Transactional
    public void deleteEvaluation(Long id) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evaluation not found"));

        // Remove Evaluation from Quiz and Participant to trigger orphanRemoval
        Quiz quiz = evaluation.getQuiz();
        if (quiz != null) {
            quiz.getEvaluations().remove(evaluation);
        }

        Participant participant = evaluation.getParticipant();
        if (participant != null) {
            participant.getEvaluations().remove(evaluation);
        }

        // No need to manually handle Responses due to CascadeType.REMOVE
        evaluationRepository.delete(evaluation);

        // Remove the existence check as it might not work within the same transaction
        System.out.println("Evaluation deleted successfully");
    }

    private double calculateScore(Evaluation evaluation) {
        List<Response> responses = evaluation.getResponses();
        if (responses == null || responses.isEmpty()) {
            return 0;
        }

        long correctCount = responses.stream()
                .filter(response -> response.getOption() != null && response.getOption().stream().allMatch(Option::isCorrect))
                .count();

        return (double) correctCount / responses.size() * 100;
    }

    public EvaluationDto getEvaluationResult(Long quizId, Long participantId) {
        List<Evaluation> evaluations = evaluationRepository.findLatestByQuizIdAndParticipantUserId(quizId, participantId);

        if (evaluations.isEmpty()) {
            throw new EntityNotFoundException("Evaluation not found");
        }
        // For example, return the latest evaluation or handle as needed
        Evaluation evaluation = evaluations.get(0); // or any other logic to select the appropriate one
        return evaluationMapper.toDto(evaluation);
    }



    public byte[] generateEvaluationPdf(Long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId).orElseThrow(() -> new IllegalArgumentException("Evaluation not found"));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add evaluation details to the PDF
        Participant participant = evaluation.getParticipant();
        document.add(new Paragraph("First Name: " + participant.getFirstName()));
        document.add(new Paragraph("Last Name: " + participant.getLastName()));
        document.add(new Paragraph("Time Taken: " + evaluation.getTimeTaken() + " seconds"));
        document.add(new Paragraph("Score: " + evaluation.getScore()));
        document.add(new Paragraph("Date: " + evaluation.getCreatedAt()));

        // Add each response with question and option
        for (Response response : evaluation.getResponses()) {
            Question question = response.getQuestion();
            List<Option> options = response.getOption();
            document.add(new Paragraph("Question: " + question.getText()));
            for (Option option : options) {
                document.add(new Paragraph("Option: " + option.getOptionText()));
            }
        }

        document.close();
        return byteArrayOutputStream.toByteArray();
    }
}
